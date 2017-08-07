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

import org.dspace.app.webui.servlet.AssessItemServlet;
import org.dspace.app.webui.servlet.DSpaceServlet;
import org.dspace.app.webui.util.AssessResult;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.loa.AssessParam;
import org.dspace.loa.AvailabilityAssess;
import org.dspace.loa.CoherenceAssess;
import org.dspace.loa.CompletenessAssess;
import org.dspace.loa.ConsistencyAssess;
import org.dspace.loa.Dimension;
import org.dspace.loa.Layer;
import org.dspace.loa.Metric;
import org.dspace.loa.ReusabilityAssess;
import org.dspace.loa.VisibilityAssess;

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

	/** assess completed successfully */
	public static boolean ASSESS_SUCCESS = false;

	/** success status */
	public static final String SUCCESS_STATUS = "Success";

	/** fail status */
	public static final String FAIL_STATUS = "Fail";

	/** Logger */
	// private static Logger log =
	// Logger.getLogger(EditCommunitiesServlet.class);

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
		double result = -1.0;

		if (assess2Perform == null)
			JSPManager.showInternalError(request, response);

		if (assess2Perform.equals("Availability")) {
			result = AvailabilityAssess.perform(item);
			if (result >= 0.0) {
				ASSESS_SUCCESS = true;
				request.setAttribute("task_result", new AssessResult("Availability", handle, SUCCESS_STATUS, new DecimalFormat(
						"#.##").format(result) + ". " + AvailabilityAssess.getResults(item), ASSESS_SUCCESS));
				Metric.addAssessValue(context, result, "Availability", 1, itemID);
			} else
				request.setAttribute("task_result", new AssessResult("Availability", handle, FAIL_STATUS, null, ASSESS_SUCCESS));
		}
		if (assess2Perform.equals("Coherence")) {
			try {
				result = CoherenceAssess.perform(item);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result >= 0.0) {
				ASSESS_SUCCESS = true;
				request.setAttribute("task_result", new AssessResult("Coherence", handle, SUCCESS_STATUS, new DecimalFormat(
						"#.##").format(result) + ". " + CoherenceAssess.getResults(item), ASSESS_SUCCESS));
				Metric.addAssessValue(context, result, "Coherence", 1, itemID);
			} else {
				request.setAttribute("task_result", new AssessResult("Coherence", handle, FAIL_STATUS, null, ASSESS_SUCCESS));
			}
		}
		if (assess2Perform.equals("Completeness")) {
			result = CompletenessAssess.perform(item);
			if (result >= 0.0) {
				ASSESS_SUCCESS = true;
				request.setAttribute("task_result", new AssessResult("Completeness", handle, SUCCESS_STATUS, new DecimalFormat(
						"#.##").format(result) + ". " + CompletenessAssess.getResults(item), ASSESS_SUCCESS));
				Metric.addAssessValue(context, result, "Completeness", 1, itemID);
			} else {
				request.setAttribute("task_result", new AssessResult("Completeness", handle, FAIL_STATUS, null, ASSESS_SUCCESS));
			}
		}
		if (assess2Perform.equals("Consistency")) {
			try {
				result = ConsistencyAssess.perform(item);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (result >= 0.0) {
				ASSESS_SUCCESS = true;
				request.setAttribute("task_result", new AssessResult("Consistency", handle, SUCCESS_STATUS, new DecimalFormat(
						"#.##").format(result) + ". " + ConsistencyAssess.getResults(item), ASSESS_SUCCESS));
				Metric.addAssessValue(context, result, "Consistency", 1, itemID);
			} else {
				request.setAttribute("task_result", new AssessResult("Consistency", handle, FAIL_STATUS, null, ASSESS_SUCCESS));
			}
		}
		if (assess2Perform.equals("Reusability")) {
			result = ReusabilityAssess.perform(item);
			if (result >= 0.0) {
				ASSESS_SUCCESS = true;
				request.setAttribute("task_result", new AssessResult("Reusability", handle, SUCCESS_STATUS, new DecimalFormat(
						"#.##").format(result) + ". " + ReusabilityAssess.getResults(item), ASSESS_SUCCESS));
				Metric.addAssessValue(context, result, "Reusability", 1, itemID);
			} else {
				request.setAttribute("task_result", new AssessResult("Reusability", handle, FAIL_STATUS, null, ASSESS_SUCCESS));
			}
		}
		if (assess2Perform.equals("Visibility")) {
			result = VisibilityAssess.perform(context, item);
			if (result >= 0.0) {
				ASSESS_SUCCESS = true;
				request.setAttribute("task_result", new AssessResult("Visibility", handle, SUCCESS_STATUS, new DecimalFormat(
						"#.##").format(result) + ". " + VisibilityAssess.getResults(context, item), ASSESS_SUCCESS));
				Metric.addAssessValue(context, result, "Visibility", 1, itemID);
			} else {
				request.setAttribute("task_result", new AssessResult("Visibility", handle, FAIL_STATUS, null, ASSESS_SUCCESS));
			}
		}

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
		int itemID = UIUtil.getIntParameter(request, "item_id");
		Item item = Item.find(context, itemID);

		switch (action) {

		case SHOW_RESULTS:

			double adminIndex,
			expIndex,
			stdIndex,
			totalIndex;

			Vector<AssessParam> assessParamList = AssessParam.findParam(context, itemID);
			Vector<String> results = new Vector<String>();

			if (assessParamList != null) {
				for (int i = 0; i < assessParamList.size(); i++) {
					AssessParam assessParam = assessParamList.elementAt(i);
					if (assessParam.getMetricValue() == null || assessParam.getMetricValue().length() == 0) {
						continue;
					}
					String dimensionName;
					String metricName;
					String mtrVal;
					String data;
					double metricValue = 0;
					dimensionName = Dimension.findNameByID(context, assessParam.getDimID());
					metricName = Metric.findNameByID(context, assessParam.getAssessMetricID());
					metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue() * 100;
					mtrVal = new DecimalFormat("###.##").format(metricValue);
					data = assessParam.getLayerID() + "," + dimensionName + "," + metricName + "," + mtrVal;
					results.addElement(data);
				}
			}

			session.setAttribute("LOA.results", results);
			request.setAttribute("item", item);

			adminIndex = calculateLayerIndex(assessParamList, 1);
			expIndex = calculateLayerIndex(assessParamList, 2);
			stdIndex = calculateLayerIndex(assessParamList, 3);
			totalIndex = calculateTotalIndex(context, adminIndex, expIndex, stdIndex, itemID);
			int indexID = Layer.findIndexByItem(context, itemID);
			if (indexID > 0) {
				Layer.updateAssessIndexes(context, adminIndex, expIndex, stdIndex, totalIndex, indexID);
			} else {
				try {
					Layer.addAssessIndexes(context, itemID, adminIndex, expIndex, stdIndex, totalIndex);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DecimalFormat decimalFormat = new DecimalFormat("###");
			String admIndexString = adminIndex != 0 ? decimalFormat.format(adminIndex * 100) : "";
			String expIndexString = expIndex != 0 ? decimalFormat.format(expIndex * 100) : "";
			String stdIndexString = stdIndex != 0 ? decimalFormat.format(stdIndex * 100) : "";
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
			int layerDel = Layer.DeleteAssessIndexes(context, itemID);
			int dimDel = Dimension.DeleteAssessWeights(context, itemID);
			int metDel = Metric.deleteAssessValues(context, itemID);

			if ((layerDel + dimDel + metDel) > 0) {
				JSPManager.showJSP(request, response, "/tools/success-page.jsp");
			}
			break;

		}

	}

	public double calculateLayerIndex(Vector<AssessParam> assessParamList, int layerID) {

		double metricValue, dimWght;
		double layerIndex = 0.0;

		Map<String, double[]> dimensionData = new HashMap<String, double[]>();
		dimensionData.put("1", new double[3]);
		dimensionData.put("2", new double[3]);
		dimensionData.put("3", new double[3]);
		dimensionData.put("4", new double[3]);
		dimensionData.put("5", new double[3]);
		dimensionData.put("6", new double[3]);

		// work out total value per dimension using weight set by administrator
		if (assessParamList != null) {
			for (int i = 0; i < assessParamList.size(); i++) {
				AssessParam assessParam = assessParamList.elementAt(i);
				if (assessParam.getLayerID() == layerID) {
					if (assessParam.getMetricValue() == null || assessParam.getMetricValue().length() == 0) {
						continue;
					}
					metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue();
					String dimId = String.valueOf(assessParam.getDimID());
					double values[] = dimensionData.get(dimId);
					values[0] += 1;

					values[1] += metricValue;
					dimWght = Double.valueOf(assessParam.getAdmWeight()).doubleValue() / 100;
					values[2] = (values[1] / values[0]) * dimWght;
					dimensionData.put(dimId, values);
				}
			}

			for (double[] values : dimensionData.values()) {
				layerIndex = layerIndex + values[2];
			}
		}
		return layerIndex;

	}

	public double calculateTotalIndex(Context context, double adminIndex, double expIndex, double stdIndex, int itemID) {
		double totIndex = 0.0;

		if (adminIndex > 0 && expIndex > 0 && stdIndex > 0)
			totIndex = (adminIndex * 0.5) + (expIndex * 0.3) + (stdIndex * 0.2);
		else if (adminIndex > 0 && expIndex > 0)
			totIndex = (adminIndex * 0.6) + (expIndex * 0.4);
		else if (adminIndex > 0 && stdIndex > 0)
			totIndex = (adminIndex * 0.7) + (stdIndex * 0.3);
		else if (expIndex > 0 && stdIndex > 0)
			totIndex = (expIndex * 0.6) + (stdIndex * 0.4);
		else if (adminIndex > 0)
			totIndex = adminIndex;
		else if (expIndex > 0)
			totIndex = expIndex;
		else if (stdIndex > 0)
			totIndex = stdIndex;

		totIndex = totIndex * 10;

		return totIndex;
	}

}
