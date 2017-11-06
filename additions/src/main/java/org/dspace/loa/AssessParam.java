package org.dspace.loa;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;
//import org.dspace.app.webui.util.JSPManager;

public class AssessParam{

	private int assessMetricId, itemId, dimWeightId, layerId, dimId, expWeight;

	private String metricValue, admWeight;

	AssessParam(Context context, TableRow row) {

		// Ensure that my TableRow is typed.
		if (null == row.getTable())
			row.setTable("assessment_result");


		assessMetricId = row.getIntColumn("assessment_metric_id");

		itemId = row.getIntColumn("item_id");

		dimWeightId = row.getIntColumn("dimension_weighting_id");

		layerId = row.getIntColumn("layer_id");

		dimId = row.getIntColumn("dimension_id");

		expWeight = row.getIntColumn("expert_weight");

		metricValue = row.getStringColumn("metric_value");

		admWeight = row.getStringColumn("admin_weight");
	}

	/**
	 * Ckeck for asses params for a given item
	 * 
	 * @param context
	 *            Dspace context
	 * @param itemID
	 *            id of item
	 * @return list of assessment parameters
	 * @throws SQLException
	 */
	public static Vector<AssessParam> findParam(Context context, int itemID)
			throws SQLException {
		String dbquery = "SELECT "
				+ "  k.assessment_metric_id, "
				+ "  k.layer_id, "
				+ "  k.dimension_id, "
				+ "  k.item_id, "
				+ "  k.metric_value, "
				+ "  d.dimension_weighting_id, "
				+ "  d.admin_weight, "
				+ "  d.expert_weight "
				+ "FROM "
				+ "  (SELECT m.layer_id,m.dimension_id,r.assessment_metric_id,r.item_id,r.metric_value "
				+ "   FROM assessment_metric m "
				+ "   INNER JOIN assessment_result r "
				+ "     ON m.assessment_metric_id=r.assessment_metric_id "
				+ "  ) k "
				+ "INNER JOIN dimension_weighting d "
				+ "  ON k.item_id=d.item_id AND k.layer_id=d.layer_id AND k.dimension_id=d.dimension_id "
				+ "WHERE k.item_id = ?";

		TableRowIterator rows = DatabaseManager.query(context, dbquery, itemID); 

		try {
			List<TableRow> dRows = rows.toList();
			Vector<AssessParam> param = new Vector<AssessParam>();

			for (int i = 0; i < dRows.size(); i++) {
				TableRow row = dRows.get(i);
				param.add(new AssessParam(context, row));
			}

			return param;
		} finally {
			if (rows != null)
				rows.close();
		}
	}

	/**
	 * Ckeck for asses params for a given item and layer id
	 * 
	 * @param context
	 *            Dspace context
	 * @param itemID
	 *            id of item
	 * @param layerID
	 *            the layer id
	 * @return list of assessment parameters
	 * @throws SQLException
	 */
	public static Vector<AssessParam> findParam(Context context, int itemID,
			int layerID) throws SQLException

	{
		String dbquery = "SELECT "
				+ "  k.assessment_metric_id, "
				+ "  k.layer_id, "
				+ "  k.dimension_id, "
				+ "  k.item_id, "
				+ "  k.metric_value, "
				+ "  d.dimension_weighting_id, "
				+ "  d.admin_weight, "
				+ "  d.expert_weight "
				+ "FROM "
				+ "  (SELECT m.layer_id,m.dimension_id,r.assessment_metric_id,r.item_id,r.metric_value "
				+ "   FROM assessment_metric m "
				+ "   INNER JOIN assessment_result r "
				+ "     ON m.assessment_metric_id=r.assessment_metric_id "
				+ "  ) k "
				+ "INNER JOIN dimension_weighting d "
				+ "  ON k.item_id=d.item_id AND k.layer_id=d.layer_id AND k.dimension_id=d.dimension_id "
				+ "WHERE k.item_id = ? AND k.layer_id = ? ";

		TableRowIterator rows = DatabaseManager.query(context, dbquery,itemID, layerID);
		try {
			List<TableRow> dRows = rows.toList();
			Vector<AssessParam> params = new Vector<AssessParam>();

			for (int i = 0; i < dRows.size(); i++) {
				TableRow row = dRows.get(i);
				params.add(new AssessParam(context, row));
			}

			return params;
		} finally {
			if (rows != null)
				rows.close();
		}
	}

	public int getAssessMetricID() {
		return assessMetricId;
	}

	public void setAssessMetricID(int assessMetricID) {
		this.assessMetricId = assessMetricID;
	}

	public int getItemID() {
		return itemId;
	}

	public void setItemID(int itemID) {
		this.itemId = itemID;
	}

	public int getDimWeightID() {
		return dimWeightId;
	}

	public void setDimWeightID(int dimWeightID) {
		this.dimWeightId = dimWeightID;
	}

	public int getLayerID() {
		return layerId;
	}

	public void setLayerID(int layerID) {
		this.layerId = layerID;
	}

	public int getDimID() {
		return dimId;
	}

	public void setDimID(int dimID) {
		this.dimId = dimID;
	}

	public int getExpWeight() {
		return expWeight;
	}

	public void setExpWeight(int expWeight) {
		this.expWeight = expWeight;
	}

	public String getMetricValue() {
		return metricValue;
	}

	public void setMetricValue(String assessValue) {
		this.metricValue = assessValue;
	}

	public String getAdmWeight() {
		return admWeight;
	}

	public void setAdmWeight(String admWeight) {
		this.admWeight = admWeight;
	}

}
