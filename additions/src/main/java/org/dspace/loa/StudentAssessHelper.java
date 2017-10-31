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
	public void setExpertAssessment(Context context, int itemId, Map<String, List<Double>> perMetricValues) throws SQLException {
		Vector<AssessParam> assessParamList = AssessParam.findParam(context, itemId, 3);
		double formValue = 0;
		double dbValue = 0;

		// Se crean mapas con información de los variables y ids de las
		// dimensiones para almacenar respuestas
		Map<String, String> criteriaIds = new HashMap<String, String>();
		criteriaIds.put("19", "Availability");
		criteriaIds.put("21", "Accuracy");
		criteriaIds.put("20", "Ease to use");
		criteriaIds.put("17", "Effectiveness");
		criteriaIds.put("16", "Motivation");
		criteriaIds.put("15", "Relevance");
		criteriaIds.put("18", "Visual Design");

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
				Metric.addAssessValue(context, dbValue, criteriaIds.get(metricId), 3, itemId);
			}
			// Se resetean los valores
			formValue = 0;
			dbValue = 0;
		}
	}
}
