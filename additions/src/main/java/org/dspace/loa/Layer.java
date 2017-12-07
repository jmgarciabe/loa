package org.dspace.loa;

/**
 * Mapping of layer DB table
 * 
 * @author JavierG
 * 
 */
public class Layer {

	/** Identifier of the metric */
	private int id;

	/** Name of the metric */
	private String name;

	public Layer() {
	};

	public Layer(int id, String name) {
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
