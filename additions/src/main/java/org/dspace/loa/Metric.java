package org.dspace.loa;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

public class Metric {
	
	/** Identifier of the metric */
	private int id;
	
	/** Name of the metric */
	private String name;

	/**
	 * Construct a Metric from a given context and tablerow
	 * 
	 * @param context
	 * @param row
	 */
	public Metric(Context context, TableRow row) throws SQLException {

		// Ensure that my TableRow is typed.
		if (row.getTable() == null){
			row.setTable("assessment_metric");
		}

		id = row.getIntColumn("assessment_metric_id");
		name = row.getStringColumn("criteria_name");

		// Cache ourselves
		context.cache(this, row.getIntColumn("assessment_metric_id"));

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

	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

}
