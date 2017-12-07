package org.dspace.loa;

/**
 * Mapping of dim_expert_weighting DB table
 * 
 * @author JavierG
 * 
 */
public class ExpertWeighting {
	
	/** expert weighting ID */
	int id;
	
	/** related dimension weighting ID */
	int dimWeightingId;
	
	/** eperson ID of the expert */
	int expertId;
	
	/** expert weight value */
	int expertWeight;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDimWeightingId() {
		return dimWeightingId;
	}
	public void setDimWeightingId(int dimWeightingId) {
		this.dimWeightingId = dimWeightingId;
	}
	public int getExpertId() {
		return expertId;
	}
	public void setExpertId(int expertId) {
		this.expertId = expertId;
	}
	public int getExpertWeight() {
		return expertWeight;
	}
	public void setExpertWeight(int expertWeight) {
		this.expertWeight = expertWeight;
	}
	
	

}
