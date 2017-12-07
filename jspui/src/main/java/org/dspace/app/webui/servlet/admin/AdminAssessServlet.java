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
import java.util.List;
import java.util.Map;

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
import org.dspace.loa.AdminAssessHelper;
import org.dspace.loa.AdminAssessmentException;
import org.dspace.loa.AdminAssessmentReport;
import org.dspace.loa.AssessmentMetric;
import org.dspace.loa.AssessmentResult;

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

		List<AssessmentMetric>  metrics = (List<AssessmentMetric>) session.getAttribute("LOA.metricList");
		int itemId = UIUtil.getIntParameter(request, "item_id");
		int assessmentMetricId = Integer.valueOf(request.getParameter("assessment-metric"));
		Item item = Item.find(context, itemId);

		if (assessmentMetricId == 0) {
			JSPManager.showInternalError(request, response);
		}

		AdminAssessHelper assessHelper = new AdminAssessHelper();
		AdminAssessmentReport report = null;
		try {
			report = assessHelper.assess(assessmentMetricId, item, context);
		} catch (AdminAssessmentException aae) {
			System.out.print(aae.getMessage());
		}
		assessHelper.saveAssessmnetResult(context, assessmentMetricId, itemId, report.getScore());

		request.setAttribute("task_result", report);
		session.setAttribute("LOA.metricList", metrics);
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
		AdminAssessHelper helper = new AdminAssessHelper();
		
		switch (action) {

		case SHOW_RESULTS:

			
			List<AssessmentResult> results = helper.getAssessmentResults(context, itemId);
			session.setAttribute("LOA.results", results);
			request.setAttribute("item", item);
			
			double adminIndex = helper.calculateLayerIndex(context,results, itemId, 1);
			double expIndex = helper.calculateLayerIndex(context, results, itemId, 2);
			double stdIndex = helper.calculateLayerIndex(context, results, itemId, 3);
			double totalIndex = helper.calculateTotalIndex(adminIndex, expIndex, stdIndex);
						
			DecimalFormat decimalFormat = new DecimalFormat("###");
			String admIndexString = adminIndex >= 0 ? decimalFormat.format(adminIndex*10) : "";
			String expIndexString = expIndex >= 0 ? decimalFormat.format(expIndex*10) : "";
			String stdIndexString = stdIndex >= 0 ? decimalFormat.format(stdIndex*10) : "";
			
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
			helper.deleteAllByItem(context, itemId);
			JSPManager.showJSP(request, response, "/tools/success-page.jsp");
			break;

		}

	}

}
