package org.dspace.loa;

/**
 * Mapping of dimension_weighting DB table
 * 
 * @author JavierG
 * 
 */
public class DimensionWeighting {
	
	/** dimension weighting ID */
	private int id;
	
	/** layer of the dimension weighting */
	private Layer layer;
	
	/** dimension of the dimension weighting */
	private Dimension dimension;
	
	/** item ID */
	private int itemId;
	
	/** weight value */
	private double adminWeight;
	
	
	public DimensionWeighting(){
		layer = new Layer();
		dimension = new Dimension();
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

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public double getAdminWeight() {
		return adminWeight;
	}

	public void setAdminWeight(double adminWeight) {
		this.adminWeight = adminWeight;
	}
	
	
}
