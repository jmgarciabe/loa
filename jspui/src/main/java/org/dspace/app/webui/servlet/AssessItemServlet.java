/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.webui.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.dspace.app.webui.util.Authenticate;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.handle.HandleManager;
import org.dspace.loa.AssessParam;
import org.dspace.loa.Dimension;
import org.dspace.loa.Metric;

/**
 * Servlet for initiate parameters for LOA's assessment
 * 
 * @author Andres Salazar
 * @version $Revision$
 */
public class AssessItemServlet extends DSpaceServlet {
	/** Logger */
	private static Logger log = Logger.getLogger(AssessItemServlet.class);

	protected void doDSGet(Context context, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, SQLException, AuthorizeException {
		try {
			// Continue if logged in or startAuthentication finds a user;
			// otherwise it will issue redirect so just return

			if (context.getCurrentUser() != null || Authenticate.startAuthentication(context, request, response)) {
				// User is authenticated

				int itemID = UIUtil.getIntParameter(request, "item_id");
				String handle = request.getParameter("handle");
				boolean showError = false;

				// See if an item ID or Handle was passed in
				Item itemToAssess = null;

				if (itemID > 0) {
					itemToAssess = Item.find(context, itemID);

					showError = (itemToAssess == null);
				} else if ((handle != null) && !handle.equals("")) {
					// resolve handle
					DSpaceObject dso = HandleManager.resolveToObject(context, handle.trim());

					// make sure it's an ITEM
					if ((dso != null) && (dso.getType() == Constants.ITEM)) {
						itemToAssess = (Item) dso;
						showError = false;
					} else {
						showError = true;
					}
				}

				// Show initial parameters form if appropriate
				if (itemToAssess != null)
					showAssessForm(context, request, response, itemToAssess);
				else {
					if (showError)
						request.setAttribute("invalid.id", Boolean.TRUE);

					JSPManager.showJSP(request, response, "/tools/get-item-id.jsp");
				}

			}
		} catch (SQLException se) {
			// log.warn(LogManager.getHeader(context, "database_error", se
			// .toString()), se);
			JSPManager.showInternalError(request, response);
		}
	}
	
	/**
	 * Shows the item assessment parametrization form for a particular item
	 * 
	 * @param context
	 *            DSpace context
	 * @param request
	 *            the HTTP request containing posted info
	 * @param response
	 *            the HTTP response
	 * @param item
	 *            the item to assess
	 */
	private void showAssessForm(Context context, HttpServletRequest request, HttpServletResponse response, Item item)
			throws ServletException, IOException, SQLException, AuthorizeException {
		HttpSession session = request.getSession(false);

		if (session == null) {
			JSPManager.showInternalError(request, response);
		}

		request.setAttribute("item", item);
			

		/*
		 * Depending on the group that the authenticated user belongs to, the
		 * systemwill display the appropiate view. We have three groups by
		 * default to verifythe membership of the user (1-Administrator,
		 * 2-Expert People, 3-Students group).
		 */

		if (AuthorizeManager.isAdmin(context)) {
			// User is a repository administrator
			Vector<AssessParam> assessParam = AssessParam.findParam(context, item.getID(), 1);
			Vector<String> parameterizedMetrics = getMetrics(context, assessParam, 1);
			String processMessage = "";
			if(parameterizedMetrics.isEmpty()){
				processMessage = "Parameters must be set prior to assessment process. Please use the link below in order to set parameters for this assessment.";
			}
			session.setAttribute("LOA.adminAvailAssess", parameterizedMetrics);
			session.setAttribute("LOA.processMessage", processMessage);
			JSPManager.showJSP(request, response, "/tools/admin-assess.jsp");
			
		} else if (Group.isMember(context, 2)) {
			//User is an admin
			Vector<AssessParam> assessParam = AssessParam.findParam(context, item.getID(), 2);
			Vector<String> parameterizedDimensions = getDimensions(context, assessParam, 2);

			if (parameterizedDimensions.isEmpty())
				JSPManager.showJSP(request, response, "/tools/init-param-error.jsp");
			else {
				// show expert assessment page
				session.setAttribute("LOA.paramDimensions", parameterizedDimensions);
				JSPManager.showJSP(request, response, "/tools/qualify-expert.jsp");
			}
		} else if (Group.isMember(context, 3)) {
			// User is a student
			Vector<AssessParam> assessParam = AssessParam.findParam(context, item.getID(), 3);
			Vector<String> parameterizedMetrics = getMetrics(context, assessParam, 3); 
			
			if (parameterizedMetrics.isEmpty())
				JSPManager.showJSP(request, response, "/tools/init-param-error.jsp");
			else {
				// show student assessment page
				session.setAttribute("LOA.paraMetrics", parameterizedMetrics);
				JSPManager.showJSP(request, response, "/tools/student-survey.jsp");
			}

		} else {
			// User doesn't belong to any privileged group that is allowed to
			// use this function, show a dialog
			JSPManager.showAuthorizeError(request, response, null);
			throw new AuthorizeException("Only system admins are allowed to perform item assessment over the site");
		}

	}
	
	private Vector<String> getDimensions(Context context, Vector<AssessParam> assessParamList, int layerId) {
		Vector<String> ckDimensions = new Vector<String>();
		String dimensionName = null;

		if (assessParamList != null){
			for (int i = 0; i < assessParamList.size(); i++) {
				AssessParam assessParam = assessParamList.elementAt(i);
				if (assessParam.getLayerID() == layerId) {
					try {
						dimensionName = Dimension.findNameByID(context, assessParam.getDimID());
					} catch (SQLException e) {
						e.printStackTrace();
					}
					if (!ckDimensions.contains(dimensionName))
						ckDimensions.addElement(dimensionName);
				}
			}
		}

		return ckDimensions;
	}

	public static Vector<String> getMetrics(Context context, Vector<AssessParam> assessParamList, int layerId) {
		Vector<String> ckmetrics = new Vector<String>();
		String metricName = null;
		if (assessParamList != null) {
			for (int i = 0; i < assessParamList.size(); i++) {
				AssessParam assessParam = assessParamList.elementAt(i);
				if (assessParam.getLayerID() == layerId) {
					try {
						metricName = Metric.findNameByID(context, assessParam.getAssessMetricID());
					} catch (SQLException e) {
						e.printStackTrace();
					}
					if (!ckmetrics.contains(metricName))
						ckmetrics.addElement(metricName);
				}
			}
		}
		return ckmetrics;
	}

}