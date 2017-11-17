package org.dspace.loa;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

public class AssessmentMetricDao {

	private AssessmentMetricDao() {
	}

	private static AssessmentMetricDao assessmentMetricDao;

	public static AssessmentMetricDao getInstance() {
		if (assessmentMetricDao == null)
			assessmentMetricDao = new AssessmentMetricDao();
		return assessmentMetricDao;
	}

	public List<AssessmentMetric> getAssessmentMerics(Context context, int itemId, int layerId) throws SQLException {
		
		String dbquery = " SELECT 															" +
				"   a.assessment_metric_id,													" +
				"   a.layer_id,																" +
				"   l.layer_name,															" +
				"   a.dimension_id,															" +
				"   d.dimension_name,														" +
				"   a.criteria_id,                                      					" +
				"   c.criteria_name,														" +
				"   CASE WHEN r.assessment_result_id IS NULL THEN 0 ELSE 1 END AS checked	" +
				" FROM assessment_metric a													" +
				"   INNER JOIN layer l ON a.layer_id = l.layer_id							" +
				"   INNER JOIN dimension d ON a.dimension_id = d.dimension_id				" +
				"   INNER JOIN criteria c ON a.criteria_id = c.criteria_id					" +
				"   LEFT JOIN assessment_result r ON a.assessment_metric_id = r.assessment_metric_id AND r.item_id = ? " +
				" WHERE a.layer_id = ? 														";

		TableRowIterator rows = DatabaseManager.query(context, dbquery, itemId, layerId);

		try {
			List<AssessmentMetric> metrics = new Vector<AssessmentMetric>();
			while(rows.hasNext()){
				TableRow row = rows.next();
				AssessmentMetric metric = new AssessmentMetric();
				metric.setId(row.getIntColumn("assessment_metric_id"));
				metric.setLayer(new Layer(row.getIntColumn("layer_id"), row.getStringColumn("layer_name")));
				metric.setDimension(new Dimension(row.getIntColumn("dimension_id"), row.getStringColumn("dimension_name")));
				metric.setCriteria(new Criteria(row.getIntColumn("criteria_id"), row.getStringColumn("criteria_name")));
				metric.setChecked(row.getIntColumn("checked") == 1 ? true : false);
				metrics.add(metric);
			}
			return metrics;
		} finally {
			if (rows != null)
				rows.close();
		}
	}
}
