package org.dspace.loa;

public class AssessmentHistory {
	
	private int id;
	private int resultId;
	private int personId;
	private double value;
	
	public AssessmentHistory(){
		
	}
	
	public AssessmentHistory(int resultId, int personId){
		this.resultId = resultId;
		this.personId = personId;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getResultId() {
		return resultId;
	}
	public void setResultId(int resultId) {
		this.resultId = resultId;
	}
	public int getPersonId() {
		return personId;
	}
	public void setPersonId(int personId) {
		this.personId = personId;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	

}
