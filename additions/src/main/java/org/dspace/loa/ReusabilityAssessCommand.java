package org.dspace.loa;

import java.io.IOException;
import java.text.DecimalFormat;

import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.core.Context;

/**
 * Reusability assessment takes item type and ispartofseries metadata values and
 * taking this into account defines the structure and granularity level of item
 * entered. This assessment returns a real number between 0 and 1 to indicate
 * how reusable is the item assessed, 1 indicates that the item is fully
 * reusable in all educational contexts, meanwhile 0 indicates that this is not
 * very usable appart from the current educational context.
 * 
 * @author Andres Salazar
 */

public class ReusabilityAssessCommand implements AdminAssessmentCommandIntarface {

	/** Store the assessment result score */
	private double score = 0.0;

	/** Store assessment result as text adding needed extra information */
	private StringBuilder result = new StringBuilder();

	/** whether the assessment process has been carried out or not */
	private boolean assessmentExecuted = false;

	/** The item's handle */
	private String handle = "";

	/** Weight for high item's granularity */
	private static final double HIGH = 0.1;

	/** Weight for average item's granularity */
	private static final double AVERAGE = 0.5;

	/** Weight for low item's granularity */
	private static final double LOW = 0.9;

	/** Weight for item's Atomic structure */
	private static final double ATOMIC = 0.9;

	/** Weight for item's Collection structure */
	private static final double COLLECTION = 0.75;

	/** Weight for item's Network structure */
	private static final double NETWORK = 0.5;

	/** Weight for item's Hierarchical structure */
	private static final double HIERACL = 0.25;

	/** Weight for item's Linear structure */
	private static final double LINEAR = 0.1;

	public void executeAssessment(DSpaceObject dso, Context context) {
		if (dso.getType() != Constants.ITEM) {
			return;
		}

		Item item = (Item) dso;
		handle = item.getHandle();
		assessmentExecuted = true;
		double sum = 0.0;
		int comparisons = 0;

		if (item.getMetadata("dc.type") != null) {
			comparisons++;
			sum += checkStructureRule(item.getMetadata("dc.type"));
			score = sum / comparisons;
		}
		if (item.getMetadata("dc.type") != null && item.getMetadata("dc.relation.ispartofseries") == null) {
			comparisons++;
			sum += LOW;
			score = sum / comparisons;
		}
		if (item.getMetadata("dc.type") != null && item.getMetadata("dc.relation.ispartofseries") != null) {
			comparisons++;
			sum += checkGranularityRule(item.getMetadata("dc.type"));
			score = sum / comparisons;
		} else if (comparisons == 0)
			System.out.println("El calculo de esta metrica no esta disponible");

	}

	private static double checkGranularityRule(String contentType) {
		// TODO Auto-generated method stub
		double granularity = 0.0;

		if (contentType.equals("Animation"))
			granularity = AVERAGE;
		if (contentType.equals("Article"))
			granularity = AVERAGE;
		if (contentType.equals("Book"))
			granularity = AVERAGE;
		if (contentType.equals("Book chapter"))
			granularity = AVERAGE;
		if (contentType.equals("Musical Score"))
			granularity = AVERAGE;
		if (contentType.equals("Recording, acoustical"))
			granularity = AVERAGE;
		if (contentType.equals("Recording, musical"))
			granularity = AVERAGE;
		if (contentType.equals("Recording, oral"))
			granularity = AVERAGE;
		if (contentType.equals("Technical Report"))
			granularity = AVERAGE;
		if (contentType.equals("Video"))
			granularity = AVERAGE;
		if (contentType.equals("Working Paper"))
			granularity = AVERAGE;
		if (contentType.equals("Dataset"))
			granularity = HIGH;
		if (contentType.equals("Learning Object"))
			granularity = HIGH;
		if (contentType.equals("Image"))
			granularity = HIGH;
		if (contentType.equals("Image, 3-D"))
			granularity = HIGH;
		if (contentType.equals("Map"))
			granularity = HIGH;
		if (contentType.equals("Plan or blueprint"))
			granularity = HIGH;
		if (contentType.equals("Preprint"))
			granularity = HIGH;
		if (contentType.equals("Presentation"))
			granularity = HIGH;
		if (contentType.equals("Software"))
			granularity = HIGH;
		if (contentType.equals("Thesis"))
			granularity = HIGH;

		return granularity;

	}

	private static double checkStructureRule(String contentType) {
		// TODO Auto-generated method stub
		double structure = 0.0;

		if (contentType.equals("Animation"))
			structure = ATOMIC;
		if (contentType.equals("Learning Object"))
			structure = ATOMIC;
		if (contentType.equals("Image"))
			structure = ATOMIC;
		if (contentType.equals("Image, 3-D"))
			structure = ATOMIC;
		if (contentType.equals("Plan or blueprint"))
			structure = ATOMIC;
		if (contentType.equals("Preprint"))
			structure = ATOMIC;
		if (contentType.equals("Article"))
			structure = COLLECTION;
		if (contentType.equals("Book chapter"))
			structure = COLLECTION;
		if (contentType.equals("Map"))
			structure = COLLECTION;
		if (contentType.equals("Technical Report"))
			structure = COLLECTION;
		if (contentType.equals("Working Paper"))
			structure = COLLECTION;
		if (contentType.equals("Software"))
			structure = NETWORK;
		if (contentType.equals("Other"))
			structure = NETWORK;
		if (contentType.equals("Book"))
			structure = HIERACL;
		if (contentType.equals("Presentation"))
			structure = HIERACL;
		if (contentType.equals("Thesis"))
			structure = HIERACL;
		if (contentType.equals("Dataset"))
			structure = LINEAR;
		if (contentType.equals("Musical Score"))
			structure = LINEAR;
		if (contentType.equals("Recording, acoustical"))
			structure = LINEAR;
		if (contentType.equals("Recording, musical"))
			structure = LINEAR;
		if (contentType.equals("Recording, oral"))
			structure = LINEAR;
		if (contentType.equals("Video"))
			structure = LINEAR;

		return structure;

	}

	public AssessResult getResult() {

		String status = score > 0.0 ? "Success" : "Fail";
		String stringScore = new DecimalFormat("#.##").format(score);

		result.append("Item: ").append(handle);
		if (score > 0.7) {
			result.append(" has a high level of reusability");
		}
		if ((score >= 0.3) && (score <= 0.7)) {
			result.append(" has a medium level of reusability");
		}
		if (score < 0.3) {
			result.append(" has a low level of reusability");
		}

		AssessResult assessResult = new AssessResult("Reusability", score, handle, status, stringScore + ". " + result,
				assessmentExecuted);
		return assessResult;
	}

}