package org.dspace.loa;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dspace.core.Context;

public class StudentAssessHelper {

	/**
	 * Sets or updates student assessment results with the given values for the
	 * item id passed
	 * 
	 * @param context
	 *            the DSpace context object to execute data base operations
	 * @param itemId
	 *            item id
	 * @param perMetricValues
	 *            A map with the results of the student assessment, each entry
	 *            in the map represents an assessment metric ID and the List of values for the
	 *            metric
	 * @throws SQLException
	 *            May throw an SQL Exception while carrying out SQL
	 *             operations
	 */
	public void setStudentAssessment(Context context, int itemId, Map<String, List<Double>> perMetricValues) throws SQLException {

		for (Entry<String, List<Double>> entry : perMetricValues.entrySet()) {
			int metricId = Integer.valueOf(entry.getKey());
			double overallScore = 0.0;
			int assessmentsCount = 0;
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
			List<PersonalAssessment> assessmentList = PersonalAssessmentDao.getInstance().getByAssessmentResult(context, result.getId());
			for (PersonalAssessment a : assessmentList) {
				if (a.getValue() != null) {
					assessmentsCount++;
					overallScore += a.getValue();
				}
			}
			if (assessmentsCount > 0) {
				overallScore /= assessmentsCount;
			}
			result.setValue(overallScore);
			AssessmentResultDao.getInstance().saveAssesmentResult(context, result);
		}

	}
}
