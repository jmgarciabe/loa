package org.dspace.loa;

public class AssessmentResult {
	
	private int id;
	private int assessmentMetricId;
	private int itemId;
	private double value;
	
	public AssessmentResult(){
		
	}
	
	public AssessmentResult(int assessmentMetricId, int itemId){
		this.assessmentMetricId = assessmentMetricId;
		this.itemId = itemId;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAssessmentMetricId() {
		return assessmentMetricId;
	}
	public void setAssessmentMetricId(int assessmentMetricId) {
		this.assessmentMetricId = assessmentMetricId;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	

}
