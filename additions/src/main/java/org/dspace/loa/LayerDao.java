package org.dspace.loa;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

/**
 * Data access object to layer DB table
 * 
 * @author JavierG
 * 
 */
public class LayerDao {

	/** unique static instance **/
	private static LayerDao layerDao;

	private LayerDao() {
	};

	/** singleton method to get unique instance **/
	public static LayerDao getInstance() {
		if (layerDao == null) {
			layerDao = new LayerDao();
		}
		return layerDao;
	}

	/**
	 * Return the layer object for the given layer ID
	 * 
	 * @param context
	 *            DSpace context object
	 * @param layerId
	 *            layer ID
	 * @return layer object
	 * @throws SQLException
	 */
	public Layer findLayer(Context context, int layerId) throws SQLException {
		String query = "SELECT * FROM layer WHERE layer_id = ?";
		TableRow row = DatabaseManager.querySingle(context, query, layerId);
		Layer layer = new Layer(row.getIntColumn("layer_id"), row.getStringColumn("layer_name"));
		return layer;
	}

	/**
	 * Finds all layers in DB
	 * 
	 * @param context
	 *            DSpace context object
	 * @return List of layer objects
	 */
	public List<Layer> findAllLayers(Context context) throws SQLException {

		String dbquery = "SELECT l.* FROM layer l ";
		TableRowIterator rowsIterator = DatabaseManager.query(context, dbquery);
		List<Layer> layerList = new Vector<Layer>();
		try {
			while (rowsIterator.hasNext()) {
				TableRow row = rowsIterator.next();
				Layer layer = new Layer(row.getIntColumn("layer_id"), row.getStringColumn("layer_name"));
				layerList.add(layer);
			}
			return layerList;
		} finally {
			if (rowsIterator != null)
				rowsIterator.close();
		}

	}

}
