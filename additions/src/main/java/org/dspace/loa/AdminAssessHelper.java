package org.dspace.loa;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;

public class AdminAssessHelper {

	private AdminAssessmentCommandIntarface criteriaCommand;

	/**
	 * Carries out the related administration assessment to the given criteria
	 * 
	 * @param criteria
	 *            - the criteria or name of assessment to be executed
	 * @param dso
	 *            - the DSpace object upon which assessment should be executed
	 * @param context
	 *            - execution context needed for carrying out some assessments
	 * @return and AssessResult object with the result information
	 * @throws AdminAssessmentException
	 *             - may throw a custom exception if something went wrong
	 */
	public AdminAssessmentReport assess(int metric, DSpaceObject dso, Context context) throws AdminAssessmentException {

		switch (metric) {
		case 2:
			criteriaCommand = new AvailabilityAssessCommand();
			break;
		case 5:
			criteriaCommand = new CoherenceAssessCommand();
			break;
		case 3:
			criteriaCommand = new CompletenessAssessCommand();
			break;
		case 4:
			criteriaCommand = new ConsistencyAssessCommand();
			break;
		case 1:
			criteriaCommand = new ReusabilityAssessCommand();
			break;
		case 6:
			criteriaCommand = new VisibilityAssessCommand();
			break;
		}
		return criteriaCommand.executeAssessment(dso, context);
	}

	/**
	 * Saves the admin assessment in the database
	 * 
	 * @param context
	 *            - Dspace context object
	 * @param metricId
	 *            - Id of the assessment metric which result is going to be
	 *            added or updated
	 * @param itemId
	 *            - item for which the update id going to be carry out
	 * @param score
	 *            - score of the assessment
	 * @throws SQLException
	 */
	public void saveAssessmnetResult(Context context, int metricId, int itemId, double score) throws SQLException {
		AssessmentResult result = new AssessmentResult(metricId, itemId);
		result.setValue(score);
		AssessmentResultDao.getInstance().saveAssesmentResult(context, result);
	}

	/**
	 * Returns assessment result per item of criteria that has been assessed
	 * 
	 * @param itemId
	 *            - item of which the results are required
	 * @param context
	 *            - DSpace Context object
	 * @return List of results
	 * @throws SQLException
	 *             - May throw an exception when look for results up in DB
	 */
	public List<AssessmentResult> getAssessmentResults(Context context, int itemId) throws SQLException {
		return AssessmentResultDao.getInstance().getAssessmentResultsByItem(context, itemId);
	}

	/**
	 * It calculates the layer index, which is the weighted value for the layer,
	 * its final score
	 * 
	 * @param assessParamList
	 *            - List of the Assess Params
	 * @param layerId
	 *            - the id of the layer
	 * @return final score for the layer worked out from performed assessments
	 *         so far
	 */
	public double calculateLayerIndex(Context context, List<AssessmentResult> results, int itemId, int layerId)
			throws SQLException {

		List<DimensionWeighting> dimensions = DimensionWeightingDao.getInstance().getAssignedDimension(context, itemId, layerId);
		double layerIndex = 0.0;
		boolean hasResults = false;

		// work out total value per dimension using weight set by administrator
		for (DimensionWeighting dim : dimensions) {

			double dimResultsSum = 0.0;
			int dimResultsCount = 0;

			for (AssessmentResult res : results) {
				if (res.getAssessmentMetric().getLayer().getId() != layerId) {
					continue;
				}
				if (res.getAssessmentMetric().getDimension().getId() == dim.getDimension().getId() && res.getValue() != null) {
					dimResultsSum += res.getValue();
					dimResultsCount++;
					hasResults = true;
				}
			}
			if (dimResultsCount > 0) {
				dimResultsSum /= dimResultsCount;
			}
			layerIndex += dimResultsSum * dim.getAdminWeight();
		}
		return hasResults ? layerIndex/10 : -1;

	}

	/**
	 * Work out the final score or index of the assessment process based on
	 * score by layers
	 * 
	 * @param adminIndex
	 *            - administrators layer final score or index
	 * @param expIndex
	 *            - experts layer final score or index
	 * @param stdIndex
	 *            - students layer final score or index
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
		
		return totIndex;
	}

	/**
	 * Encapsulates the deletion of all the expert weights, admin weights,
	 * individual results and overall results for the given item id, that is,
	 * deletes all the information stored on DB for the given item
	 * 
	 * @param context
	 *            DSpace context object
	 * @param itemId
	 *            item ID
	 * @throws SQLException
	 */
	public void deleteAllByItem(Context context, int itemId) throws SQLException {
		ExpertWeightingDao.getInstance().deleteAllByItem(context, itemId);
		DimensionWeightingDao.getInstance().deleteAllByItem(context, itemId);
		PersonalAssessmentDao.getInstance().deleteAllByItem(context, itemId);
		AssessmentResultDao.getInstance().deleteAllByItem(context, itemId);
	}

}
