package org.dspace.loa;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

/**
 * Data access object for assessment_result DB table
 * 
 * @author JavierG
 * 
 */
public class DimensionWeightingDao {

	/** unique static instance **/
	private static DimensionWeightingDao dimWeightingDao;

	private DimensionWeightingDao() {
	};

	/** singleton method to get unique instance **/
	public static DimensionWeightingDao getInstance() {
		if (dimWeightingDao == null) {
			dimWeightingDao = new DimensionWeightingDao();
		}
		return dimWeightingDao;
	}

	/**
	 * Retrieves the corresponding DimensionWeighting objects set for a given item and layer
	 * @param context DSpace context object
	 * @param itemId item ID
	 * @param layerId layer ID
	 * @return
	 * @throws SQLException
	 */
	public List<DimensionWeighting> getAssignedDimension(Context context, int itemId, int layerId) throws SQLException {

		String query = " SELECT w.dimension_weighting_id, w.layer_id, l.layer_name, w.dimension_id, "
				+ "	 d.dimension_name, w.item_id, w.admin_weight "
				+ " FROM dimension_weighting w INNER JOIN dimension d ON w.dimension_id = d.dimension_id "
				+ " INNER JOIN layer l ON w.layer_id = l.layer_id WHERE w.item_id = ? AND w.layer_id = ? ";
		TableRowIterator iterator = DatabaseManager.query(context, query, itemId, layerId);
		List<DimensionWeighting> dimensions = new Vector<DimensionWeighting>();
		try {
			while (iterator.hasNext()) {
				TableRow row = iterator.next();
				DimensionWeighting dim = new DimensionWeighting();
				dim.setId(row.getIntColumn("dimension_weighting_id"));
				dim.getLayer().setId(row.getIntColumn("layer_id"));
				dim.getLayer().setName(row.getStringColumn("layer_name"));
				dim.getDimension().setId(row.getIntColumn("dimension_id"));
				dim.getDimension().setName(row.getStringColumn("dimension_name"));
				dim.setItemId(itemId);
				String value = row.getStringColumn("admin_weight");
				if(value != null && value.length() > 0){
					dim.setAdminWeight(Double.valueOf(value));
				}
				dimensions.add(dim);
			}
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
		return dimensions;
	}

	/**
	 * Inserts or updates a dimension weighting in DB dimension_weighting table
	 * 
	 * @param context
	 *            DSpace context object
	 * @param dimWeighting
	 *            The DimensionWeighting object which is going to be updated or
	 *            added
	 * @throws SQLException
	 */
	public void updateAdminWeight(Context context, DimensionWeighting dimWeighting) throws SQLException {

		String weight = String.valueOf(dimWeighting.getAdminWeight());
		String query = "SELECT * FROM dimension_weighting WHERE layer_id = ? AND dimension_id = ? AND item_id = ? ";
		TableRow row = DatabaseManager.querySingleTable(context, "dimension_weighting", query, dimWeighting.getLayer().getId(),
				dimWeighting.getDimension().getId(), dimWeighting.getItemId());

		if (row == null) {
			row = DatabaseManager.row("dimension_weighting");
			row.setColumn("layer_id", dimWeighting.getLayer().getId());
			row.setColumn("dimension_id", dimWeighting.getDimension().getId());
			row.setColumn("item_id", dimWeighting.getItemId());
			row.setColumn("admin_weight", weight);
			DatabaseManager.insert(context, row);

		} else {
			row.setColumn("admin_weight", weight);
			DatabaseManager.update(context, row);
		}

		context.commit();
	}

	/**
	 * Deletes corresponding row in dimension_weighting table for the given
	 * DimensionWeighting object
	 * 
	 * @param context
	 *            DSpace context object
	 * @param dimeWeighting
	 *            The DimensionWeighting object which is going to be deleted
	 * @throws SQLException
	 */
	public void deleteDimensionWeighting(Context context, DimensionWeighting dimeWeighting) throws SQLException {
		String dbquery = "DELETE FROM dimension_weighting WHERE " + " layer_id = ? AND dimension_id = ? AND item_id = ? ";
		DatabaseManager.updateQuery(context, dbquery, dimeWeighting.getLayer().getId(), dimeWeighting.getDimension().getId(),
				dimeWeighting.getItemId());
		context.commit();
	}

	/**
	 * Deletes all set dimension weighting for the given item in dimension_weighting table
	 * 
	 * @param context
	 *            DSpace context object
	 * @param itemId
	 *            item ID
	 * @throws SQLException
	 */
	public void deleteAllByItem(Context context, int itemId) throws SQLException {
		String query = "DELETE FROM  dimension_weighting WHERE item_id = ?";
		DatabaseManager.updateQuery(context, query, itemId);
		context.commit();
	}
}
