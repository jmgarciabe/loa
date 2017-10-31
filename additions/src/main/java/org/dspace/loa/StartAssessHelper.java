package org.dspace.loa;

import java.sql.SQLException;
import java.util.Vector;

import org.dspace.core.Context;

public class StartAssessHelper {

	/**
	 * Retrieve the name of the Dimensions contained in the list of AssessParams
	 * objects for the given layer
	 * 
	 * @param context
	 *            - DSPace context
	 * @param assessParamList
	 *            - List of AssessParams objects
	 * @param layerId
	 *            - id of the layer
	 * @return String list of dimensions
	 */
	public Vector<String> getDimensions(Context context, Vector<AssessParam> assessParamList, int layerId) {
		Vector<String> ckDimensions = new Vector<String>();
		String dimensionName = null;

		if (assessParamList != null) {
			for (int i = 0; i < assessParamList.size(); i++) {
				AssessParam assessParam = assessParamList.elementAt(i);
				if (assessParam.getLayerID() == layerId) {
					try {
						dimensionName = Dimension.findNameByID(context, assessParam.getDimID());
					} catch (SQLException e) {
						e.printStackTrace();
					}
					if (!ckDimensions.contains(dimensionName))
						ckDimensions.addElement(dimensionName);
				}
			}
		}

		return ckDimensions;
	}

	/**
	 * Retrive metric names contained in the list of AssessParam objects for the
	 * given layer
	 * 
	 * @param context
	 *            - DSpace context object
	 * @param assessParamList
	 *            - List of AssessParams
	 * @param layerId
	 *            - id of the layer
	 * @return
	 */
	public Vector<String> getMetrics(Context context, Vector<AssessParam> assessParamList, int layerId) {
		Vector<String> ckmetrics = new Vector<String>();
		String metricName = null;
		if (assessParamList != null) {
			for (int i = 0; i < assessParamList.size(); i++) {
				AssessParam assessParam = assessParamList.elementAt(i);
				if (assessParam.getLayerID() == layerId) {
					try {
						metricName = Metric.findNameByID(context, assessParam.getAssessMetricID());
					} catch (SQLException e) {
						e.printStackTrace();
					}
					if (!ckmetrics.contains(metricName))
						ckmetrics.addElement(metricName);
				}
			}
		}
		return ckmetrics;
	}

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
	public Vector<String> getLayers(Context context) throws SQLException {
		Vector<String> layerList = new Vector<String>();
		Layer[] layersRetrieved = Layer.findAllLayers(context);
		for (int i = 0; i < layersRetrieved.length; i++) {
			String layer = layersRetrieved[i].getName();
			if (!layerList.contains(layer))
				layerList.addElement(layer);
		}
		return layerList;
	}

	/**
	 * Retrieve dimension list from database for the given layer
	 * 
	 * @param context
	 *            - DSpace context object
	 * @param layer
	 *            - the layer name
	 * @return - List of dimension
	 */
	public Vector<Dimension> getDimensions(Context context, String layer) throws SQLException {

		Vector<Dimension> dimensionList = new Vector<Dimension>();
		Dimension[] dimensions = Dimension.findByLayer(context, layer);
		for (Dimension dim : dimensions) {
			dimensionList.add(dim);

		}
		return dimensionList;
	}

	/**
	 * Return a list of String with dimension names, using a List of String with
	 * comma separated values in which the first value is the layer id, and the
	 * second is the dimension id. Values are filtered to only return the
	 * dimension for the given layer
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
	public Vector<String> verifyCheckedDimensions(Context context, Vector<String> checkedMetrics, String layer) {

		Vector<String> ckDimensions = new Vector<String>();
		String dimensionName = null;
		int layerId = layer.equals("Administrator") ? 1 : layer.equals("Expert") ? 2 : layer.equals("Student") ? 3 : 0;

		// Now, we can add dimension from metrics recently checked to item's
		// assessment
		if (checkedMetrics != null) {
			for (int i = 0; i < checkedMetrics.size(); i++) {
				String metricInfo = checkedMetrics.elementAt(i).toString();
				String[] data = metricInfo.split(",");
				int tempLayerId = Integer.valueOf(data[0]).intValue();
				int dimensionId = Integer.valueOf(data[1]).intValue();

				if (tempLayerId == layerId) {
					try {
						dimensionName = Dimension.findNameByID(context, dimensionId);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (!ckDimensions.contains(dimensionName))
					ckDimensions.addElement(dimensionName);
			}
		}

		return ckDimensions;
	}

}
