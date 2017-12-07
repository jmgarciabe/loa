package org.dspace.loa;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dspace.core.Context;

public class ExpertAssessHelper {

	/**
	 * Sets or updates expert weights with the given values
	 * 
	 * @param context
	 *            the DSpace context object to execute data base operations
	 * @param values
	 *            A map with the experts weights values to be set, each entry in
	 *            the map represents a dimension and the given weight
	 * @throws SQLException
	 *             May throw an SQL Exception while carrying out SQL operations
	 */
	public void setExpertWeight(Context context, Map<String, Integer> values) throws SQLException {

		for (Entry<String, Integer> entry : values.entrySet()) {
			ExpertWeighting expWeighting = new ExpertWeighting();
			expWeighting.setDimWeightingId(Integer.valueOf(entry.getKey()));
			expWeighting.setExpertId(context.getCurrentUser().getID());
			expWeighting.setExpertWeight(entry.getValue());
			ExpertWeightingDao.getInstance().saveWeight(context, expWeighting);
		}
	}

	/**
	 * Sets or updates the set of expert assessment results using the given
	 * values
	 * 
	 * @param context
	 *            the DSpace context object to execute data base operations
	 * @param perMetricValues
	 *            A map with the results of the expertAssessment, each entry in
	 *            the map represents an assessment metric ID and a List of values for the
	 *            metric
	 * @param itemId
	 *            item ID
	 * @throws SQLException
	 *             - May throw an SQL Exception while carrying out SQL
	 *             operations
	 */
	public void setExpertAssessment(Context context, Map<String, List<Double>> perMetricValues, int itemId) throws SQLException {

		for (Entry<String, List<Double>> entry : perMetricValues.entrySet()) {
			int metricId = Integer.valueOf(entry.getKey());
			double overallScore = 0.0;
			double weightsTotal = 0.0;
			double score = 0.0;
			for (Double val : entry.getValue()) {
				score += val;
			}
			if (score > 0) {
				score /= (entry.getValue().size() * 5);
			}
			AssessmentResult result = AssessmentResultDao.getInstance().getAssessmentResult(context, metricId, itemId);
			PersonalAssessment individualResult = new PersonalAssessment(result.getId(), context.getCurrentUser().getID());
			individualResult.setValue(score);
			PersonalAssessmentDao.getInstance().addAssessment(context, individualResult);
			// Work out new metric assessment result
			List<PersonalAssessment> assessmentList = PersonalAssessmentDao.getInstance().getByAssessmentResult(context,
					result.getId());
			List<ExpertWeighting> weights = ExpertWeightingDao.getInstance().getWeightings(context, metricId, itemId);
			for (PersonalAssessment a : assessmentList) {
				if (a.getValue() != null) {
					for (ExpertWeighting w : weights) {
						if (a.getPersonId() == w.getExpertId()) {
							weightsTotal += w.getExpertWeight();
							overallScore += (a.getValue() * w.getExpertWeight());
							break;
						}
					}
				}
			}
			if (weightsTotal > 0) {
				overallScore /= weightsTotal;
			}
			result.setValue(overallScore);
			AssessmentResultDao.getInstance().saveAssesmentResult(context, result);
		}
	}

}
