package org.dspace.loa;

import java.sql.SQLException;

import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;

public class AssessmentResultDao {

	private static AssessmentResultDao resultDao;

	private AssessmentResultDao() {
	}

	public static AssessmentResultDao getInstance() {
		if (resultDao == null)
			resultDao = new AssessmentResultDao();
		return resultDao;
	}

	public void addAssessmentResult(Context context, AssessmentResult result) throws SQLException {

		String query = " SELECT * FROM assessment_result WHERE assessment_metric_id = ? AND item_id = ? ";
		TableRow row = DatabaseManager.querySingleTable(context, "assessment_result", query, result.getAssessmentMetricId(),
				result.getItemId());

		if (row != null) {
			return;
		}

		// Create a new row, and assign a data
		TableRow newRow = DatabaseManager.row("assessment_result");
		newRow.setColumn("assessment_metric_id", result.getAssessmentMetricId());
		newRow.setColumn("item_id", result.getItemId());

		// Save changes to the database
		DatabaseManager.insert(context, newRow);
		context.commit();
	}

	public void saveAssesmentResult(Context context, AssessmentResult result) throws SQLException{
		String dbupdate = "UPDATE assessment_result SET metric_value = ? " + "WHERE assessment_metric_id = ? "
				+ "AND item_id = ? ";
		String assessVal = String.valueOf(result.getValue());
		DatabaseManager.updateQuery(context, dbupdate, assessVal, result.getAssessmentMetricId(), result.getItemId());
		context.commit();
	}

	public void deleteAssessmentResult(Context context, AssessmentResult result) throws SQLException {
		String query = "DELETE FROM assessment_result WHERE assessment_metric_id = ? AND item_id = ? ";
		DatabaseManager.updateQuery(context, query, result.getAssessmentMetricId(), result.getItemId());
		context.commit();
	}
}
