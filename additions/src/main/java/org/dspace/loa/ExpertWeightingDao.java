package org.dspace.loa;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

/**
 * Data access object for dim_expert_weighting DB table
 * 
 * @author JavierG
 * 
 */
public class ExpertWeightingDao {

	/** unique static instance **/
	private static ExpertWeightingDao expWeightDao;

	private ExpertWeightingDao() {
	};

	/** singleton method to get unique instance **/
	public static ExpertWeightingDao getInstance() {
		if (expWeightDao == null) {
			expWeightDao = new ExpertWeightingDao();
		}
		return expWeightDao;
	}

	/**
	 * Adds or updates the given expert weighting object in the
	 * dim_expert_weighting DB table
	 * 
	 * @param context
	 *            DSpace context object
	 * @param expWeighting
	 *            expert weighting object which is going to be added or updated
	 * @throws SQLException
	 */
	public void saveWeight(Context context, ExpertWeighting expWeighting) throws SQLException {
		String query = "SELECT * FROM dim_expert_weighting WHERE dimension_weighting_id = ? AND eperson_id = ?";
		TableRow row = DatabaseManager.querySingleTable(context, "dim_expert_weighting", query, expWeighting.getDimWeightingId(),
				expWeighting.getExpertId());
		if (row == null) {
			row = DatabaseManager.row("dim_expert_weighting");
			row.setColumn("dimension_weighting_id", expWeighting.getDimWeightingId());
			row.setColumn("eperson_id", expWeighting.getExpertId());
			row.setColumn("expert_weight", expWeighting.getExpertWeight());
			DatabaseManager.insert(context, row);
		}else{
			row.setColumn("expert_weight", expWeighting.getExpertWeight());
			DatabaseManager.update(context, row);
		}
		context.commit();
	}

	/**
	 * Retrieves from DB all the expert weightings related to the given metric
	 * and item id
	 * 
	 * @param context
	 *            DSpace context object
	 * @param itemId
	 *            item ID
	 * @param metricId
	 *            assessment metric id
	 * @return list of assessment result objects
	 * @throws SQLException
	 */
	public List<ExpertWeighting> getWeightings(Context context, int metricId, int itemId) throws SQLException {
		String query = " SELECT e.dim_expert_weighting_id, e.dimension_weighting_id, e.eperson_id, e.expert_weight 	"
				+ " FROM dim_expert_weighting e 																	"
				+ " INNER JOIN dimension_weighting d ON e.dimension_weighting_id = d.dimension_weighting_id 		"
				+ " INNER JOIN assessment_metric m ON d.layer_id = m.layer_id AND d.dimension_id = m.dimension_id "
				+ " WHERE m.assessment_metric_id = ? AND d.item_id = ? 											";
		List<ExpertWeighting> weightings = new ArrayList<ExpertWeighting>();
		TableRowIterator rowIterator = null;
		try {
			rowIterator = DatabaseManager.query(context, query, metricId, itemId);
			while (rowIterator.hasNext()) {
				TableRow row = rowIterator.next();
				ExpertWeighting w = new ExpertWeighting();
				w.setId(row.getIntColumn("dim_expert_weighting_id"));
				w.setDimWeightingId(row.getIntColumn("dimension_weighting_id"));
				w.setExpertId(row.getIntColumn("eperson_id"));
				w.setExpertWeight(row.getIntColumn("expert_weight"));
				weightings.add(w);
			}
		} finally {
			if (rowIterator != null) {
				rowIterator.close();
			}
		}
		return weightings;
	}

	/**
	 * Deletes from DB all the expert weightings related to the given dimension
	 * weighting object
	 * 
	 * @param context
	 *            DSpace context object
	 * @param dimWeighting
	 *            Dimension weighting object
	 * @throws SQLException
	 */
	public void deleteByDimWeighting(Context context, DimensionWeighting dimWeighting) throws SQLException {
		String query = " DELETE FROM dim_expert_weighting WHERE dimension_weighting_id =  	"
				+ " (SELECT dimension_weighting_id FROM dimension_weighting WHERE layer_id = ? AND dimension_id = ? AND item_id = ?) ";
		DatabaseManager.updateQuery(context, query, dimWeighting.getLayer().getId(), dimWeighting.getDimension().getId(),
				dimWeighting.getItemId());
		context.commit();
	}

	/**
	 * Removes all expert weightings set for the given item
	 * 
	 * @param context
	 *            - DSpace context object
	 * @param itemId
	 *            - ID of the Item
	 * @throws SQLException
	 */
	public void deleteAllByItem(Context context, int itemId) throws SQLException {
		String query = " DELETE FROM dim_expert_weighting WHERE dimension_weighting_id IN 			"
				+ " (SELECT dimension_weighting_id FROM dimension_weighting WHERE item_id = ?) ";
		DatabaseManager.updateQuery(context, query, itemId);
		context.commit();
	}
}
