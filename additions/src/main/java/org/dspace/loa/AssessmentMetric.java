package org.dspace.loa;

/**
 * Mapping of assessment_metric DB table
 * 
 * @author JavierG
 * 
 */
public class AssessmentMetric {

	/** assessment metric id */
	private int id;
	
	/** layer of the assessment metric */
	private Layer layer;
	
	/** dimension of the assessment metric */
	private Dimension dimension;
	
	/** criteria of the assessment metric */
	private Criteria criteria;
	
	/** whether the assessment metric has an associated assessment result or not */
	private boolean checked;

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Layer getLayer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

}
