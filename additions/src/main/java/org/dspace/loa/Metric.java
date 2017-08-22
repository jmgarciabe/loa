package org.dspace.loa;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

public class Metric extends DSpaceObject {
	private int ID;

	private String name;

	/** The row in the table representing this object */
	private final TableRow myRow;
	/** log4j logger */
	private static final Logger log = Logger.getLogger(Metric.class);

	/**
	 * Construct a Metric from a given context and tablerow
	 * 
	 * @param context
	 * @param row
	 */
	Metric(Context context, TableRow row) throws SQLException {
		super(context);

		// Ensure that my TableRow is typed.
		if (null == row.getTable())
			row.setTable("assessment_metric");

		myRow = row;
		ID = row.getIntColumn("assessment_metric_id");
		name = row.getStringColumn("criteria_name");

		// Cache ourselves
		context.cache(this, row.getIntColumn("assessment_metric_id"));

		clearDetails();
	}

	/**
	 * Inserts a new assessment metric attached to an item in DB
	 * 
	 * @param context
	 *            DSpace context object
	 */
	public static void addAssessMetric(Context context, int metricID, int itemID) throws SQLException {
		// Check if the row already exist
		String query = "SELECT * FROM assessment_result " + "WHERE assessment_metric_id = ? AND item_id = ? ";

		TableRow row = DatabaseManager.querySingleTable(context, "assessment_result", query, metricID, itemID);

		if (row != null) {
			return;
		}

		// Create a new row, and assign a data
		TableRow newRow = DatabaseManager.row("assessment_result");
		
		newRow.setColumn("assessment_metric_id", metricID);
		newRow.setColumn("item_id", itemID);

		// Save changes to the database
		DatabaseManager.insert(context, newRow);

		// Make sure all changes are committed
		context.commit();
	}

	/**
	 * Saves assessment value in DB, obtained via perfom method in each
	 * assessment class
	 * 
	 * @param context
	 *            DSpace context object
	 */
	public static void addAssessValue(Context context, double result, String metric, int layerID, int itemID) throws SQLException {
		String dbupdate = "UPDATE assessment_result SET metric_value = ? " + "WHERE assessment_metric_id = "
				+ "( SELECT b.assessment_metric_id "
				+ "  FROM assessment_metric b INNER JOIN criteria c ON b.criteria_id = c.criteria_id "
				+ "  WHERE c.criteria_name = ? AND b.layer_id = ? " + ") " + "AND item_id = ? ";
		String assessVal = String.valueOf(result);

		DatabaseManager.updateQuery(context, dbupdate, assessVal, metric, layerID, itemID);
		context.commit();

	}

	/**
	 * Deletes the given assessment result
	 * 
	 * @param context
	 *            dspace context to execute db operation
	 * @param metricID
	 *            metric id of the assessment result
	 * @param itemID
	 *            item id of the assessment resutl
	 */
	public static void deleteAssessMetric(Context context, int metricID, int itemID) throws SQLException {
		// Check if the row already exist
		String query = "DELETE FROM assessment_result WHERE assessment_metric_id = ? AND item_id = ? ";
		
		int afectedRows = DatabaseManager.updateQuery(context, query, metricID, itemID);
		
		System.out.println("Deleted " + afectedRows + " rows from assessment result");
		// Make sure all changes are committed
		context.commit();
	}

	/**
	 * Deletes all assessment values attached to an item in DB
	 * 
	 * @param context
	 *            DSpace context object
	 */
	public static int deleteAssessValues(Context context, int itemID) throws SQLException {
		int rowsAffected = 0;

		String dbquery = "DELETE FROM assessment_result " + "WHERE item_id = ? ";

		// Deletes rows with data from frontend
		rowsAffected = DatabaseManager.updateQuery(context, dbquery, itemID);

		// Make sure all changes are committed
		context.commit();

		return rowsAffected;
	}

	/**
	 * Finds all criteria attached to a specific dimension - assumes name is
	 * unique
	 * 
	 * @param context
	 * @param layerName
	 * 
	 * @return array of all dimensions by a specific layer
	 */
	public static Vector<Metric> findByLayer(Context context, String layerName) throws SQLException {

		String dbquery = "SELECT a.assessment_metric_id,c.criteria_name FROM criteria c "
				+ "INNER JOIN assessment_metric a ON a.criteria_id=c.criteria_id "
				+ "AND a.layer_id = (select layer_id from layer where layer_name= ?) ";

		TableRowIterator rows = DatabaseManager.query(context, dbquery, layerName);

		try {
			List<TableRow> dRows = rows.toList();

			Vector<Metric> criteria = new Vector<Metric>();

			for (int i = 0; i < dRows.size(); i++) {
				TableRow row = dRows.get(i);

				// First check the cache
				Metric fromCache = (Metric) context.fromCache(Metric.class, row.getIntColumn("assessment_metric_id"));

				if (fromCache != null) {
					criteria.add(fromCache);
				} else {
					criteria.add(new Metric(context, row));
				}
			}

			return criteria;
		} finally {
			if (rows != null)
				rows.close();
		}
	}

	/**
	 * Finds criteria name taking into account its ID
	 * 
	 * @param context
	 * @param metricID
	 * 
	 * @return criteria name string for a specific metric ID
	 */
	public static String findNameByID(Context context, int metricID) throws SQLException {
		String dbquery = "SELECT c.criteria_name FROM criteria c "
				+ "INNER JOIN assessment_metric a ON a.criteria_id=c.criteria_id " + "AND a.assessment_metric_id = ? ";

		TableRow row = DatabaseManager.querySingle(context, dbquery, metricID);

		try {
			String criteriaName = row.getStringColumn("criteria_name");
			return criteriaName;
		} finally {

		}
	}

	public void setID(int iD) {
		ID = iD;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return ID;
	}

	@Override
	public String getHandle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public void update() throws SQLException, AuthorizeException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateLastModified() {
		// TODO Auto-generated method stub

	}

}
