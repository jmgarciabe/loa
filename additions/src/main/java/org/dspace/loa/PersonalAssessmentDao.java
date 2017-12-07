package org.dspace.loa;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

/**
 * Data access object for personal_assessment DB table
 * 
 * @author JavierG
 * 
 */
public class PersonalAssessmentDao {

	/** unique static instance **/
	private static PersonalAssessmentDao personalAssessDao;

	private PersonalAssessmentDao() {
	}

	/** singleton method to get unique instance **/
	public static PersonalAssessmentDao getInstance() {
		if (personalAssessDao == null) {
			personalAssessDao = new PersonalAssessmentDao();
		}
		return personalAssessDao;
	}

	/**
	 * Creates a personal assessment in the personal_assessment DB table
	 * 
	 * @param context
	 *            DSpace context object
	 * @param assessment
	 *            personal assessment which is going to be inserted
	 * @throws SQLException
	 */
	public void addAssessment(Context context, PersonalAssessment assessment) throws SQLException {

		String assessmentResult = String.valueOf(assessment.getValue());
		String query = " SELECT * FROM personal_assessment WHERE assessment_result_id = ? AND eperson_id = ? ";
		TableRow row = DatabaseManager.querySingleTable(context, "personal_assessment", query, assessment.getResultId(),
				assessment.getPersonId());

		if (row != null) {
			// update row
			row.setColumn("assess_value", assessmentResult);
			DatabaseManager.update(context, row);
		} else {
			// Create a new row, and assign a data
			row = DatabaseManager.row("personal_assessment");
			row.setColumn("assessment_result_id", assessment.getResultId());
			row.setColumn("eperson_id", assessment.getPersonId());
			row.setColumn("assess_value", assessmentResult);
			DatabaseManager.insert(context, row);
		}
		// Save changes to the database
		context.commit();
	}

	/**
	 * Retrieves all the PersonalAssessments related to the given metric id from
	 * DB
	 * 
	 * @param context
	 *            DSpace context object
	 * @param resultId
	 *            ID of the assessment metric
	 * @return List of personal assessments
	 * @throws SQLException
	 */
	public List<PersonalAssessment> getByAssessmentResult(Context context, int resultId) throws SQLException {
		String query = "SELECT * FROM personal_assessment WHERE assessment_result_id = ?";
		List<PersonalAssessment> historyList = new ArrayList<PersonalAssessment>();
		TableRowIterator rowIterator = null;
		try {
			rowIterator = DatabaseManager.query(context, query, resultId);
			while (rowIterator.hasNext()) {
				TableRow row = rowIterator.next();
				PersonalAssessment result = new PersonalAssessment();
				result.setId(row.getIntColumn("personal_assessment_id"));
				result.setResultId(row.getIntColumn("assessment_result_id"));
				result.setPersonId(row.getIntColumn("eperson_id"));
				String value = row.getStringColumn("assess_value");
				if (value != null && value.length() > 0) {
					result.setValue(Double.valueOf(value));
				}
				historyList.add(result);
			}
		} finally {
			if (rowIterator != null) {
				rowIterator.close();
			}
		}
		return historyList;
	}
	
	/**
	 * Deletes all the PersonalAssessments related to the given assessment result 
	 * @param context - DSpace context object
	 * @param result - assessment result object
	 * @throws SQLException
	 */
	public void deleteByAssessmentResult(Context context, AssessmentResult result) throws SQLException {
		String query = " DELETE FROM personal_assessment WHERE assessment_result_id = "
					+" (SELECT assessment_result_id FROM assessment_result WHERE assessment_metric_id = ? AND item_id = ? )";
		DatabaseManager.updateQuery(context, query, result.getAssessmentMetric().getId(), result.getItemId());
		context.commit();
	}

	/**
	 * Deletes all registers associated to the given item in the
	 * personal_assessment DB table
	 * 
	 * @param context
	 *            DSpace context object
	 * @param itemId
	 *            item ID
	 * @throws SQLException
	 */
	public void deleteAllByItem(Context context, int itemId) throws SQLException {
		String query = " DELETE FROM personal_assessment WHERE assessment_result_id IN 			"
				+ " (SELECT assessment_result_id FROM assessment_result WHERE item_id = ?)	";
		DatabaseManager.updateQuery(context, query, itemId);
		context.commit();
	}

}
