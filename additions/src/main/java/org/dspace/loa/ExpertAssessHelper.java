package org.dspace.loa;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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

	/**
	 * Sets or updates expert weights with the given values
	 * 
	 * @param context
	 *            -the DSpace context object to execute data base operations
	 * @param values
	 *            - A map with the experts weights values to be set, each entry
	 *            in the map represents a dimension and the given weight
	 * @throws SQLException
	 *             - May throw an SQL Exception while carrying out SQL
	 *             operations
	 */
	public void setExpertWeight(Context context, Map<String, Integer> values) throws SQLException {

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

	/**
	 * Sets or updates the set of expert assessment results using the
	 * given values
	 * 
	 * @param context
	 *            - the DSpace context object to execute data base operations
	 * @param perMetricValues
	 *            - A map with the results of the expertAssessment, each entry
	 *            in the map represents a metric and an List of values for the
	 *            metric
	 * @throws SQLException
	 *             - May throw an SQL Exception while carrying out SQL
	 *             operations
	 */
	public void setExpertAssessment(Context context, Map<String, List<Double>> perMetricValues) throws SQLException {

		Vector<AssessParam> assessParamList = AssessParam.findParam(context, itemId, 2);
		Map<String, String> criteriaIds = new HashMap<String, String>();
		criteriaIds.put("12", "Accessibility");
		criteriaIds.put("14", "Accuracy");
		criteriaIds.put("13", "Completeness");
		criteriaIds.put("11", "Ease to use");
		criteriaIds.put("8", "Potential Effectiveness");
		criteriaIds.put("10", "Reusability");
		criteriaIds.put("7", "Rigor and Relevance");
		criteriaIds.put("9", "Visual Design");

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
			List<Double> valuesEntry = perMetricValues.get(assessMetricId);
			for (Double answer : valuesEntry) {
				value += answer;
			}
			value /= (valuesEntry.size() * 5);
			result = Double.valueOf(metricsValues.get(assessMetricId));
			result = (result * oldW + value * newW) / (oldW + newW);
			Metric.addAssessValue(context, result, criteriaIds.get(assessMetricId), 2, itemId);

		}

	}

}
