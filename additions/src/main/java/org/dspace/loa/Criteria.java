package org.dspace.loa;

/**
 * Mapping of criteria DB table
 * 
 * @author JavierG
 * 
 */
public class Criteria {

	/** criteria ID */
	private int id;
	
	/** criteria name */
	private String name;
	
	public Criteria(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
		
}
