package org.dspace.loa;

/**
 * Mapping of assessment_result DB table
 * 
 * @author JavierG
 * 
 */
public class AssessmentResult {
	
	/** assessment result id */
	private int id;
	
	/** assessment metric id */
	private AssessmentMetric assessmentMetric;
	
	/** item id */
	private int itemId;
	
	/** result value */
	private Double value;
	
	public AssessmentResult(){
		
	}
	
	public AssessmentResult(int assessmentMetricId, int itemId){
		assessmentMetric = new AssessmentMetric();
		assessmentMetric.setId(assessmentMetricId);
		this.itemId = itemId;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public AssessmentMetric getAssessmentMetric() {
		return assessmentMetric;
	}

	public void setAssessmentMetric(AssessmentMetric assessmentMetric) {
		this.assessmentMetric = assessmentMetric;
	}

	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	
	

}
