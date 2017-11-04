package org.dspace.loa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.core.Context;

/**
 * Consistency assessment compares some item metadata values with a list of
 * intended values defined by Dublin Core Metadata Registry. This assessment
 * returns a real number between 0 and 1 to indicate which fields meet with
 * allowed values, 1 indicates that all checked metadata fields are filled out
 * and its values are consistent. Meanwhile 0 indicates that item metadata are
 * inconsistent against Dublin Core Metadata Registry.
 * 
 * @author Andres Salazar
 */

public class ConsistencyAssessCommand implements AdminAssessmentCommandIntarface {

	/** Log object to send errorr messages to log file */
	private static final Logger log = Logger.getLogger(ConsistencyAssessCommand.class);

	/**
	 * Perform the consistency assess upon passed DSO
	 * 
	 * @param dso
	 *            the DSpace object
	 * @throws IOException
	 */
	public AssessResult executeAssessment(DSpaceObject dso, Context context) throws AdminAssessmentException {

		if (dso.getType() != Constants.ITEM) {
			return null;
		}
		
		double score = 0.0;
		double sum = 0.0;
		int comparisons = 0;
		boolean assessmentExecuted = true;
		StringBuilder result = new StringBuilder();
		Item item = (Item) dso;
		String handle = item.getHandle();

		if (item.getMetadata("dc.format.mimetype") != null) {
			comparisons++;

			String path = "allowedValues/media-types.csv";
			if (compareCSValues(path, item.getMetadata("dc.format.mimetype"))) {
				sum += 1;
				score = sum / comparisons;
			} else {
				score = sum / comparisons;
			}
		}
		if (item.getMetadata("dc.language.iso") != null) {

			comparisons++;
			boolean match = false;
			String[] dcISOLang = { "en_US", "en", "es", "de", "fr", "it", "ja", "zh", "tr", "other" };

			for (int i = 0; i < dcISOLang.length; i++) {
				if (item.getMetadata("dc.language.iso").equals(dcISOLang[i])) {
					match = true;
					break;
				}
			}

			if (match) {
				sum += 1;
			}
			score = sum / comparisons;

		}
		if (item.getMetadata("dc.subject.ddc") != null) {
			comparisons++;
			String path = "allowedValues/ddc.csv";
			if (compareCSValues(path, item.getMetadata("dc.subject.ddc"))) {
				sum += 1;
			}
			score = sum / comparisons;
		}
		if (item.getMetadata("dc.subject.lcc") != null) {
			comparisons++;
			String path = "allowedValues/lcc.csv";
			if (compareCSValues(path, item.getMetadata("dc.subject.lcc"))) {
				sum += 1;
			}
			score = sum / comparisons;
		}
		if (item.getMetadata("dc.subject.mesh") != null) {
			comparisons++;
			String path = "allowedValues/mesh.csv";
			if (compareCSValues(path, item.getMetadata("dc.subject.mesh"))) {
				sum += 1;
			}
			score = sum / comparisons;
		}
		if (item.getMetadata("dc.type") != null) {
			comparisons++;
			boolean match = false;
			String[] contenType = { "Animation", "Article", "Book", "Book chapter", "Dataset", "Learning Object", "Image",
					"Image, 3-D", "Map", "Musical Score", "Plan or blueprint", "Preprint", "Presentation",
					"Recording, acoustical", "Recording, musical", "Recording, oral", "Software", "Technical Report", "Thesis",
					"Video", "Working Paper", "Other" };

			for (int i = 0; i < contenType.length; i++) {
				if (item.getMetadata("dc.type").equals(contenType[i]))
					match = true;
				break;
			}

			if (match) {
				sum += 1;
			}
			score = sum / comparisons;
		}
		
		//Build assessment result
		String status = score > 0.0 ? "Success" : "Fail";
		String stringScore = new DecimalFormat("#.##").format(score);
		// Appends item's handle to results message
		result.append("Item: ").append(handle);
		if (score > 0.7) {
			result.append(" has consistent data in most of analyzed metadata fields");
		}
		if ((score >= 0.3) && (score <= 0.7)) {
			result.append(" has consistent data in half of analyzed metadata fields");
		}
		if (score < 0.3) {
			result.append(" has inconsistent data in most of analyzed metadata fields");
		}
		AssessResult assessResult = new AssessResult("Consistency", score, handle, status, stringScore + ". " + result,
				assessmentExecuted);
		return assessResult;
	}
	

	/**
	 * Loads the file with the supplied path and checks if the given metadata
	 * field is in the file
	 * 
	 * @param filePath
	 *            - the file path to the file with types
	 * @param metadata
	 *            - the value of the type store in metadata
	 * @return - whether the metadata type is a valid type or not
	 * @throws AdminAssessmentException
	 *             - may throw an exception loading the CSV file
	 */
	private boolean compareCSValues(String path, String metadata) throws AdminAssessmentException {

		boolean result = false;
		try {
			InputStream in = CoherenceAssessCommand.class.getResourceAsStream(path);
			try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
				String line;
				while ((line = br.readLine()) != null) {
					String[] array = line.split(",");
					if ((array[0].length() > 0 && metadata.contains(array[0]))
							|| (array[1].length() > 0 && metadata.contains(array[1]))) {
						result = true;
						break;
					}
				}
			}
		} catch (IOException ioe) {
			log.error(ioe.getMessage());
			throw new AdminAssessmentException("Exception loading Lang Detect Profiles");
		}

		return result;
	}

}