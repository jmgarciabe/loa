package org.dspace.loa;

import java.io.IOException;
import java.text.DecimalFormat;

import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.core.Context;

/**
 * Completeness assessment compares item metadata with some specific fields
 * obtained via input-forms.xml. This assessment returns a real number between 0
 * and 1 to indicate which fields are present in the item metadata, 1 indicates
 * that all checked fields are present in the item metadata, meanwhile 0
 * indicates that none of them were filled out.
 * 
 * @author Andres Salazar
 */

public class CompletenessAssessCommand implements AdminAssessmentCommandIntarface {

	/** Weight for Title metadata field value */
	private final double TITLE = 0.17;

	/** Weight for Subject metadata field value */
	private final double SUBJECT = 0.16;

	/** Weight for Abstract metadata field value */
	private final double ABSTRACT = 0.14;

	/** Weight for Author metadata field value */
	private final double AUTHOR = 0.13;

	/** Weight for Date metadata field value */
	private final double DATE = 0.12;

	/** Weight for Type metadata field value */
	private final double TYPE = 0.11;

	/** Weight for Language metadata field value */
	private final double LANG = 0.07;

	/** Weight for Description metadata field value */
	private final double DESCR = 0.05;

	/** Weight for Location metadata field value */
	private final double LOCATION = 0.03;

	/** Weight for Provenance (Status) metadata field value */
	private final double STATUS = 0.02;

	public AssessResult executeAssessment(DSpaceObject dso, Context context) {

		if (dso.getType() != Constants.ITEM) {
			return null;
		}
		
		double score = 0.0;
		boolean assessmentExecuted = true;
		StringBuilder result = new StringBuilder();
		Item item = (Item) dso;
		String handle = item.getHandle();

		if (item.getMetadata("dc.title") != null) {
			score = TITLE + score;
		}
		if (item.getMetadata("dc.subject") != null) {
			score = SUBJECT + score;
		}
		if (item.getMetadata("dc.description.abstract") != null) {
			score = ABSTRACT + score;
		}
		if (item.getMetadata("dc.contributor.author") != null) {
			score = AUTHOR + score;
		}
		if (item.getMetadata("dc.date.issued") != null) {
			score = DATE + score;
		}
		if (item.getMetadata("dc.type") != null) {
			score = TYPE + score;
		}
		if (item.getMetadata("dc.language.iso") != null) {
			score = LANG + score;
		}
		if (item.getMetadata("dc.description") != null) {
			score = DESCR + score;
		}
		if (item.getMetadata("dc.identifier.uri") != null) {
			score = LOCATION + score;
		}
		if (item.getMetadata("dc.description.provenance") != null) {
			score = STATUS + score;
		}
		
		//Build assessment result
		String status = score > 0.0 ? "Success" : "Fail";
		String stringScore = new DecimalFormat("#.##").format(score);

		result.append("Item: ").append(handle);
		if (score > 0.7) {
			result.append(" most of the most important metadata fields has been filled");
		}
		if ((score >= 0.3) && (score <= 0.7)) {
			result.append(" half of the most important metadata fields has been filled");
		}
		if (score < 0.3) {
			result.append(" very few of the most important metadata fields have been filled");
		}

		AssessResult assessResult = new AssessResult("Completeness", score, handle, status, stringScore + ". " + result,
				assessmentExecuted);

		return assessResult;

	}
	
}