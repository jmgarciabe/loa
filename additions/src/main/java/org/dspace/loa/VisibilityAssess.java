package org.dspace.loa;

import java.io.IOException;

import org.dspace.content.Collection;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.statistics.Dataset;
import org.dspace.statistics.content.DatasetDSpaceObjectGenerator;
import org.dspace.statistics.content.StatisticsDataVisits;
import org.dspace.statistics.content.StatisticsListing;

/**
 * Visibility assessment looks for number of item views and number
 * of file downloads in solr index to calculate relation between 
 * LO visits and downloads and the total number of visits and 
 * downloads in the repository. This assessment returns a real 
 * number between 0 and 1 to indicate how visible is that LO
 * in the repository, 1 indicates that the LO is very "popular"
 * in the item repository, meanwhile 0 indicates that any usuer
 * has used it.   
 * @author Kim Shepherd (Adapted by Andres Salazar)
 */

public class VisibilityAssess {
	
	/**
     * Perform the visibility assess upon passed DSO
     *
     * @param dso the DSpace object
     * @throws IOException
     */
    public static double perform(Context context, DSpaceObject dso) 
    {
    	double visibility = 0;
    	double itemVisits = 0;
    	double itemDown = 0;
    	double collVisits = 0;
    	double nonCollVisits = 0;
    	double nonCollSum = 0;
    	
    	// Gets the total number of visits by item from Solr index
    	try
        {
            StatisticsListing statListing = new StatisticsListing(
            		new StatisticsDataVisits(dso));

            statListing.setTitle("Total Visits");
            statListing.setId("list1");

            DatasetDSpaceObjectGenerator dsoAxis = new DatasetDSpaceObjectGenerator();
            dsoAxis.addDsoChild(dso.getType(), 10, false, -1);
            statListing.addDatasetGenerator(dsoAxis);
            Dataset dataset = statListing.getDataset(context);

            if (dataset != null)
            {
            	String[][] matrix = dataset.getMatrix();
                
                for (int i=0; i<matrix.length; i++){
            		for (int j=0; j<matrix[i].length; j++){
            			itemVisits = new Integer(matrix[i][j]).doubleValue();
            		}
            	}
            }
        } catch (Exception e)
        {
		/*log.error(
                    "Error occurred while creating statistics for dso with ID: "
                            + dso.getID() + " and type " + dso.getType()
                            + " and handle: " + dso.getHandle(), e);*/
        }
    	
    	// Gets the total number of file downloads by item from Solr index
    	
    	try
        {

            StatisticsListing statisticsTable = new StatisticsListing(
            		new StatisticsDataVisits(dso));

            statisticsTable.setTitle("File Downloads");
            statisticsTable.setId("list2");

            DatasetDSpaceObjectGenerator dsoAxis = new DatasetDSpaceObjectGenerator();
            dsoAxis.addDsoChild(Constants.BITSTREAM, 10, false, -1);
            statisticsTable.addDatasetGenerator(dsoAxis);
            Dataset dataset = statisticsTable.getDataset(context);

            if (dataset != null)
            {
            	String[][] matrix = dataset.getMatrix();
                
                for (int i=0; i<matrix.length; i++){
            		for (int j=0; j<matrix[i].length; j++){
            			itemDown = new Integer(matrix[i][j]).doubleValue();
            		}
            	}
            }
        }
        catch (Exception e)
        {
            /*log.error(
                "Error occurred while creating statistics for dso with ID: "
                                + dso.getID() + " and type " + dso.getType()
                                + " and handle: " + dso.getHandle(), e);*/
        }
    	
    	// Gets the total number of visits by collection where belongs the item assessed from Solr index
    	
    	try
        {
            StatisticsListing statListing = new StatisticsListing(
            		new StatisticsDataVisits(dso.getParentObject()));

            statListing.setTitle("Total Collection Visits");
            statListing.setId("list3");

            DatasetDSpaceObjectGenerator dsoAxis = new DatasetDSpaceObjectGenerator();
            dsoAxis.addDsoChild(dso.getParentObject().getType(), 10, false, -1);
            statListing.addDatasetGenerator(dsoAxis);
            Dataset dataset = statListing.getDataset(context);

            if (dataset != null)
            {
            	String[][] matrix = dataset.getMatrix();
                
                for (int i=0; i<matrix.length; i++){
            		for (int j=0; j<matrix[i].length; j++){
            			collVisits = new Integer(matrix[i][j]).doubleValue();
            		}
            	}
            }
        } catch (Exception e)
        {
		/*log.error(
                    "Error occurred while creating statistics for dso with ID: "
                            + dso.getID() + " and type " + dso.getType()
                            + " and handle: " + dso.getHandle(), e);*/
        }
    	
    	// Gets the total number of visits by collections where doesn't belong the item assessed from Solr index
    	
    	try
        {
    		Item item = (Item) dso;
    		
    		Collection [] nonColl = item.getCollectionsNotLinked();
    		
    		for (int k=0; k<nonColl.length; k++)
    		{
    			StatisticsListing statListing = new StatisticsListing(
                        new StatisticsDataVisits(nonColl[k]));
    			
    			statListing.setTitle("Total NonInCollection Visits");
                statListing.setId("list4");

                DatasetDSpaceObjectGenerator dsoAxis = new DatasetDSpaceObjectGenerator();
                dsoAxis.addDsoChild(nonColl[k].getType(), 10, false, -1);
                statListing.addDatasetGenerator(dsoAxis);
                Dataset dataset = statListing.getDataset(context);

                if (dataset != null)
                {
                	String[][] matrix = dataset.getMatrix();
                    
                    for (int i=0; i<matrix.length; i++){
                		for (int j=0; j<matrix[i].length; j++){
                			nonCollVisits = new Integer(matrix[i][j]).doubleValue();
                			nonCollSum += nonCollVisits;
                		}
                	}
                }
    		}
        } catch (Exception e)
        {
		/*log.error(
                    "Error occurred while creating statistics for dso with ID: "
                            + dso.getID() + " and type " + dso.getType()
                            + " and handle: " + dso.getHandle(), e);*/
        }
    	
    	if (collVisits > 0)
    	{
    		if(nonCollSum > 0)
    			visibility = (itemVisits + itemDown) / (collVisits + nonCollSum);
        	else
        		visibility = itemVisits / collVisits;		
    	}
    	
    	return visibility;	
    }
    
    public static String getResults(Context context,DSpaceObject dso)
    {
    	// The results that we'll return
        StringBuilder results = new StringBuilder();
    	
    	if (dso.getType() == Constants.ITEM)
    	{
    		Item item = (Item) dso;
            
            // Appends item's handle to results message
            results.append("Item: ").append(item.getHandle());
            
			if (perform(context,item) > 0.7)
				results.append(" is highly popular in the repository");
			if ((perform(context,item) >= 0.3) && (perform(context,item) <= 0.7))
				results.append(" is frequently visited in the repository");
			if (perform(context,item) < 0.3) 
				results.append(" is not so popular in the repository");
    	}
    	
    	return results.toString();
    }
    
}