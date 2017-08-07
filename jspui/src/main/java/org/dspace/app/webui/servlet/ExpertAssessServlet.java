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
import org.dspace.loa.Metric;

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
	private Map<String, Integer> oldDimWeights;

	/** Map holding weights assigned by expert */
	private Map<String, Integer> dimWeights;

	/** Map holding attributes IDs expected from the client */
	private Map<String, String> attrId;

	/** Map holding old values store in database before update */
	private Map<String, Double> metricsValues;

	/** Variables to assess content metrics */
	private int contWght = 0;
	private int contWghtDb = 0;
	private double rigor = 0.0;

	/** Variables to assess educational metrics */
	private int eduWght = 0;
	private int eduWghtDb = 0;
	private double effect = 0.0;

	/** Variables to assess esthetic metrics */
	private int estWght = 0;
	private int estWghtDb = 0;
	private double visual = 0.0;

	/** Variables to assess functional metrics */
	private int funcWght = 0;
	private int funcWghtDb = 0;
	private double reuse = 0.0;
	private double use = 0.0;
	private double access = 0.0;

	/** Variables to assess metadata metrics */
	private int metWght = 0;
	private int metWghtDb = 0;
	private double complete = 0.0;
	private double accuracy = 0.0;

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

			initData();
			// we check values previously parameterized for this layer (2) and
			// update them if
			// necessary.
			Vector<AssessParam> assessParamList = AssessParam.findParam(context, itemId, 2);

			if (assessParamList != null) {
				for (int i = 0; i < assessParamList.size(); i++) {
					AssessParam assessParam = (AssessParam) assessParamList.elementAt(i);
					String dimId = String.valueOf(assessParam.getDimID());
					String assessMetricId = String.valueOf(assessParam.getAssessMetricID());
					String weight = request.getParameter(attrId.get(dimId));
					if (weight == null || weight.length() == 0) {
						continue;
					}
					if (assessParam.getMetricValue() != null && assessParam.getExpWeight() > 0) {
						oldDimWeights.put(dimId, assessParam.getExpWeight());
						metricsValues.put(assessMetricId, Double.valueOf(assessParam.getMetricValue()));
					}
					dimWeights.put(dimId, Integer.valueOf(weight));
					Dimension.updateExpertWeight(context, assessParam.getDimWeightID(), itemId, dimWeights.get(dimId));
				}
			}

			Vector<String> expMetrics = new Vector<String>();
			expMetrics = AssessItemServlet.getMetrics(context, assessParamList, 2);
			request.setAttribute("item", item);
			request.setAttribute("handle", handle);
			session.setAttribute("LOA.expMetrics", expMetrics);

			// Redirect to expert survey
			JSPManager.showJSP(request, response, "/tools/expert-survey.jsp");

			break;

		case EXP_SURVEY:


			
			Map<String, String> criteriaIds = new HashMap<String, String>();
			criteriaIds.put("12","Accessibility");
			criteriaIds.put("14","Accuracy");
			criteriaIds.put("13","Completeness");
			criteriaIds.put("11","Ease to use");
			criteriaIds.put("8" ,"Potential Effectiveness");
			criteriaIds.put("10","Reusability");
			criteriaIds.put("7" ,"Rigor and Relevance");
			criteriaIds.put("9" ,"Visual Design");
			
			Map<String,String[]> answerIds = new HashMap<String,String[]>();
			answerIds.put("12", new String [] {"acs1", "acs2"});
			answerIds.put("14", new String [] {"acc1"});
			answerIds.put("13", new String [] {"com1"});
			answerIds.put("11", new String [] {"eou1", "eou2"});
			answerIds.put("8", new String [] {"poe1", "poe2"});
			answerIds.put("10", new String [] {"reu1", "reu2"});
			answerIds.put("7", new String [] {"rar1", "rar2"});
			answerIds.put("9", new String [] {"vid1", "vid2"});
			
			Map<String,String> metricDimension = new HashMap<String,String>();
			metricDimension.put("12","5");
			metricDimension.put("14","6");
			metricDimension.put("13","6");
			metricDimension.put("11","5");
			metricDimension.put("8","3");
			metricDimension.put("10","5");
			metricDimension.put("7","1");
			metricDimension.put("9","4");
			
			
			
			for(Entry<String, String[]> ansIds: answerIds.entrySet()){
				
				double result = 0.0;
				double oldW = 0.0;
				double newW = 0.0;
				double value = 0.0;
				String dimension;
				
				dimension = metricDimension.get(ansIds.getKey());
				oldW = oldDimWeights.get(dimension);
				newW = dimWeights.get(dimension);;
				 
				String[] ids = ansIds.getValue();
				for(String answer : ids){
					if(request.getParameter(answer) != null){
						value += Double.valueOf(request.getParameter(answer));
					}
				}
				value /= (ids.length*5);
				result = Double.valueOf(metricsValues.get(ansIds.getKey()));
				result = (result*oldW  + value*newW) / (oldW + newW);
				Metric.addAssessValue(context, result, criteriaIds.get(ansIds.getKey()), 2, itemId);
				
			}

			request.setAttribute("item", item);
			JSPManager.showJSP(request, response, "/tools/success-page.jsp");

			break;

		}
	}

	private void initData() {

		oldDimWeights = new HashMap<String, Integer>();
		oldDimWeights.put("1", 0);
		oldDimWeights.put("3", 0);
		oldDimWeights.put("4", 0);
		oldDimWeights.put("5", 0);
		oldDimWeights.put("6", 0);

		dimWeights = new HashMap<String, Integer>();
		dimWeights.put("1", 0);
		dimWeights.put("3", 0);
		dimWeights.put("4", 0);
		dimWeights.put("5", 0);
		dimWeights.put("6", 0);

		attrId = new HashMap<String, String>();
		attrId.put("1", "exp_Content");
		attrId.put("3", "exp_Educational");
		attrId.put("4", "exp_Esthetic");
		attrId.put("5", "exp_Functional");
		attrId.put("6", "exp_Metadata");

		metricsValues = new HashMap<String, Double>();
		metricsValues.put("12", 0.0);
		metricsValues.put("14", 0.0);
		metricsValues.put("13", 0.0);
		metricsValues.put("11", 0.0);
		metricsValues.put("8", 0.0);
		metricsValues.put("10", 0.0);
		metricsValues.put("7", 0.0);
		metricsValues.put("9", 0.0);

	}

}