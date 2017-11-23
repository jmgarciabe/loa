package org.dspace.loa;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.dspace.core.Context;

public class StudentAssessHelper {
	

	/**
	 * Sets or updates student assessment results with the given values for the
	 * item id passed
	 * 
	 * @param context
	 *            - the DSpace context object to execute data base operations
	 * @param itemId
	 *            - item id
	 * @param perMetricValues
	 *            - A map with the results of the student assessment, each entry
	 *            in the map represents a metric and an List of values for the
	 *            metric
	 * @throws SQLException
	 *             - May throw an SQL Exception while carrying out SQL
	 *             operations
	 */
	public void setStudentAssessment(Context context, int itemId, Map<String, List<Double>> perMetricValues) throws SQLException {
		List<AssessParam> assessParamList = AssessParam.findParam(context, itemId, 3);
		double formValue = 0;
		double dbValue = 0;

		// Recorremos los param para actualizar o guardar el valor de la métrica
		for (AssessParam param : assessParamList) {
			if (param.getMetricValue() != null && param.getMetricValue().length() > 0) {
				formValue = Double.valueOf(param.getMetricValue()).doubleValue();
			}
			String metricId = String.valueOf(param.getAssessMetricID());
			List<Double> answers = perMetricValues.get(metricId);
			for (Double q : answers) {
				dbValue += q.doubleValue();
			}
			if (answers.size() > 0) {
				dbValue /= (answers.size() * 5);
			}
			if (formValue > 0) {
				dbValue = (dbValue + formValue) / 2;
			}
			if (answers.size() > 0) {
				AssessmentResult r = new AssessmentResult(param.getAssessMetricID(), itemId);
				r.setValue(dbValue);
				AssessmentResultDao.getInstance().addAssessmentResult(context, r);
			}
			// Se resetean los valores
			formValue = 0;
			dbValue = 0;
		}
	}
}
