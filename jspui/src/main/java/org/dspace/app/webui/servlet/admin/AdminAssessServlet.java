/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.webui.servlet.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dspace.app.webui.servlet.DSpaceServlet;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.loa.AdminAsessHelper;
import org.dspace.loa.AdminAssessmentException;
import org.dspace.loa.AssessParam;
import org.dspace.loa.AssessResult;
import org.dspace.loa.Dimension;
import org.dspace.loa.Layer;
import org.dspace.loa.Metric;

/**
 * Servlet for perform the assessment logic of each of the administrator layer
 * metrics
 * 
 * @author Andres Salazar
 * @version $Revision$
 */
public class AdminAssessServlet extends DSpaceServlet {

	/** User selects show assessment result report */
	public static final int SHOW_RESULTS = 10;

	/** User selects delete all assessment data */
	public static final int DELETE_ASSESS = 15;

	protected void doDSGet(Context context, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, SQLException, AuthorizeException {
		HttpSession session = request.getSession(false);

		if (session == null) {
			JSPManager.showInternalError(request, response);
		}

		Vector<String> adminAvailAssess = (Vector<String>) session.getAttribute("LOA.adminAvailAssess");
		int itemID = UIUtil.getIntParameter(request, "item_id");
		String assess2Perform = request.getParameter("admin_assess");
		Item item = Item.find(context, itemID);
		String handle = item.getHandle();

		if (assess2Perform == null) {
			JSPManager.showInternalError(request, response);
		}

		AdminAsessHelper assessHelper = new AdminAsessHelper();
		AssessResult result = null;
		try {
			result = assessHelper.assess(assess2Perform, item, context);
		} catch (AdminAssessmentException aae) {
			System.out.print(aae.getMessage());
		}
		Metric.addAssessValue(context, result.getScore(), assess2Perform, 1, itemID);

		request.setAttribute("task_result", result);
		session.setAttribute("LOA.adminAvailAssess", adminAvailAssess);
		request.setAttribute("item", item);
		JSPManager.showJSP(request, response, "/tools/admin-assess.jsp");
	}

	protected void doDSPost(Context context, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, SQLException, AuthorizeException {

		HttpSession session = request.getSession(false);

		if (session == null) {
			JSPManager.showInternalError(request, response);
		}

		int action = UIUtil.getIntParameter(request, "action");
		int itemId = UIUtil.getIntParameter(request, "item_id");
		Item item = Item.find(context, itemId);

		switch (action) {

		case SHOW_RESULTS:

			AdminAsessHelper helper = new AdminAsessHelper();
			Vector<String> results = helper.getAssessmentResults(itemId, context);
			session.setAttribute("LOA.results", results);
			request.setAttribute("item", item);
			Vector<AssessParam> assessParamList = AssessParam.findParam(context, itemId);
			double adminIndex = helper.calculateLayerIndex(assessParamList, 1);
			double expIndex = helper.calculateLayerIndex(assessParamList, 2);
			double stdIndex = helper.calculateLayerIndex(assessParamList, 3);
			double totalIndex = helper.calculateTotalIndex(adminIndex, expIndex, stdIndex);
			int indexID = Layer.findIndexByItem(context, itemId);
			if (indexID > 0) {
				Layer.updateAssessIndexes(context, adminIndex, expIndex, stdIndex, totalIndex, indexID);
			} else {
				try {
					Layer.addAssessIndexes(context, itemId, adminIndex, expIndex, stdIndex, totalIndex);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DecimalFormat decimalFormat = new DecimalFormat("###");
			String admIndexString = adminIndex >= 0 ? decimalFormat.format(adminIndex * 100) : "";
			String expIndexString = expIndex >= 0 ? decimalFormat.format(expIndex * 100) : "";
			String stdIndexString = stdIndex >= 0 ? decimalFormat.format(stdIndex * 100) : "";
			Map<String, String> layerIndexes = new HashMap<String, String>();
			layerIndexes.put("1", admIndexString);
			layerIndexes.put("2", expIndexString);
			layerIndexes.put("3", stdIndexString);
			request.setAttribute("layerIndexes", layerIndexes);

			String totIndex = new DecimalFormat("##.##").format(totalIndex);
			request.setAttribute("totalIndex", totIndex);

			JSPManager.showJSP(request, response, "/tools/results-report.jsp");

			break;

		case DELETE_ASSESS:

			request.setAttribute("item", item);
			int layerDel = Layer.DeleteAssessIndexes(context, itemId);
			int dimDel = Dimension.DeleteAssessWeights(context, itemId);
			int metDel = Metric.deleteAssessValues(context, itemId);

			if ((layerDel + dimDel + metDel) > 0) {
				JSPManager.showJSP(request, response, "/tools/success-page.jsp");
			}
			break;

		}

	}

}
