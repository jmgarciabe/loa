package org.dspace.loa;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.dspace.core.Context;

public class StartAssessHelper {

	/**
	 * Wrapper for getting the layer object for the given layer ID
	 * 
	 * @param context
	 *            DSpace context object
	 * @param layerId
	 *            layer ID
	 * @return layer object
	 * @throws SQLException
	 */
	public Layer findLayer(Context context, int layerId) throws SQLException {
		return LayerDao.getInstance().findLayer(context, layerId);
	}

	/**
	 * Wrapper for getting a list of all layer objects
	 * 
	 * @param context
	 *            DSpace context object
	 * @return layer objects list
	 * @throws SQLException
	 *             may throw SQLException while retrieving list from data model
	 */
	public List<Layer> getLayers(Context context) throws SQLException {
		List<Layer> layerList = LayerDao.getInstance().findAllLayers(context);
		return layerList;
	}

	/**
	 * Wrapper for getting the list of dimension objects for the given layer
	 * 
	 * @param context
	 *            DSpace context object
	 * @param layerId
	 *            layer ID
	 * @return List of dimension objects
	 */
	public List<Dimension> getDimensions(Context context, int layerId) throws SQLException {

		List<Dimension> dimensionList = DimensionDao.getInstance().findByLayer(context, layerId);
		return dimensionList;
	}

	/**
	 * Wrapper for getting form DB the list of dimension weighting for the given
	 * layer and item ID
	 * 
	 * @param context
	 *            DSpace context object
	 * @param layerId
	 *            layer ID
	 * @param itemId
	 *            item ID
	 * @return list of dimension weighting objects
	 * @throws SQLException
	 */
	public List<DimensionWeighting> getSelectedDimensions(Context context, int layerId, int itemId) throws SQLException {
		return DimensionWeightingDao.getInstance().getAssignedDimension(context, itemId, layerId);
	}

	/**
	 * Wrapper for getting from DB all the assessment metrics related to the
	 * given item and layer id
	 * 
	 * @param context
	 *            DSpace context object
	 * @param itemId
	 *            item ID
	 * @param layerId
	 *            layer ID
	 * @return List of assessment metrics
	 * @throws SQLException
	 */
	public List<AssessmentMetric> getAssessmentMetrics(Context context, int itemId, int layerId) throws SQLException {
		return AssessmentMetricDao.getInstance().getAssessmentMerics(context, itemId, layerId);
	}

	/**
	 * Return a list of dimensions objects contained among the list of
	 * assessment metrics objects
	 * 
	 * @param context
	 *            DSpace context object
	 * @param checkedMetrics
	 *            List of assessment metrics objects
	 * @return List of dimension objects
	 */
	public List<Dimension> getDimensionsInMetrics(List<AssessmentMetric> checkedMetrics) {

		List<Dimension> ckDimensions = new Vector<Dimension>();
		// Now, we can add dimension from metrics recently checked to item's
		// assessment
		for (AssessmentMetric metric : checkedMetrics) {
			if (metric.isChecked()) {
				boolean contained = false;
				Dimension dim = metric.getDimension();
				for (Dimension dimension : ckDimensions) {
					if (dimension.getId() == dim.getId()) {
						contained = true;
						break;
					}
				}
				if (!contained) {
					ckDimensions.add(dim);
				}
			}
		}

		return ckDimensions;
	}

	/**
	 * Updates admin weights with values given per dimension for the layer
	 * passed
	 * 
	 * @param context
	 *            DSpace context
	 * @param metrics
	 *            List of AssessmentMetrics with the updated information
	 * @param itemId
	 *            the item identifier
	 * @param layerId
	 *            identifier of the layer updated
	 * @param weightsPerDimension
	 *            Map with entries made up of dimension name and corresponding
	 *            weight
	 * @throws SQLException
	 */
	public void updateWeights(Context context, List<AssessmentMetric> metrics, int itemId, int layerId,
			Map<String, Double> weightsPerDimension) throws SQLException {

		List<Dimension> dimensionsList = getDimensions(context, layerId);
		DimensionWeighting dimW = new DimensionWeighting();
		dimW.getLayer().setId(layerId);
		dimW.setItemId(itemId);

		for (Dimension dimension : dimensionsList) {
			dimW.setDimension(dimension);
			boolean delete = true;
			for (Entry<String, Double> entry : weightsPerDimension.entrySet()) {
				if (dimension.getName().equals(entry.getKey())) {
					delete = false;
					break;
				}
			}

			if (delete) {
				ExpertWeightingDao.getInstance().deleteByDimWeighting(context, dimW);
				DimensionWeightingDao.getInstance().deleteDimensionWeighting(context, dimW);
			} else {
				Double weight = weightsPerDimension.get(dimension.getName());
				dimW.setAdminWeight(weight);
				DimensionWeightingDao.getInstance().updateAdminWeight(context, dimW);
			}
		}
	}

	/**
	 * Creates or deletes the metrics in the given list upon if have been or not
	 * checked. That is, creates or deletes the corresponding assessment_result
	 * register in DB
	 * 
	 * @param context
	 *            - Dspace context
	 * @param metrics
	 *            - List of AssessmentMetrics with the updated information
	 * @param itemId
	 *            - the item identifier
	 * @throws SQLException
	 */
	public void updateSelectedMetrics(Context context, List<AssessmentMetric> metrics, int itemId) throws SQLException {
		for (AssessmentMetric metric : metrics) {
			AssessmentResult result = new AssessmentResult(metric.getId(), itemId);
			if (metric.isChecked()) {
				AssessmentResultDao.getInstance().addAssessmentResult(context, result);
			} else {
				PersonalAssessmentDao.getInstance().deleteByAssessmentResult(context, result);
				AssessmentResultDao.getInstance().deleteAssessmentResult(context, result);
			}
		}
	}

}
