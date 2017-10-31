package org.dspace.loa;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;

public class AdminAsessHelper {

	private AdminAssessmentCommandIntarface criteriaComand;

	/**
	 * Carries out the related administration assessment to the given criteria
	 * @param criteria - the criteria or name of assessment to be executed
	 * @param dso - the DSpace object upon which assessment should be executed
	 * @param context - execution context needed for carrying out some assessments
	 * @return and AssessResult object with the result information
	 * @throws AdminAssessmentException - may throw a custom exception if something went wrong
	 */
	public AssessResult assess(String criteria, DSpaceObject dso, Context context) throws AdminAssessmentException {

		switch (criteria) {
		case "Availability":
			criteriaComand = new AvailabilityAssessCommand();
			break;
		case "Coherence":
			criteriaComand = new CoherenceAssessCommand();
			break;
		case "Completeness":
			criteriaComand = new CompletenessAssessCommand();
			break;
		case "Consistency":
			criteriaComand = new ConsistencyAssessCommand();
			break;
		case "Reusability":
			criteriaComand = new ReusabilityAssessCommand();
			break;
		case "Visibility":
			criteriaComand = new VisibilityAssessCommand();
			break;
		}
		criteriaComand.executeAssessment(dso, context);
		return criteriaComand.getResult();
	}
	
	/**
	 * Returns assessment result per item of criteria that has been assessed
	 * @param itemId - item of which the results are required
	 * @param context - DSpace Context object
	 * @return List of results
	 * @throws SQLException - May throw an exception when look for results up in DB
	 */
	public Vector<String> getAssessmentResults(int itemId, Context context) throws SQLException{

		DecimalFormat formater = new DecimalFormat("###.##");
		Vector<AssessParam> assessParamList = AssessParam.findParam(context, itemId);
		Vector<String> results = new Vector<String>();

		if (assessParamList != null) {
			for (int i = 0; i < assessParamList.size(); i++) {
				AssessParam assessParam = assessParamList.elementAt(i);
				if (assessParam.getMetricValue() == null || assessParam.getMetricValue().length() == 0) {
					continue;
				}
				String dimensionName;
				String metricName;
				String mtrVal;
				String data;
				double metricValue = 0;
				dimensionName = Dimension.findNameByID(context, assessParam.getDimID());
				metricName = Metric.findNameByID(context, assessParam.getAssessMetricID());
				metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue() * 100;
				mtrVal = formater.format(metricValue);
				data = assessParam.getLayerID() + "," + dimensionName + "," + metricName + "," + mtrVal;
				results.addElement(data);
			}
		}
		
		return results;
	}
	
	/**
	 * It calculates the layer index, which is the weighted value for the layer, its final score
	 * @param assessParamList - List of the Assess Params
	 * @param layerID - the id of the layer
	 * @return final score for the layer worked out from performed assessments so far
	 */
	public double calculateLayerIndex(Vector<AssessParam> assessParamList, int layerID) {

		double metricValue, dimWght;
		double layerIndex = 0.0;
		boolean hasValues = false;

		Map<String, double[]> dimensionData = new HashMap<String, double[]>();
		dimensionData.put("1", new double[3]);
		dimensionData.put("2", new double[3]);
		dimensionData.put("3", new double[3]);
		dimensionData.put("4", new double[3]);
		dimensionData.put("5", new double[3]);
		dimensionData.put("6", new double[3]);

		// work out total value per dimension using weight set by administrator
		if (assessParamList != null) {
			for (int i = 0; i < assessParamList.size(); i++) {
				AssessParam assessParam = assessParamList.elementAt(i);
				if (assessParam.getLayerID() == layerID) {
					if (assessParam.getMetricValue() == null || assessParam.getMetricValue().length() == 0) {
						continue;
					}
					hasValues = true;
					metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue();
					String dimId = String.valueOf(assessParam.getDimID());
					double values[] = dimensionData.get(dimId);
					values[0] += 1;
					values[1] += metricValue;
					dimWght = Double.valueOf(assessParam.getAdmWeight()).doubleValue() / 100;
					values[2] = (values[1] / values[0]) * dimWght;
					dimensionData.put(dimId, values);
				}
			}

			for (double[] values : dimensionData.values()) {
				layerIndex = layerIndex + values[2];
			}
		}
		if (!hasValues)
			return -1;
		return layerIndex;

	}
	
	/**
	 * Work out the final score or index of the assessment process based on score by layers
	 * @param adminIndex - administrators layer final score or index
	 * @param expIndex - experts layer final score or index
	 * @param stdIndex - students layer final score or index
	 * @return
	 */
	public double calculateTotalIndex(double adminIndex, double expIndex, double stdIndex) {
		double totIndex = 0.0;

		if (adminIndex > 0 && expIndex > 0 && stdIndex > 0)
			totIndex = (adminIndex * 0.5) + (expIndex * 0.3) + (stdIndex * 0.2);
		else if (adminIndex > 0 && expIndex > 0)
			totIndex = (adminIndex * 0.6) + (expIndex * 0.4);
		else if (adminIndex > 0 && stdIndex > 0)
			totIndex = (adminIndex * 0.7) + (stdIndex * 0.3);
		else if (expIndex > 0 && stdIndex > 0)
			totIndex = (expIndex * 0.6) + (stdIndex * 0.4);
		else if (adminIndex > 0)
			totIndex = adminIndex;
		else if (expIndex > 0)
			totIndex = expIndex;
		else if (stdIndex > 0)
			totIndex = stdIndex;

		totIndex = totIndex * 10;

		return totIndex;
	}
	
	
}
