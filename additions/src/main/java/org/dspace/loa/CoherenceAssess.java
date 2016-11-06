package org.dspace.loa;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Constants;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

/**
 * Coherence assessment compares some item metadata values with the values 
 * of other specific metadata fields. This assessment returns a real number 
 * between 0 and 1 to indicate metadata fields which value correspond with 
 * the value declared in others like: dc.language.iso, dc.type and 
 * dc.format.mimetype, 1 indicates that all checked fields accomplishes with
 * values present in the reference metadata fields, meanwhile 0 indicates 
 * that none of them are coherent with that.  
 * @author Andres Salazar
 */

public class CoherenceAssess {
	
	/**
     * Perform the coherence assess upon passed DSO
     *
     * @param dso the DSpace object
     * @throws IOException
     */
    public static double perform(DSpaceObject dso) throws Exception 
    {
    	double coherence = 0.0;
    	double sum = 0.0;
    	
    	if (dso.getType() == Constants.ITEM)
    	{
    		Item item = (Item) dso;
    		int comparisons = 0;
    		if (item.getMetadata("dc.language.iso") != null)
    		{
    			if (item.getMetadata("dc.title") != null)
    			{
    				comparisons ++;
    				init(System.getProperty("user.home") + "/profiles.sm");
    				
    				if (item.getMetadata("dc.language.iso").startsWith(detect(item.getMetadata("dc.title"))))
    				{
    					sum += 1;
    					coherence = sum / comparisons;
    				}
    				else
    					coherence = sum / comparisons;
    				
    				DetectorFactory.clear();
    			}
    			if (item.getMetadata("dc.subject") != null)
    			{
    				comparisons ++;
    				init(System.getProperty("user.home") + "/profiles.sm");
    				
    				if (item.getMetadata("dc.language.iso").startsWith(detect(item.getMetadata("dc.subject"))))
    				{
    					sum += 1;
    					coherence = sum / comparisons;
    				}
    				else
    					coherence = sum / comparisons;
    				
    				DetectorFactory.clear();
    			}
    			if (item.getMetadata("dc.description.abstract") != null)
    			{
    				comparisons ++;
    				init(System.getProperty("user.home") + "/profiles.sm");
    				
    				if (item.getMetadata("dc.language.iso").startsWith(detect(item.getMetadata("dc.description.abstract"))))
    				{
    					sum += 1;
    					coherence = sum / comparisons;
    				}
    				else
    					coherence = sum / comparisons;
    				
    				DetectorFactory.clear();
    			}
    			if (item.getMetadata("dc.description") != null)
    			{
    				comparisons ++;
    				init(System.getProperty("user.home") + "/profiles.sm");
    				
    				if (item.getMetadata("dc.language.iso").startsWith(detect(item.getMetadata("dc.description"))))
    				{
    					sum += 1;
    					coherence = sum / comparisons;
    				}
    				else
    					coherence = sum / comparisons;
    				
    				DetectorFactory.clear();
    			}
    		}
    		
    		if (item.getMetadata("dc.type") != null && item.getMetadata("dc.format.mimetype") != null)
    		{
    			String path = System.getProperty("user.home") + "/Allowed values/media-types.csv";
    			String formaType = searchFormaType(path, item.getMetadata("dc.format.mimetype"));
    			if(item.getMetadata("dc.type").equals("Animation") && formaType.equals("app"))
    			{
    				comparisons ++;
    				sum += 0.5;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Animation") && formaType.equals("video"))
    			{
    				comparisons ++;
    				sum += 0.5;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Article") && formaType.equals("app"))
    			{
    				comparisons ++;
    				sum += 0.5;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Article") && formaType.equals("text"))
    			{
    				comparisons ++;
    				sum += 0.5;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Book") && formaType.equals("app"))
    			{
    				comparisons ++;
    				sum += 1;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Book chapter") && formaType.equals("app"))
    			{
    				comparisons ++;
    				sum += 1;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Book chapter") && formaType.equals("text"))
    			{
    				comparisons ++;
    				sum += 0.5;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Dataset") && formaType.equals("app"))
    			{
    				comparisons ++;
    				sum += 0.5;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Dataset") && formaType.equals("text"))
    			{
    				comparisons ++;
    				sum += 0.5;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Learning Object") && formaType.equals("app"))
    			{
    				comparisons ++;
    				sum += 1;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Image") && formaType.equals("image"))
    			{
    				comparisons ++;
    				sum += 1;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Image, 3-D") && formaType.equals("image"))
    			{
    				comparisons ++;
    				sum += 1;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Map") && formaType.equals("app"))
    			{
    				comparisons ++;
    				sum += 0.5;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Map") && formaType.equals("image"))
    			{
    				comparisons ++;
    				sum += 0.5;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Musical Score") && formaType.equals("audio"))
    			{
    				comparisons ++;
    				sum += 1;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Plan or blueprint") && formaType.equals("app"))
    			{
    				comparisons ++;
    				sum += 1;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Preprint") && formaType.equals("app"))
    			{
    				comparisons ++;
    				sum += 1;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Presentation") && formaType.equals("app"))
    			{
    				comparisons ++;
    				sum += 0.33;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Presentation") && formaType.equals("text"))
    			{
    				comparisons ++;
    				sum += 0.33;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Presentation") && formaType.equals("video"))
    			{
    				comparisons ++;
    				sum += 0.33;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Recording, acoustical") && formaType.equals("audio"))
    			{
    				comparisons ++;
    				sum += 1;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Recording, musical") && formaType.equals("audio"))
    			{
    				comparisons ++;
    				sum += 1;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Recording, oral") && formaType.equals("audio"))
    			{
    				comparisons ++;
    				sum += 1;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Software") && formaType.equals("app"))
    			{
    				comparisons ++;
    				sum += 1;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Technical Report") && formaType.equals("app"))
    			{
    				comparisons ++;
    				sum += 0.5;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Technical Report") && formaType.equals("text"))
    			{
    				comparisons ++;
    				sum += 0.5;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Thesis") && formaType.equals("app"))
    			{
    				comparisons ++;
    				sum += 1;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Video") && formaType.equals("video"))
    			{
    				comparisons ++;
    				sum += 1;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Working Paper") && formaType.equals("app"))
    			{
    				comparisons ++;
    				sum += 0.5;
    				coherence = sum / comparisons;
    			}
    			if(item.getMetadata("dc.type").equals("Working Paper") && formaType.equals("text"))
    			{
    				comparisons ++;
    				sum += 0.5;
    				coherence = sum / comparisons;
    			}		
    		}
    		
    	if (comparisons == 0)
    		System.out.println("El calculo de esta metrica no esta disponible");
    		
    	}
    	    	
		return coherence;
    	
    }
    
    private static void init(String profileDirectory) throws LangDetectException {
        DetectorFactory.loadProfile(profileDirectory);
	}
    
    private static String detect(String text) throws LangDetectException {
		Detector detector = DetectorFactory.create();
		detector.append(text);
		return detector.detect();
	}
    
    private static String searchFormaType(String filePath, String metadata) throws Exception {
    	
    	String type = null;
    	File csvFile = new File(filePath);
		List<String> lines = Files.readAllLines(csvFile.toPath(), StandardCharsets.UTF_8);
		
		for (String line : lines)
		{
			String[] array = line.split(",");
			if ((metadata.contains(array[0]) || metadata.contains(array[1])) && array[1].contains("application/"))
				type = "app";
			if ((metadata.contains(array[0]) || metadata.contains(array[1])) && array[1].contains("audio/"))
				type = "audio";
			if ((metadata.contains(array[0]) || metadata.contains(array[1])) && array[1].contains("image/"))
				type = "image";
			if ((metadata.contains(array[0]) || metadata.contains(array[1])) && array[1].contains("text/"))
				type = "text";
			if ((metadata.contains(array[0]) || metadata.contains(array[1])) && array[1].contains("video/"))
				type = "video";
		}
    	
		return type;
    	
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
            
            try {
				if (perform(item) >= 0.8)
					results.append(" has highly coherent data in these metadata fields: dc.language.iso, dc.type and dc.format.mimetype");
				if ((perform(item) > 0.3) && (perform(item) < 0.8))
					results.append(" has medium coherence data in these metadata fields: dc.language.iso, dc.type and dc.format.mimetype");
				if (perform(item) <= 0.3)
					results.append(" has low coherence data in these metadata fields: dc.language.iso, dc.type and dc.format.mimetype");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	return results.toString();
    }
    
}