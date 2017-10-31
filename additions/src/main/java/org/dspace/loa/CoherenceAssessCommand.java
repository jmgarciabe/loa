package org.dspace.loa;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.core.Context;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

/**
 * Coherence assessment compares some item metadata values with the values of
 * other specific metadata fields. This assessment returns a real number between
 * 0 and 1 to indicate metadata fields which value correspond with the value
 * declared in others like: dc.language.iso, dc.type and dc.format.mimetype, 1
 * indicates that all checked fields accomplishes with values present in the
 * reference metadata fields, meanwhile 0 indicates that none of them are
 * coherent with that.
 * 
 * @author Andres Salazar
 */

public class CoherenceAssessCommand implements AdminAssessmentCommandIntarface {

	/** Store the assessment result score */
	private double score = 0.0;

	/** Store assessment result as text adding needed extra information */
	private StringBuilder result = new StringBuilder();

	/** whether the assessment process has been carried out or not */
	private boolean assessmentExecuted = false;

	/** The item's handle */
	private String handle = "";

	/** Log object to send errorr messages to log file */
	private static final Logger log = Logger.getLogger(CoherenceAssessCommand.class);

	public void executeAssessment(DSpaceObject dso, Context context) throws AdminAssessmentException {

		score = 0.0;
		double sum = 0.0;

		if (dso.getType() != Constants.ITEM) {
			return;
		}

		assessmentExecuted = true;
		Item item = (Item) dso;
		handle = item.getHandle();
		int comparisons = 0;
		// load Lang Detect profiles
		init();

		if (item.getMetadata("dc.language.iso") != null) {
			if (item.getMetadata("dc.title") != null) {
				comparisons++;

				if (item.getMetadata("dc.language.iso").startsWith(detect(item.getMetadata("dc.title")))) {
					sum += 1;
					score = sum / comparisons;
				} else
					score = sum / comparisons;

			}
			if (item.getMetadata("dc.subject") != null) {
				comparisons++;

				if (item.getMetadata("dc.language.iso").startsWith(detect(item.getMetadata("dc.subject")))) {
					sum += 1;
					score = sum / comparisons;
				} else
					score = sum / comparisons;

			}
			if (item.getMetadata("dc.description.abstract") != null) {
				comparisons++;

				if (item.getMetadata("dc.language.iso").startsWith(detect(item.getMetadata("dc.description.abstract")))) {
					sum += 1;
					score = sum / comparisons;
				} else
					score = sum / comparisons;

			}
			if (item.getMetadata("dc.description") != null) {
				comparisons++;

				if (item.getMetadata("dc.language.iso").startsWith(detect(item.getMetadata("dc.description")))) {
					sum += 1;
					score = sum / comparisons;
				} else
					score = sum / comparisons;

			}
		}
		// Release Lang Detect loaded resources
		DetectorFactory.clear();

		if (item.getMetadata("dc.type") != null && item.getMetadata("dc.format.mimetype") != null) {

			// Dspace types and formats accepted pairs
			Map<String, String[]> alloweMimeValues = new HashMap<String, String[]>();
			alloweMimeValues.put("Animation", new String[] { "app", "video" });
			alloweMimeValues.put("Article", new String[] { "app", "text" });
			alloweMimeValues.put("Book", new String[] { "app" });
			alloweMimeValues.put("Book chapter", new String[] { "app", "text" });
			alloweMimeValues.put("Dataset", new String[] { "app", "text" });
			alloweMimeValues.put("Learning Object", new String[] { "app" });
			alloweMimeValues.put("Image", new String[] { "image" });
			alloweMimeValues.put("Image, 3-D", new String[] { "image" });
			alloweMimeValues.put("Map", new String[] { "app", "image" });
			alloweMimeValues.put("Musical Score", new String[] { "audio" });
			alloweMimeValues.put("Plan or blueprint", new String[] { "app" });
			alloweMimeValues.put("Preprint", new String[] { "app" });
			alloweMimeValues.put("Presentation", new String[] { "app", "text", "video" });
			alloweMimeValues.put("Recording, acoustical", new String[] { "audio" });
			alloweMimeValues.put("Recording, musical", new String[] { "audio" });
			alloweMimeValues.put("Recording, oral", new String[] { "audio" });
			alloweMimeValues.put("Software", new String[] { "app" });
			alloweMimeValues.put("Technical Report", new String[] { "app", "text" });
			alloweMimeValues.put("Thesis", new String[] { "app" });
			alloweMimeValues.put("Video", new String[] { "audio" });
			alloweMimeValues.put("Working Paper", new String[] { "app", "text" });

			String formaType = searchFormaType(item.getMetadata("dc.format.mimetype"));
			String type = item.getMetadata("dc.type");
			String[] formats = alloweMimeValues.get(type);

			for (String format : formats) {
				if (formaType.equals(format)) {
					sum += 1;
				}
			}

			comparisons++;
			score = sum / comparisons;
		}

		if (comparisons == 0) {
			System.out.println("El calculo de esta metrica no esta disponible");
		}
	}

	/**
	 * Load Lang Detect profiles, that is static files with data to detect
	 * languages
	 * 
	 * @throws AdminAssessmentException
	 */
	private void init() throws AdminAssessmentException {

		if (DetectorFactory.getLangList() == null || DetectorFactory.getLangList().size() > 0) {
			return;
		}

		List<String> profiles = new ArrayList<>();
		URL url = CoherenceAssessCommand.class.getResource("profiles");

		try {
			JarURLConnection urlcon = (JarURLConnection) url.openConnection();
			JarFile jar = urlcon.getJarFile();
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				String entry = entries.nextElement().getName();
				if (entry.contains("profiles/") && !entry.endsWith("profiles/")) {
					try (InputStream in = CoherenceAssessCommand.class.getClassLoader().getResourceAsStream(entry);) {
						profiles.add(IOUtils.toString(in));
					}
				}
			}
		} catch (IOException ioe) {
			log.error(ioe.getMessage());
			throw new AdminAssessmentException("Exception loading Lang Detect Profiles");
		}

		if (profiles.size() > 0) {
			try {
				DetectorFactory.loadProfile(profiles);
			} catch (LangDetectException lde) {
				log.error(lde.getMessage());
				throw new AdminAssessmentException("Exception reading Lang Detect profiles");
			}
		}
	}

	/**
	 * 
	 * @param text
	 *            - message which language is going to be parse
	 * @return language in which the message is wirtten
	 * @throws AdminAssessmentException
	 */
	private String detect(String text) throws AdminAssessmentException {
		try {
			Detector detector = DetectorFactory.create();
			detector.append(text);
			return detector.detect();
		} catch (LangDetectException le) {
			log.error(le.getMessage());
			throw new AdminAssessmentException("Exception detecting language with Lang Detect");
		}
	}

	/**
	 * Search for the type of the given format, checking in the file of valid
	 * format ah their types
	 * 
	 * @param metadata
	 *            - the format to be checked
	 * @return - type of the format given (app, video, audio, ...)
	 * @throws AdminAssessmentException
	 */
	private String searchFormaType(String metadata) throws AdminAssessmentException {

		String type = null;
		try {
			InputStream in = CoherenceAssessCommand.class.getResourceAsStream("allowedValues/media-types.csv");
			try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
				String line;
				while ((line = br.readLine()) != null) {
					String[] array = line.split(",");
					if ((metadata.contains(array[0]) || metadata.contains(array[1])) && array[1].contains("application/")) {
						type = "app";
						break;
					}
					if ((metadata.contains(array[0]) || metadata.contains(array[1])) && array[1].contains("audio/")) {
						type = "audio";
						break;
					}
					if ((metadata.contains(array[0]) || metadata.contains(array[1])) && array[1].contains("image/")) {
						type = "image";
						break;
					}
					if ((metadata.contains(array[0]) || metadata.contains(array[1])) && array[1].contains("text/")) {
						type = "text";
						break;
					}
					if ((metadata.contains(array[0]) || metadata.contains(array[1])) && array[1].contains("video/")) {
						type = "video";
						break;
					}
				}
			}
		} catch (IOException ioe) {
			log.error(ioe.getMessage());
			throw new AdminAssessmentException("Exception loading Lang Detect Profiles");
		}

		return type;

	}

	public AssessResult getResult() {

		String status = score > 0.0 ? "Success" : "Fail";
		String stringScore = new DecimalFormat("#.##").format(score);
		result.append("Item: ").append(handle);

		if (score > 0.7) {
			result.append(" has highly coherent data in these metadata fields: dc.language.iso, dc.type and dc.format.mimetype");
		}
		if (score > 0.3 && score <= 0.7) {
			result.append(" has medium coherence data in these metadata fields: dc.language.iso, dc.type and dc.format.mimetype");
		}
		if (score < 0.3) {
			result.append(" has low coherence data in these metadata fields: dc.language.iso, dc.type and dc.format.mimetype");
		}

		return new AssessResult("Coherence", score, handle, status, stringScore + ". " + result, assessmentExecuted);
	}

}