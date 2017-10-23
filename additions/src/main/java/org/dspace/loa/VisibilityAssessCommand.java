package org.dspace.loa;

import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;
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
 * Visibility assessment looks for number of item views and number of file
 * downloads in solr index to calculate relation between LO visits and downloads
 * and the total number of visits and downloads in the repository. This
 * assessment returns a real number between 0 and 1 to indicate how visible is
 * that LO in the repository, 1 indicates that the LO is very "popular" in the
 * item repository, meanwhile 0 indicates that any usuer has used it.
 * 
 * @author Kim Shepherd (Adapted by Andres Salazar)
 */

public class VisibilityAssessCommand implements AdminAssessmentCommandIntarface {

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

		handle = dso.getHandle();
		assessmentExecuted = true;
		double itemVisits = 0;
		double collVisits = 0;
		double nonCollVisits = 0;
		double nonCollSum = 0;

		// Gets the total number of visits by item from Solr index
		try {
			StatisticsListing statListing = new StatisticsListing(new StatisticsDataVisits(dso));

			statListing.setTitle("Total Visits");
			statListing.setId("list1");

			DatasetDSpaceObjectGenerator dsoAxis = new DatasetDSpaceObjectGenerator();
			dsoAxis.addDsoChild(dso.getType(), 10, false, -1);
			statListing.addDatasetGenerator(dsoAxis);
			Dataset dataset = statListing.getDataset(context);

			if (dataset != null) {
				String[][] matrix = dataset.getMatrix();

				for (int i = 0; i < matrix.length; i++) {
					for (int j = 0; j < matrix[i].length; j++) {
						itemVisits = new Integer(matrix[i][j]).doubleValue();
					}
				}
			}
		} catch (Exception e) {
			log.error("Error occurred while creating statistics for dso with ID: " + dso.getID() + " and type " + dso.getType()
					+ " and handle: " + dso.getHandle(), e);
			throw new AdminAssessmentException("Error occurred while creating statistics");

		}

		// Gets the total number of visits by collection where belongs the item
		// assessed from Solr index
		try {
			StatisticsListing statListing = new StatisticsListing(new StatisticsDataVisits(dso.getParentObject()));

			statListing.setTitle("Total Collection Visits");
			statListing.setId("list3");

			DatasetDSpaceObjectGenerator dsoAxis = new DatasetDSpaceObjectGenerator();
			dsoAxis.addDsoChild(dso.getParentObject().getType(), 10, false, -1);
			statListing.addDatasetGenerator(dsoAxis);
			Dataset dataset = statListing.getDataset(context);

			if (dataset != null) {
				String[][] matrix = dataset.getMatrix();

				for (int i = 0; i < matrix.length; i++) {
					for (int j = 0; j < matrix[i].length; j++) {
						collVisits = new Integer(matrix[i][j]).doubleValue();
					}
				}
			}
		} catch (Exception e) {
			log.error("Error occurred while creating statistics for dso with ID: " + dso.getID() + " and type " + dso.getType()
					+ " and handle: " + dso.getHandle(), e);
			throw new AdminAssessmentException("Error occurred while creating statistics");
		}

		// Gets the total number of visits by collections where doesn't belong
		// the item assessed from Solr index
		try {
			Item item = (Item) dso;

			Collection[] nonColl = item.getCollectionsNotLinked();

			for (int k = 0; k < nonColl.length; k++) {
				StatisticsListing statListing = new StatisticsListing(new StatisticsDataVisits(nonColl[k]));

				statListing.setTitle("Total NonInCollection Visits");
				statListing.setId("list4");

				DatasetDSpaceObjectGenerator dsoAxis = new DatasetDSpaceObjectGenerator();
				dsoAxis.addDsoChild(nonColl[k].getType(), 10, false, -1);
				statListing.addDatasetGenerator(dsoAxis);
				Dataset dataset = statListing.getDataset(context);

				if (dataset != null) {
					String[][] matrix = dataset.getMatrix();

					for (int i = 0; i < matrix.length; i++) {
						for (int j = 0; j < matrix[i].length; j++) {
							nonCollVisits = new Integer(matrix[i][j]).doubleValue();
							nonCollSum += nonCollVisits;
						}
					}
				}
			}
		} catch (Exception e) {

			log.error("Error occurred while creating statistics for dso with ID: " + dso.getID() + " and type " + dso.getType()
					+ " and handle: " + dso.getHandle(), e);
			throw new AdminAssessmentException("Error occurred while creating statistics");

		}

		if ((collVisits + nonCollSum) > 0)
			score = itemVisits / (collVisits + nonCollSum);

	}

	public AssessResult getResult() {

		String status = score > 0.0 ? "Success" : "Fail";
		String stringScore = new DecimalFormat("#.##").format(score);

		result.append("Item: ").append(handle);
		if (score > 0.7) {
			result.append(" is highly popular in the repository");
		}
		if ((score >= 0.3) && (score <= 0.7)) {
			result.append(" is frequently visited in the repository");
		}
		if (score < 0.3) {
			result.append(" is not so popular in the repository");
		}

		AssessResult assessResult = new AssessResult("Visibility",score, handle, status, stringScore + ". " + result,
				assessmentExecuted);
		return assessResult;
	}

}