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
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.dspace.app.webui.servlet.AssessItemServlet;
import org.dspace.app.webui.servlet.DSpaceServlet;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.handle.HandleManager;
import org.dspace.loa.AssessParam;
import org.dspace.loa.Dimension;
import org.dspace.loa.Metric;

/**
 * Servlet for setting up the initial parameters of each LO's assessment
 * 
 * @author Andres Salazar
 * @version $Revision$
 */
public class InitialParamServlet extends DSpaceServlet {
	/** User selects layers to be parameterized in assessment */
	public static final int LAYER_PARAM = 1;

	/** User selects metrics to take into account in assessment */
	public static final int METRIC_PARAM = 5;

	/** User's previous parameterized metrics vector */
	private Vector<AssessParam> assessParamList = null;

	/** User's selected metrics ID's vector */
	private Vector<String> ckMetricsID = null;

	/** Logger */
	private static Logger log = Logger.getLogger(InitialParamServlet.class);

	protected void doDSGet(Context context, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, SQLException, AuthorizeException {
		HttpSession session = request.getSession(false);

		if (session == null) {
			JSPManager.showInternalError(request, response);
		}

		int action = UIUtil.getIntParameter(request, "action");

		Item item = Item.find(context, UIUtil.getIntParameter(request, "item_id"));
		String handle = HandleManager.findHandle(context, item);

		Vector dimensionList = (Vector) session.getAttribute("LOA.dimensionList");
		Vector metricList = (Vector) session.getAttribute("LOA.metricList");
		Vector checkedDimensions = (Vector) session.getAttribute("LOA.ckDimensionList");

		/*
		 * Respond to submitted forms. Each form includes an "action" parameter
		 * indicating what needs to be done (from the constants above.)
		 */

		switch (action) {

		case LAYER_PARAM:

			String layer = request.getParameter("layer_name");

			if (layer == null) {
				JSPManager.showInternalError(request, response);
				System.out.println("Selected layer is not valid");
			}

			dimensionList = setDimensions(context, layer);
			metricList = setMetrics(context, layer);

			request.setAttribute("item", item);
			request.setAttribute("handle", handle);
			request.setAttribute("layer_name", layer);

			session.setAttribute("LOA.dimensionList", dimensionList);
			session.setAttribute("LOA.metricList", metricList);

			JSPManager.showJSP(request, response, "/tools/dim-display.jsp");

			break;

		case METRIC_PARAM:

			layer = request.getParameter("layer");
			int layerId = layer.equals("Administrator") ? 1 : layer.equals("Expert") ? 2 : layer.equals("Student") ? 3 : 0;

			String[] ckMetrics = request.getParameterValues("metrics");

			ckMetricsID = new Vector<String>();

			for (int i = 0; i < ckMetrics.length; i++) {

				String layerVarName = ckMetrics[i] + "_lay";
				String dimensioVarName = ckMetrics[i] + "_dim";
				String metricVarName = ckMetrics[i] + "_id";
				String lay_id = request.getParameter(layerVarName);
				String dimension_id = request.getParameter(dimensioVarName);
				String metric_id = request.getParameter(metricVarName);
				ckMetricsID.addElement(lay_id + "," + dimension_id + "," + metric_id);
			}

			assessParamList = AssessParam.findParam(context, item.getID(), layerId);
			checkedDimensions = verifyCheckedDimensions(context, ckMetricsID, assessParamList, layer);

			request.setAttribute("item", item);
			request.setAttribute("handle", handle);
			request.setAttribute("layer", layer);
			session.setAttribute("LOA.ckDimensionList", checkedDimensions);

			JSPManager.showJSP(request, response, "/tools/dim-param-form.jsp");

			break;

		}

	}

	protected void doDSPost(Context context, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, SQLException, AuthorizeException {
		HttpSession session = request.getSession(false);

		if (session == null) {
			JSPManager.showInternalError(request, response);
		}

		int itemID = UIUtil.getIntParameter(request, "item_id");

		String layer = request.getParameter("layer");

		Item item = Item.find(context, itemID);

		String handle = HandleManager.findHandle(context, item);

		String weight, dimWght = null;

		int layerId = layer.equals("Administrator") ? 1 : layer.equals("Expert") ? 2 : layer.equals("Student") ? 3 : 0;

		// Firstly, we get the values set by user in GUI and check total weight
		// by layer

		boolean validWeights = validateTotalWeight(context, request, response);

		if (!validWeights) {
			JSPManager.showInternalError(request, response);
			System.out.println("Assigned weights must complete 100%!!!");
		}

		// Later, we check values previously parameterized and update them if
		// necessary

		Dimension[] dimensionsList = Dimension.findByLayer(context, layer);
		// Buscanos solo los assess params de la capa
		assessParamList = AssessParam.findParam(context, itemID, layerId);
		// AssessItemServlet.loadAssessParam(context, request, response,
		// itemID);

		if (assessParamList != null) {
			for (int i = 0; i < assessParamList.size(); i++) {
				AssessParam assessParam = assessParamList.elementAt(i);
				Dimension dimension = null;
				for (Dimension dim : dimensionsList) {
					if (dim.getID() == assessParam.getDimID()) {
						dimension = dim;
						break;
					}

				}
				if (request.getParameter(dimension.getName()) != null) {
					dimWght = request.getParameter(dimension.getName());
					Dimension.updateDimensionWeight(context, assessParam.getDimWeightID(), itemID, dimWght);
				}

			}
		}

		// Saves and updates parameterized metrics assessment for the item
		// involved and show confirm page

		if (ckMetricsID == null) {
			JSPManager.showInternalError(request, response);
			System.out.println("You must select at least a metric!!!");
		}

		for (int i = 0; i < ckMetricsID.size(); i++) {
			try {
				Dimension dimension = null;
				String metricInfo = ckMetricsID.elementAt(i).toString();
				String[] data = metricInfo.split(",");
				int layerID = Integer.valueOf(data[0]).intValue();
				int dimensionID = Integer.valueOf(data[1]).intValue();
				int metricID = Integer.valueOf(data[2]).intValue();

				Metric.addAssessMetric(context, metricID, itemID);

				for (Dimension dim : dimensionsList) {
					if (dim.getID() == dimensionID) {
						dimension = dim;
						break;
					}
				}

				if (request.getParameter(dimension.getName()) != null) {
					weight = request.getParameter(dimension.getName());
					Dimension.addDimensionWeight(context, dimensionID, layerID, itemID, weight);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		request.setAttribute("item", item);
		request.setAttribute("handle", handle);

		if (request.getParameter("submit_yes") != null) {
			JSPManager.showJSP(request, response, "/tools/param-form.jsp");
		}
		if (layer.equals("Administrator")) {
			// Reload assess param for the layer in order to get all the new
			// params added from checked metrics
			assessParamList = AssessParam.findParam(context, itemID, layerId);
			Vector<String> adminAvailAssess = new Vector<String>();
			;
			String criteriaName = "";
			String assessOptions = "";

			if (assessParamList != null) {
				for (int i = 0; i < assessParamList.size(); i++) {

					AssessParam assessParam = (AssessParam) assessParamList.elementAt(i);
					criteriaName = Metric.findNameByID(context, assessParam.getAssessMetricID());
					assessOptions = AssessItemServlet.getListOptions(criteriaName);

					if (!adminAvailAssess.contains(assessOptions))
						adminAvailAssess.addElement(assessOptions);
				}
			}

			session.setAttribute("LOA.adminAvailAssess", adminAvailAssess);

			JSPManager.showJSP(request, response, "/tools/admin-assess.jsp");
		} else
			JSPManager.showJSP(request, response, "/tools/success-page.jsp");
	}

	private Vector<Dimension> setDimensions(Context context, String layer) {

		Vector<Dimension> dimensionList = new Vector<Dimension>();

		try {
			Dimension[] dimensions = Dimension.findByLayer(context, layer);
			for (Dimension dim : dimensions) {
				dimensionList.add(dim);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dimensionList;
	}

	private Vector<Metric> setMetrics(Context context, String layer) {

		Vector<Metric> metricList = null;
		try {
			metricList = Metric.findByLayer(context, layer);
		} catch (SQLException e) {
			e.printStackTrace();

		}
		return metricList;
	}

	private boolean validateTotalWeight(Context context, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		double dimWeight = 0.0;
		double totWeight = 0.0;

		String[] dimensions = { "Content", "Contextual", "Educational", "Esthetic", "Functional", "Metadata" };
		for (String dim : dimensions) {
			if (request.getParameter(dim) != null) {
				dimWeight = Double.valueOf(request.getParameter(dim)).doubleValue() / 100.0;
				totWeight += dimWeight;
			}
		}

		// Validation for total dimension weightings
		if (totWeight > 0.998 && totWeight < 1.002)
			return true;

		return false;
	}

	private Vector<String> verifyCheckedDimensions(Context context, Vector<String> checkedMetrics,
			Vector<AssessParam> assessmentParam, String layer) {

		Vector<String> ckDimensions = new Vector<String>();
		String dimensionName = null;
		int layerId = layer.equals("Administrator") ? 1 : layer.equals("Expert") ? 2 : layer.equals("Student") ? 3 : 0;

		// first, we verify metric's dimension previously parameterized
		if (assessmentParam != null && !assessmentParam.isEmpty() && layerId > 0) {
			for (int i = 0; i < assessmentParam.size(); i++) {
				AssessParam assessParam = (AssessParam) assessmentParam.elementAt(i);
				if (assessParam.getLayerID() == layerId) {
					try {
						dimensionName = Dimension.findNameByID(context, assessParam.getDimID());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (!ckDimensions.contains(dimensionName) && dimensionName != null)
					ckDimensions.addElement(dimensionName);
			}
		}

		// Now, we can add dimension from metrics recently checked to item's
		// assessment
		if (checkedMetrics != null && !checkedMetrics.isEmpty()) {
			for (int i = 0; i < checkedMetrics.size(); i++) {
				String metricInfo = checkedMetrics.elementAt(i).toString();
				String[] data = metricInfo.split(",");
				int tempLayerId = Integer.valueOf(data[0]).intValue();
				int dimensionId = Integer.valueOf(data[1]).intValue();

				if (tempLayerId == layerId) {
					try {
						dimensionName = Dimension.findNameByID(context, dimensionId);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (!ckDimensions.contains(dimensionName) && dimensionName != null)
					ckDimensions.addElement(dimensionName);
			}
		}

		return ckDimensions;
	}
}