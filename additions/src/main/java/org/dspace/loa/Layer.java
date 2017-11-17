package org.dspace.loa;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

public class Layer {

	/** Identifier of the metric */
	private int id;

	/** Name of the metric */
	private String name;

	/**
	 * Construct a Layer from a given context and tablerow
	 * 
	 * @param context
	 * @param row
	 */
	public Layer(int id, String name){

		this.id = id;
		this.name = name;
	}
	
	public static Layer findLayer(Context context, int layerId) throws SQLException{
		String query = "SELECT * FROM layer WHERE layer_id = ?";
		
		TableRow row = DatabaseManager.querySingle(context, query, layerId);
		Layer layer = new Layer(row.getIntColumn("layer_id"), row.getStringColumn("layer_name"));
		return layer;
	}

	/**
	 * Inserts new assessment layer indexes attached to an item in DB
	 * 
	 * @param context
	 *            DSpace context object
	 */
	public static void addAssessIndexes(Context context, int itemID, double admIndex, double expIndex, double stdIndex,
			double totIndex) throws SQLException {
		Double admin = new Double(admIndex);
		String adminVal = admin.toString();

		Double expert = new Double(expIndex);
		String expVal = expert.toString();

		Double stud = new Double(stdIndex);
		String stdVal = stud.toString();

		Double total = new Double(totIndex);
		String totVal = total.toString();

		// Create a new row, and assign a primary key
		TableRow newRow = DatabaseManager.row("assessment_history");

		// Populates the new row with data from frontend
		newRow.setColumn("item_id", itemID);
		newRow.setColumn("admin_index", adminVal);
		newRow.setColumn("expert_index", expVal);
		newRow.setColumn("user_index", stdVal);
		newRow.setColumn("assess_value", totVal);

		// Save changes to the database
		DatabaseManager.insert(context, newRow);

		// Make sure all changes are committed
		context.commit();
	}

	/**
	 * updates assessment indexes in DB attached to an item
	 * 
	 * @param context
	 *            DSpace context object
	 * @param adminIndex
	 * @param expIndex
	 * @param stdIndex
	 * @param totIndex
	 * @param indexID
	 */
	public static void updateAssessIndexes(Context context, double adminIndex, double expIndex, double stdIndex, double totIndex,
			int indexID) {
		// TODO Auto-generated method stub
		String dbquery = "SELECT * FROM assessment_history " + "WHERE assessment_history_id = ? ";

		Double admin = new Double(adminIndex);
		String adminVal = admin.toString();

		Double expert = new Double(expIndex);
		String expVal = expert.toString();

		Double stud = new Double(stdIndex);
		String stdVal = stud.toString();

		Double total = new Double(totIndex);
		String totVal = total.toString();

		try {
			TableRow updateable = DatabaseManager.querySingle(context, dbquery, indexID);
			updateable.setTable("assessment_history");
			updateable.setColumn("admin_index", adminVal);
			updateable.setColumn("expert_index", expVal);
			updateable.setColumn("user_index", stdVal);
			updateable.setColumn("assess_value", totVal);

			// Save changes to the database
			DatabaseManager.update(context, updateable);

			// Make sure all changes are committed
			context.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Deletes all assessment indexes attached to an item in DB
	 * 
	 * @param context
	 *            DSpace context object
	 */
	public static int deleteAssessIndexes(Context context, int itemID) throws SQLException {
		int rowsAffected = 0;

		String dbquery = "DELETE FROM assessment_history " + "WHERE item_id = ? ";

		try {
			// Deletes rows with data from frontend
			rowsAffected = DatabaseManager.updateQuery(context, dbquery, itemID);

			// Make sure all changes are committed
			context.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rowsAffected;
	}

	/**
	 * Finds all layers in DB
	 * 
	 * @param context
	 * @param dimID
	 * 
	 * @return layer objects
	 */
	public static List<Layer> findAllLayers(Context context) throws SQLException {
		
		String dbquery = "SELECT l.* FROM layer l ";

		TableRowIterator rowsIterator = DatabaseManager.query(context, dbquery);
		List<Layer> layerList = new Vector<Layer>(); 
		
		try {
			while(rowsIterator.hasNext()){
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

	/**
	 * Finds assessment indexes taking into account its itemID
	 * 
	 * @param context
	 * @param itemID
	 * 
	 * @return dimension name string for a specific dimension ID
	 */
	public static int findIndexByItem(Context context, int itemID) throws SQLException {
		String dbquery = "SELECT * FROM assessment_history " + "WHERE item_id = ? ";

		TableRow row = DatabaseManager.querySingle(context, dbquery, itemID);

		if (row == null)
			return -1;
		else {
			try {
				int indexID = row.getIntColumn("assessment_history_id");
				return indexID;
			} finally {

			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
