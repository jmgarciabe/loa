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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dspace.app.webui.util.JSPManager;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.handle.HandleManager;
import org.dspace.loa.AssessmentMetric;
import org.dspace.loa.AssessmentMetricDao;
import org.dspace.loa.Dimension;
import org.dspace.loa.ExpertAssessHelper;
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
		

		switch (action) {
		case DIM_PARAM:

			// we check values previously parameterized for this layer (2) and
			// update them if necessary.
			ExpertAssessHelper expertHelper = new ExpertAssessHelper(itemId);
			StartAssessHelper startHelper = new StartAssessHelper();

			Map<String, Integer> expertWeights = new HashMap<String, Integer>();
			List<Dimension> expertDimensions = startHelper.getDimensions(context, 2);
			
			for (Dimension dimension : expertDimensions) {
				String weight = request.getParameter(dimension.getName());
				if (weight == null || weight.length() == 0) {
					weight = "0";
				}
				expertWeights.put(String.valueOf(dimension.getId()), Integer.valueOf(weight));
			}

			expertHelper.setExpertWeight(context, expertWeights);
			List<AssessmentMetric> expMetrics = AssessmentMetricDao.getInstance().getAssessmentMerics(context, item.getID(), 2);
			session.setAttribute("expertHelper", expertHelper);
			request.setAttribute("item", item);
			request.setAttribute("handle", handle);
			session.setAttribute("LOA.expMetrics", expMetrics);

			// Redirect to expert survey
			JSPManager.showJSP(request, response, "/tools/expert-survey.jsp");
			break;

		case EXP_SURVEY:

			ExpertAssessHelper helper = (ExpertAssessHelper)session.getAttribute("expertHelper");
			Map<String, String[]> answerIds = new HashMap<String, String[]>();
			answerIds.put("12", new String[] { "acs1", "acs2" });
			answerIds.put("14", new String[] { "acc1" });
			answerIds.put("13", new String[] { "com1" });
			answerIds.put("11", new String[] { "eou1", "eou2" });
			answerIds.put("8", new String[] { "poe1", "poe2" });
			answerIds.put("10", new String[] { "reu1", "reu2" });
			answerIds.put("7", new String[] { "rar1", "rar2" });
			answerIds.put("9", new String[] { "vid1", "vid2" });
			
			Map<String,List<Double>> responses = new HashMap<String,List<Double>>();
			
			for(Entry<String,String[]> entry: answerIds.entrySet()){
				List<Double> perMetricResponses = new ArrayList<Double>();;
				for (String answer : entry.getValue()) {
					if (request.getParameter(answer) != null) {
						perMetricResponses.add(Double.valueOf(request.getParameter(answer)));
					}
				}
				responses.put(entry.getKey(), perMetricResponses);
			}
			
			helper.setExpertAssessment(context, responses);
			request.setAttribute("item", item);
			JSPManager.showJSP(request, response, "/tools/success-page.jsp");
			break;

		}
	}
}