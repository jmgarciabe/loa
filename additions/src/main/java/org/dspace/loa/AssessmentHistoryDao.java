package org.dspace.loa;

import java.sql.SQLException;

import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;

public class AssessmentHistoryDao {

	private static AssessmentHistoryDao historyDao;

	private AssessmentHistoryDao() {
	}

	public static AssessmentHistoryDao getInstance() {
		if (historyDao == null) {
			historyDao = new AssessmentHistoryDao();
		}
		return historyDao;
	}

	public void addAssessment(Context context, AssessmentHistory assessment) throws SQLException {
		
		String assessmentResult = String.valueOf(assessment.getValue());
		String query = " SELECT * FROM assessment_history WHERE assessment_result_id = ? AND eperson_id = ? ";
		TableRow row = DatabaseManager.querySingleTable(context, "assessment_result", query, assessment.getResultId(),
				assessment.getPersonId());

		if (row != null) {
			//update row
			row.setColumn("assess_value", assessmentResult);
		} else {
			// Create a new row, and assign a data
			row = DatabaseManager.row("assessment_history");
			row.setColumn("assessment_result_id", assessment.getResultId());
			row.setColumn("eperson_id", assessment.getPersonId());
			row.setColumn("assess_value", assessmentResult);
		}
		// Save changes to the database
		DatabaseManager.insert(context, row);
		context.commit();
	}

}
