package org.dspace.loa;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

public class Dimension{

	private int id;

	private String name;

	/**
	 * Construct a Dimension from a given context and tablerow
	 * 
	 * @param context
	 * @param row
	 */
	public Dimension(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	/**
	 * Finds all dimensions attached to a specific layer - assumes name is
	 * unique
	 * 
	 * @param context
	 * @param layerId
	 * 
	 * @return array of all dimensions by a specific layer
	 */
	public static List<Dimension> findByLayer(Context context, int layerId) throws SQLException {
		
		String dbquery = "SELECT  									" +
						"dimension_id,								" + 
						"dimension_name								" +
						"FROM dimension								" +
						"WHERE dimension_id IN (Select dimension_id FROM layer2dimension WHERE layer_id = ?) ";

		TableRowIterator rowsIterator = DatabaseManager.query(context, dbquery, layerId);

		try {
			
			List<Dimension> dimensionList = new Vector<Dimension>();
			while(rowsIterator.hasNext()){
				TableRow row = rowsIterator.next();
				Dimension dim = new Dimension(row.getIntColumn("dimension_id"),row.getStringColumn("dimension_name"));
				dimensionList.add(dim);
				
			}
			return dimensionList;
			
		} finally {
			if (rowsIterator != null)
				rowsIterator.close();
		}
	}

	/**
	 * Finds dimension name taking into account its ID
	 * 
	 * @param context
	 * @param dimID
	 * 
	 * @return dimension name string for a specific dimension ID
	 */
	public static String findNameByID(Context context, int dimID) throws SQLException {
		String dbquery = "SELECT dimension_name FROM dimension " + "WHERE dimension_id = ? ";
		TableRow row = DatabaseManager.querySingle(context, dbquery, dimID);
		String dimensionName = row.getStringColumn("dimension_name");
		return dimensionName;
	}


	/**
	 * Inserts a new weight in DB attached to a dimension
	 * 
	 * @param context
	 *            DSpace context object
	 * @param dimID
	 * @param layID
	 * @param itemID
	 * @param dimWeight
	 */
	public static void updateAdminWeight(Context context, int dimID, int layID, int itemID, double dimWeight)
			throws SQLException {
		// Verificar si ya existe la fila y actualizarla o crearla
		TableRow row;
		String weight = String.valueOf(dimWeight);
		String query = "select * from dimension_weighting where layer_id = ?" + " and dimension_id = ? and item_id = ? ";

		row = DatabaseManager.querySingleTable(context, "dimension_weighting", query, layID, dimID, itemID);
		if (row == null) {
			row = DatabaseManager.row("dimension_weighting");
			row.setColumn("layer_id", layID);
			row.setColumn("dimension_id", dimID);
			row.setColumn("item_id", itemID);
			row.setColumn("admin_weight", weight);
			DatabaseManager.insert(context, row);

		} else {
			row.setColumn("admin_weight", weight);
			DatabaseManager.update(context, row);
		}
		// Make sure all changes are committed
		context.commit();

	}
	
	/**
	 * updates expert weight in DB attached to a dimension
	 * 
	 * @param context
	 *            DSpace context object
	 * @param dimWghtID
	 * @param itemID
	 * @param weight
	 */
	public static void updateExpertWeight(Context context, int dimWghtID, int itemID, int weight) {
		String dbquery = "update dimension_weighting set expert_weight = ?" + "where dimension_weighting_id = ? "
				+ "and item_id = ? ";
		try {
			DatabaseManager.updateQuery(context, dbquery, weight, dimWghtID, itemID);
			context.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Deletes corresponding row in dimension_weighting table for the given data  
	 * @param context - DSpace context object required for perform database transactions 
	 * @param layerId - Layer ID of the row 
	 * @param dimensionId - Dimension ID of the row
	 * @param itemID - item ID of the row
	 * @return numbers of rows affected (Must be 0 or 1)
	 * @throws SQLException
	 */
	public static int deleteDimensionWeighting(Context context, int layerId, int dimensionId, int itemID) throws SQLException {
		String dbquery = "DELETE FROM dimension_weighting WHERE " + 
							" layer_id = ? AND dimension_id = ? AND item_id = ? ";
		// Deletes rows with data 
		int rowsAffected = DatabaseManager.updateQuery(context, dbquery, layerId, dimensionId, itemID);
		// Make sure all changes are committed
		context.commit();
		return rowsAffected;
	}

	/**
	 * Deletes all assessment weights attached to an item in DB
	 * 
	 * @param context
	 *            DSpace context object
	 */
	public static int deleteAssessWeights(Context context, int itemID) throws SQLException {
		String dbquery = "DELETE FROM dimension_weighting " + "WHERE item_id = ? ";
		// Deletes rows with data from frontend
		int rowsAffected = DatabaseManager.updateQuery(context, dbquery, itemID);
		// Make sure all changes are committed
		context.commit();
		return rowsAffected;
	}

	/**
	 * Deletes all dimension weightings for a given layer and item
	 * 
	 * @param context
	 *            DSpace Context
	 * @param itemId
	 *            item id
	 * @param layerId
	 *            layer id
	 * @return
	 * @throws SQLException
	 */
	public static int deleteWeightsById(Context context, int itemId, int layerId) throws SQLException {
		String dbquery = "DELETE FROM dimension_weighting WHERE item_id = ? AND layer_id = ? ";
		// Deletes rows with data from frontend
		int rowsAffected = DatabaseManager.updateQuery(context, dbquery, itemId, layerId);
		// Make sure all changes are committed
		context.commit();
		return rowsAffected;
	}


	public int getId() {
		return id;
	}

	public void setId(int iD) {
		id = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
