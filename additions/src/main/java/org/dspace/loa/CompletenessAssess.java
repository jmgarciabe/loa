package org.dspace.loa;

import java.io.IOException;

import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Constants;

/**
 * Completeness assessment compares item metadata with some
 * specific fields obtained via input-forms.xml. This assessment 
 * returns a real number between 0 and 1 to indicate which fields
 * are present in the item metadata, 1 indicates that all checked
 * fields are present in the item metadata, meanwhile 0 indicates 
 * that none of them were filled out.   
 * @author Andres Salazar
 */

public class CompletenessAssess {
	
	/** Weight for Title metadata field value */
    private static final double TITLE = 0.17;
    
    /** Weight for Subject metadata field value */
    private static final double SUBJECT = 0.16;
    
    /** Weight for Abstract metadata field value */
    private static final double ABSTRACT = 0.14;
    
    /** Weight for Author metadata field value */
    private static final double AUTHOR = 0.13;
    
    /** Weight for Date metadata field value */
    private static final double DATE = 0.12;
    
    /** Weight for Type metadata field value */
    private static final double TYPE = 0.11;
    
    /** Weight for Language metadata field value */
    private static final double LANG = 0.07;
    
    /** Weight for Description metadata field value */
    private static final double DESCR = 0.05;
    
    /** Weight for Location metadata field value */
    private static final double LOCATION = 0.03;
    
    /** Weight for Provenance (Status) metadata field value */
    private static final double STATUS = 0.02;
	

	/**
     * Perform the completeness assess upon passed DSO
     *
     * @param dso the DSpace object
     * @throws IOException
     */
    public static double perform(DSpaceObject dso) 
    {
    	double completeness = 0.0;
    	
    	if (dso.getType() == Constants.ITEM)
    	{
    		Item item = (Item) dso;
    		if (item.getMetadata("dc.title") != null)
    			completeness = TITLE + completeness;
    		if (item.getMetadata("dc.subject") != null)
    			completeness = SUBJECT + completeness;
    		if (item.getMetadata("dc.description.abstract") != null)
    			completeness = ABSTRACT + completeness;
    		if (item.getMetadata("dc.contributor.author") != null)
    			completeness = AUTHOR + completeness;
    		if (item.getMetadata("dc.date.issued") != null)
    			completeness = DATE + completeness;
    		if (item.getMetadata("dc.type") != null)
    			completeness = TYPE + completeness;
    		if (item.getMetadata("dc.language.iso") != null)
    			completeness = LANG + completeness;
    		if (item.getMetadata("dc.description") != null)
    			completeness = DESCR + completeness;
    		if (item.getMetadata("dc.identifier.uri") != null)
    			completeness = LOCATION + completeness;
    		if (item.getMetadata("dc.description.provenance") != null)
    			completeness = STATUS + completeness;
    	}
    	
        return completeness;
    }
    
    
    public static String getResults(DSpaceObject dso)
    {
    	// The results that we'll return
        StringBuilder results = new StringBuilder();
    	
    	if (dso.getType() == Constants.ITEM)
    	{
    		Item item = (Item) dso;
            
            // Appends item's handle to results message
            results.append("Item: ").append(item.getHandle());
            
            if (perform(item) > 0.7)
            	results.append(" most of the most important metadata fields has been filled");
            if ((perform(item) >= 0.3) && (perform(item) <= 0.7))
            	results.append(" half of the most important metadata fields has been filled");
            if (perform(item) < 0.3)
            	results.append(" very few of the most important metadata fields have been filled");
    	}
    	
    	return results.toString();
    }
       
}