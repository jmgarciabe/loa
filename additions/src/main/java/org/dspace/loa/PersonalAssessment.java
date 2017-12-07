package org.dspace.loa;

/**
 * Mapping of personal_assessment DB table
 * @author JavierG
 *
 */
public class PersonalAssessment {
	
	/** Personal assessment **/
	private int id;
	
	/** Assessment result id **/
	private int resultId;
	
	/** Eperson ID **/
	private int personId;
	
	/** Value of assessment history **/
	private Double value;
	
	public PersonalAssessment(){
		
	}
	
	public PersonalAssessment(int resultId, int personId){
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
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	
	

}
