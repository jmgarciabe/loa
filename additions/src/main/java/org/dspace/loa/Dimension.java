package org.dspace.loa;


/**
 * Mapping of dimension DB table
 * 
 * @author JavierG
 * 
 */
public class Dimension {

	/** dimension ID */
	private int id;

	/** dimension name */
	private String name;

	public Dimension() {
	};

	public Dimension(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int iD) {
		id = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
