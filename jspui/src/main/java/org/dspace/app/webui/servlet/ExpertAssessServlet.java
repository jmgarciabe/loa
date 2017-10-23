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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.dspace.loa.ExpertAssessHelper;
import org.dspace.loa.Metric;
import org.dspace.loa.StartAssessHelper;

/**
 * Servlet for perform the assessment logic of each of the expert layer metrics
 * 
 * @author Andres Salazar
 * @version $Revision$
 */
public class ExpertAssessServlet extends DSpaceServlet {

	/**
	 * Users sets weights to dimensions to indicate their experience in each one
	 */
	public static final int DIM_PARAM = 10;

	/** Users send their concept for an item through an answered survey */
	public static final int EXP_SURVEY = 15;

	/** Map holding old weights retrieved from data base */
	// private Map<String, Integer> oldDimWeights;

	/** Map holding weights assigned by expert */
	// private Map<String, Integer> dimWeights;

	/** Map holding attributes IDs expected from the client */
	// private Map<String, String> attrId;

	/** Map holding old values store in database before update */
	// private Map<String, Double> metricsValues;

	protected void doDSGet(Context context, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, SQLException, AuthorizeException {

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
		String handle = HandleManager.findHandle(context, item);
		Vector<AssessParam> assessParamList = AssessParam.findParam(context, itemId, 2);

		switch (action) {
		case DIM_PARAM:

			// we check values previously parameterized for this layer (2) and
			// update them if necessary.
			ExpertAssessHelper expertHelper = new ExpertAssessHelper(itemId);
			StartAssessHelper startHelper = new StartAssessHelper();

			Map<String, Integer> expertWeights = new HashMap<String, Integer>();
			Map<String, String> ids = new HashMap<String, String>();
			ids.put("1", "exp_Content");
			ids.put("3", "exp_Educational");
			ids.put("4", "exp_Esthetic");
			ids.put("5", "exp_Functional");
			ids.put("6", "exp_Metadata");
			for (Entry<String, String> entry : ids.entrySet()) {
				String weight = request.getParameter(entry.getValue());
				if (weight == null || weight.length() == 0) {
					weight = "0";
				}
				expertWeights.put(entry.getKey(), Integer.valueOf(weight));
			}

			expertHelper.setExpertWeight(context, expertWeights);
			Vector<String> expMetrics = startHelper.getMetrics(context, assessParamList, 2);
			request.setAttribute("expertHelper", expertHelper);
			request.setAttribute("item", item);
			request.setAttribute("handle", handle);
			session.setAttribute("LOA.expMetrics", expMetrics);

			// Redirect to expert survey
			JSPManager.showJSP(request, response, "/tools/expert-survey.jsp");
			break;

		case EXP_SURVEY:

			Map<String, String> criteriaIds = new HashMap<String, String>();
			criteriaIds.put("12", "Accessibility");
			criteriaIds.put("14", "Accuracy");
			criteriaIds.put("13", "Completeness");
			criteriaIds.put("11", "Ease to use");
			criteriaIds.put("8", "Potential Effectiveness");
			criteriaIds.put("10", "Reusability");
			criteriaIds.put("7", "Rigor and Relevance");
			criteriaIds.put("9", "Visual Design");

			Map<String, String[]> answerIds = new HashMap<String, String[]>();
			answerIds.put("12", new String[] { "acs1", "acs2" });
			answerIds.put("14", new String[] { "acc1" });
			answerIds.put("13", new String[] { "com1" });
			answerIds.put("11", new String[] { "eou1", "eou2" });
			answerIds.put("8", new String[] { "poe1", "poe2" });
			answerIds.put("10", new String[] { "reu1", "reu2" });
			answerIds.put("7", new String[] { "rar1", "rar2" });
			answerIds.put("9", new String[] { "vid1", "vid2" });

			for (AssessParam param : assessParamList) {

				double result = 0.0;
				double oldW = 0.0;
				double newW = 0.0;
				double value = 0.0;
				String dimension;
				String assessMetricId;

				dimension = String.valueOf(param.getDimID());
				assessMetricId = String.valueOf(param.getAssessMetricID());
				oldW = oldDimWeights.get(dimension);
				newW = dimWeights.get(dimension);
				String[] ids = answerIds.get(assessMetricId);
				for (String answer : ids) {
					if (request.getParameter(answer) != null) {
						value += Double.valueOf(request.getParameter(answer));
					}
				}
				value /= (ids.length * 5);
				result = Double.valueOf(metricsValues.get(assessMetricId));
				result = (result * oldW + value * newW) / (oldW + newW);
				Metric.addAssessValue(context, result, criteriaIds.get(assessMetricId), 2, itemId);

			}

			request.setAttribute("item", item);
			JSPManager.showJSP(request, response, "/tools/success-page.jsp");

			break;

		}
	}
}