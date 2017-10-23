package org.dspace.loa;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.dspace.core.Context;

public class ExpertAssessHelper {

	/** Item id */
	private int itemId;

	/** Map holding old weights retrieved from data base */
	private Map<String, Integer> oldDimWeights;

	/** Map holding weights assigned by expert */
	private Map<String, Integer> dimWeights;

	/** Map holding old values store in database before update */
	private Map<String, Double> metricsValues;

	public ExpertAssessHelper(int itemId) {

		this.itemId = itemId;
		
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

	public void setExpertWeight(Context context, Map<String,Integer> values) throws SQLException{
		
		Vector<AssessParam> assessParamList = AssessParam.findParam(context, itemId, 2);
		for (int i = 0; i < assessParamList.size(); i++) {
			AssessParam assessParam = (AssessParam) assessParamList.elementAt(i);
			String dimId = String.valueOf(assessParam.getDimID());
			String assessMetricId = String.valueOf(assessParam.getAssessMetricID());
			Integer weight = values.get(dimId);
			if (weight == 0) {
				continue;
			}
			if (assessParam.getMetricValue() != null && assessParam.getExpWeight() > 0) {
				oldDimWeights.put(dimId, assessParam.getExpWeight());
				metricsValues.put(assessMetricId, Double.valueOf(assessParam.getMetricValue()));
			}
			dimWeights.put(dimId, weight);
			Dimension.updateExpertWeight(context, assessParam.getDimWeightID(), itemId, dimWeights.get(dimId));
		}

	}

}
