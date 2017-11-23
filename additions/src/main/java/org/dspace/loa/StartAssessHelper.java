package org.dspace.loa;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.dspace.core.Context;

public class StartAssessHelper {

	
	/**
	 * Return a list of layer names
	 * 
	 * @param context
	 *            DSpace context object
	 * @return Layer list
	 * @throws SQLException
	 *             - may throw SQLException while retrieving list from data
	 *             model
	 */
	public List<Layer> getLayers(Context context) throws SQLException {
		List<Layer> layerList = Layer.findAllLayers(context);
		return layerList;
	}

	/**
	 * Retrieve dimension list from database for the given layer
	 * 
	 * @param context
	 *            - DSpace context object
	 * @param layerId
	 *            - the layer name
	 * @return - List of dimension
	 */
	public List<Dimension> getDimensions(Context context, int layerId) throws SQLException {

		List<Dimension> dimensionList = Dimension.findByLayer(context, layerId);
		return dimensionList;
	}

	/**
	 * Return a list of Dimensions contained among the list of AssessmentMetric
	 * 
	 * @param context
	 *            DSpace context object
	 * @param checkedMetrics
	 *            List of String with comma separated values in which first
	 *            values is layer id and second is dimension id
	 * @param layer
	 *            Layer name
	 * @return List of dimension names listed in the input
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
	 * Creates or deletes the metrics in the given list upon if have been or not
	 * checked. That is, creates or deletes the corresponding AssessmentResult
	 * register
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
				AssessmentResultDao.getInstance().deleteAssessmentResult(context, result);
			}
		}
	}

	/**
	 * Updates admin weights with values given per dimension for the layer
	 * passed
	 * 
	 * @param context
	 *            - Dspace context
	 * @param metrics
	 *            - List of AssessmentMetrics with the updated information
	 * @param itemId
	 *            - the item identifier
	 * @param layerId
	 *            - identifier of the layer updated
	 * @param weightsPerDimension
	 *            - Map with entries made up of dimension name and corresponding
	 *            weight
	 * @throws SQLException
	 */
	public void updateAdminWeights(Context context, List<AssessmentMetric> metrics, int itemId, int layerId,
			Map<String, Double> weightsPerDimension) throws SQLException {

		List<Dimension> dimensionsList = getDimensions(context, layerId);

		for (Dimension dimension : dimensionsList) {
			boolean delete = true;
			for (AssessmentMetric metric : metrics) {
				if (dimension.getId() == metric.getDimension().getId() && metric.isChecked()) {
					delete = false;
					break;
				}
			}
			if (delete) {
				Dimension.deleteDimensionWeighting(context, layerId, dimension.getId(), itemId);
			} else {
				Double weight = weightsPerDimension.get(dimension.getName());
				Dimension.updateAdminWeight(context, dimension.getId(), layerId, itemId, weight);
			}
		}
	}

}
