package org.dspace.loa;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Constants;

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

public class ConsistencyAssess {

	/**
	 * Perform the consistency assess upon passed DSO
	 * 
	 * @param dso
	 *            the DSpace object
	 * @throws IOException
	 */
	public static double perform(DSpaceObject dso) throws Exception {
		double consistency = 0.0;
		double sum = 0.0;

		if (dso.getType() == Constants.ITEM) {
			Item item = (Item) dso;
			int comparisons = 0;
			if (item.getMetadata("dc.format.mimetype") != null) {
				comparisons++;
				String path = System.getProperty("user.home") + "/Allowed values/media-types.csv";

				if (compareCSValues(path, item.getMetadata("dc.format.mimetype"))) {
					sum += 1;
					consistency = sum / comparisons;
				} else {
					consistency = sum / comparisons;
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
					consistency = sum / comparisons;
				} else {
					consistency = sum / comparisons;
				}
			}
			if (item.getMetadata("dc.subject.ddc") != null) {
				comparisons++;
				String path = System.getProperty("user.home") + "/Allowed values/ddc.csv";

				if (compareCSValues(path, item.getMetadata("dc.subject.ddc"))) {
					sum += 1;
					consistency = sum / comparisons;
				} else {
					consistency = sum / comparisons;
				}
			}
			if (item.getMetadata("dc.subject.lcc") != null) {
				comparisons++;
				String path = System.getProperty("user.home") + "/Allowed values/lcc.csv";

				if (compareCSValues(path, item.getMetadata("dc.subject.lcc"))) {
					sum += 1;
					consistency = sum / comparisons;
				} else {
					consistency = sum / comparisons;
				}
			}
			if (item.getMetadata("dc.subject.mesh") != null) {
				comparisons++;
				String path = System.getProperty("user.home") + "/Allowed values/mesh.csv";

				if (compareCSValues(path, item.getMetadata("dc.subject.mesh"))) {
					sum += 1;
					consistency = sum / comparisons;
				} else {
					consistency = sum / comparisons;
				}
			}
			if (item.getMetadata("dc.type") != null) {
				comparisons++;
				boolean match = false;
				String[] contenType = { "Animation", "Article", "Book", "Book chapter", "Dataset", "Learning Object", "Image",
						"Image, 3-D", "Map", "Musical Score", "Plan or blueprint", "Preprint", "Presentation",
						"Recording, acoustical", "Recording, musical", "Recording, oral", "Software", "Technical Report",
						"Thesis", "Video", "Working Paper", "Other" };

				for (int i = 0; i < contenType.length; i++) {
					if (item.getMetadata("dc.type").equals(contenType[i]))
						match = true;
					break;
				}

				if (match) {
					sum += 1;
					consistency = sum / comparisons;
				} else {
					consistency = sum / comparisons;
				}
			} else if (comparisons == 0) {
				System.out.println("El calculo de esta metrica no esta disponible");
			}
		}

		return consistency;

	}

	private static boolean compareCSValues(String filePath, String metadata) throws Exception {

		boolean result = false;
		File csvFile = new File(filePath);
		List<String> lines = Files.readAllLines(csvFile.toPath(), StandardCharsets.UTF_8);

		for (String line : lines) {
			String[] array = line.split(",");
			if (metadata.contains(array[0]) || metadata.contains(array[1]))
				result = true;
		}

		return result;

	}

	public static String getResults(DSpaceObject dso) {
		// The results that we'll return
		StringBuilder results = new StringBuilder();

		if (dso.getType() == Constants.ITEM) {
			Item item = (Item) dso;

			// Appends item's handle to results message
			results.append("Item: ").append(item.getHandle());

			try {
				if (perform(item) > 0.8)
					results.append(" has consistent data in most of metadata fields");
				if ((perform(item) > 0.3) && (perform(item) < 0.8))
					results.append(" has consistent data in half of the most important metadata fields");
				if (perform(item) <= 0.3)
					results.append(" has inconsistent data in most of metadata fields");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return results.toString();
	}

}