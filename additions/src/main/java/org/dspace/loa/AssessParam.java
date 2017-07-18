package org.dspace.loa;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

//import org.dspace.app.webui.util.JSPManager;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

public class AssessParam extends DSpaceObject {

	private int assessMetricID, itemID, dimWeightID, layerID, dimID, expWeight;

	private String metricValue, admWeight;

	/** The row in the table representing this object */
	private final TableRow myRow;

	AssessParam(Context context, TableRow row) {
		// TODO Auto-generated constructor stub
		super(context);

		// Ensure that my TableRow is typed.
		if (null == row.getTable())
			row.setTable("assessment_result");

		myRow = row;

		assessMetricID = row.getIntColumn("assessment_metric_id");

		itemID = row.getIntColumn("item_id");

		dimWeightID = row.getIntColumn("dimension_weighting_id");

		layerID = row.getIntColumn("layer_id");

		dimID = row.getIntColumn("dimension_id");

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
		return assessMetricID;
	}

	public void setAssessMetricID(int assessMetricID) {
		this.assessMetricID = assessMetricID;
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public int getDimWeightID() {
		return dimWeightID;
	}

	public void setDimWeightID(int dimWeightID) {
		this.dimWeightID = dimWeightID;
	}

	public int getLayerID() {
		return layerID;
	}

	public void setLayerID(int layerID) {
		this.layerID = layerID;
	}

	public int getDimID() {
		return dimID;
	}

	public void setDimID(int dimID) {
		this.dimID = dimID;
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

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getHandle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
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
