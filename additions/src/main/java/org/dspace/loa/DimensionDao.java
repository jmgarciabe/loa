package org.dspace.loa;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

/**
 * Data access object for dimension DB table
 * 
 * @author JavierG
 * 
 */
public class DimensionDao {

	/** unique static instance **/
	private static DimensionDao dimDao;

	private DimensionDao() {
	}

	/** singleton method to get unique instance **/
	public static DimensionDao getInstance() {
		if (dimDao == null) {
			dimDao = new DimensionDao();
		}
		return dimDao;
	}

	/**
	 * Finds all dimensions related to a specific layer
	 * 
	 * @param context
	 *            DSpace context object
	 * @param layerId
	 *            layer ID
	 * @return List of dimension objects
	 */
	public List<Dimension> findByLayer(Context context, int layerId) throws SQLException {

		String dbquery = " SELECT dimension_id, dimension_name FROM dimension WHERE dimension_id IN "
						+" (Select dimension_id FROM layer2dimension WHERE layer_id = ?) 			";
		TableRowIterator rowsIterator = DatabaseManager.query(context, dbquery, layerId);
		try {
			List<Dimension> dimensionList = new Vector<Dimension>();
			while (rowsIterator.hasNext()) {
				TableRow row = rowsIterator.next();
				Dimension dim = new Dimension(row.getIntColumn("dimension_id"), row.getStringColumn("dimension_name"));
				dimensionList.add(dim);
			}
			return dimensionList;
		} finally {
			if (rowsIterator != null)
				rowsIterator.close();
		}
	}
}
