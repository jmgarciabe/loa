package org.dspace.loa;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
public class AssessmentResultDao {

	/** unique static instance **/
	private static AssessmentResultDao resultDao;

	private AssessmentResultDao() {
	}

	/** singleton method to get unique instance **/
	public static AssessmentResultDao getInstance() {
		if (resultDao == null)
			resultDao = new AssessmentResultDao();
		return resultDao;
	}

	/**
	 * Add assessment result in the assessment_result DB table
	 * 
	 * @param context
	 *            DSpace context object
	 * @param result
	 *            assessment result object which is going to be inserted
	 * @throws SQLException
	 */
	public void addAssessmentResult(Context context, AssessmentResult result) throws SQLException {

		String query = " SELECT * FROM assessment_result WHERE assessment_metric_id = ? AND item_id = ? ";
		TableRow row = DatabaseManager.querySingleTable(context, "assessment_result", query,
				result.getAssessmentMetric().getId(), result.getItemId());

		if (row != null) {
			return;
		}

		// Create a new row, and assign a data
		TableRow newRow = DatabaseManager.row("assessment_result");
		newRow.setColumn("assessment_metric_id", result.getAssessmentMetric().getId());
		newRow.setColumn("item_id", result.getItemId());

		// Save changes to the database
		DatabaseManager.insert(context, newRow);
		context.commit();
	}

	/**
	 * Updates the assessment value in the corresponding assessment result
	 * register
	 * 
	 * @param context
	 *            DSpace context object
	 * @param result
	 *            assessment result object which is going to be inserted
	 * @throws SQLException
	 */
	public void saveAssesmentResult(Context context, AssessmentResult result) throws SQLException {
		String dbupdate = "UPDATE assessment_result SET metric_value = ? " + "WHERE assessment_metric_id = ? "
				+ " AND item_id = ? ";
		String assessVal = String.valueOf(result.getValue());
		DatabaseManager.updateQuery(context, dbupdate, assessVal, result.getAssessmentMetric().getId(), result.getItemId());
		context.commit();
	}

	/**
	 * Retrieves the assessment result object from assessment_result DB table
	 * for the given pair of metric and item ID
	 * 
	 * @param context
	 *            DSpace context object
	 * @param metricId
	 *            assessment metric ID
	 * @param itemId
	 *            item ID
	 * @return assessment result object
	 * @throws SQLException
	 */
	public AssessmentResult getAssessmentResult(Context context, int metricId, int itemId) throws SQLException {
		String query = "SELECT * FROM assessment_result WHERE assessment_metric_id = ? AND item_id = ? ";
		TableRow row = DatabaseManager.querySingleTable(context, "assessment_result", query, metricId, itemId);
		if (row == null) {
			return null;
		}
		AssessmentResult result = new AssessmentResult(row.getIntColumn("assessment_metric_id"), row.getIntColumn("item_id"));
		result.setId(row.getIntColumn("assessment_result_id"));
		String value = row.getStringColumn("metric_value");
		if(value != null && value.length() > 0){
			result.setValue(Double.valueOf(value));
		}
		return result;
	}

	/**
	 * Retrieves from DB all the assessment results related to the given item id
	 * 
	 * @param context
	 *            DSpace context object
	 * @param itemId
	 *            item ID
	 * @return list of assessment result objects
	 * @throws SQLException
	 */
	public List<AssessmentResult> getAssessmentResultsByItem(Context context, int itemId) throws SQLException {
		String query = "  SELECT r.assessment_result_id, r.assessment_metric_id, r.item_id, r.metric_value, "
				+ " 		m.layer_id, m.dimension_id, d.dimension_name, m.criteria_id, c.criteria_name 	"
				+ " FROM assessment_result r 															"
				+ " INNER JOIN assessment_metric m ON r.assessment_metric_id = m.assessment_metric_id "
				+ " INNER JOIN dimension d ON m.dimension_id = d.dimension_id 							" 
				+ " INNER JOIN criteria c ON m.criteria_id = c.criteria_id 							" 
				+ " WHERE   r.item_id = ? ";
		TableRowIterator rowIterator = null;
		List<AssessmentResult> results = new ArrayList<AssessmentResult>();
		try {
			rowIterator = DatabaseManager.query(context, query, itemId);
			while (rowIterator.hasNext()) {
				TableRow row = rowIterator.next();
				AssessmentResult r = new AssessmentResult(row.getIntColumn("assessment_metric_id"), row.getIntColumn("item_id"));
				r.setId(row.getIntColumn("assessment_result_id"));
				String value = row.getStringColumn("metric_value");
				if(value != null && value.length() > 0){
					r.setValue(Double.valueOf(value));
				}
				Layer l = new Layer();
				l.setId(row.getIntColumn("layer_id"));
				Dimension d = new Dimension(row.getIntColumn("dimension_id"), row.getStringColumn("dimension_name"));
				Criteria c = new Criteria(row.getIntColumn("criteria_id"), row.getStringColumn("criteria_name"));
				r.getAssessmentMetric().setLayer(l);
				r.getAssessmentMetric().setDimension(d);
				r.getAssessmentMetric().setCriteria(c);
				results.add(r);
			}
		} finally {
			if (rowIterator != null) {
				rowIterator.close();
			}
		}
		return results;
	}

	/**
	 * Deletes the given assessment result in DB
	 * 
	 * @param context
	 *            Dspace context object
	 * @param result
	 *            the assessment result object to be deleted
	 * @throws SQLException
	 */
	public void deleteAssessmentResult(Context context, AssessmentResult result) throws SQLException {
		String query = "DELETE FROM assessment_result WHERE assessment_metric_id = ? AND item_id = ? ";
		DatabaseManager.updateQuery(context, query, result.getAssessmentMetric().getId(), result.getItemId());
		context.commit();
	}

	/**
	 * Deletes all assessment results associated to an item
	 * 
	 * @param context
	 *            DSpace context object
	 * @param itemId
	 *            item ID
	 * @throws SQLException
	 */
	public void deleteAllByItem(Context context, int itemId) throws SQLException {

		String dbquery = "DELETE FROM assessment_result " + "WHERE item_id = ? ";
		DatabaseManager.updateQuery(context, dbquery, itemId);
		context.commit();

	}
}
