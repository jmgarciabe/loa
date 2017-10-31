package org.dspace.loa;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.Metadatum;
import org.dspace.core.Constants;
import org.dspace.core.Context;

/**
 * Availability assessment checks that all links stored in
 * anyschema.anyelement.uri metadata fields return a 2xx status code. This link
 * checker can be enhanced by extending this class, and overriding the getURLs
 * and checkURL methods. For instance, to check URLs that appear in all metadata
 * fields where the field starts with http:// or https://. This metric returns
 * one or zero to indicate if the resource location (exposed in uri field) is
 * available or it is not.
 * 
 * @author Stuart Lewis (Adapted by Andres Salazar)
 */

public class AvailabilityAssessCommand implements AdminAssessmentCommandIntarface {

	/** Store the assessment result score */
	private double score = 0.0;
	
	/** Store assessment result as text adding needed extra information */
	private StringBuilder result = new StringBuilder();
	
	/** whether the assessment process has been carried out or not */
	private boolean assessmentExecuted = false;
	
	/** The item's handle */
	private String handle = "";

	
	
	
	public void executeAssessment(DSpaceObject dso, Context context) {

		if (dso.getType() == Constants.ITEM) {

			Item item = (Item) dso;
			handle = getItemHandle(item);
			result.append("Item: ").append(handle);
			assessmentExecuted = true;

			// Get the URLs
			List<String> urls = getURLs(item);

			// Check the URLs
			for (String url : urls) {
				boolean ok = checkURL(url);

				if (ok) {
					score = 1.0;
					result.append(" - " + url + " = " + getResponseStatus(url) + " - OK");
				} else {
					result.append(" - " + url + " = " + getResponseStatus(url) + " - FAILED");
					return;
				}
			}
		}

		return;
	}

	/**
	 * Get the URLs to check
	 * 
	 * @param item
	 *            The item to extract URLs from
	 * @return An array of URL Strings
	 */
	private List<String> getURLs(Item item) {
		// Get URIs from anyschema.anyelement.uri.*
		Metadatum[] urls = item.getMetadata(Item.ANY, Item.ANY, "uri", Item.ANY);
		ArrayList<String> theURLs = new ArrayList<String>();
		for (Metadatum url : urls) {
			theURLs.add(url.value);
		}
		return theURLs;
	}

	/**
	 * Check the URL and perform appropriate reporting
	 * 
	 * @param url
	 *            The URL to check
	 * @return If the URL was OK or not
	 */
	private boolean checkURL(String url) {
		// Link check the URL
		int httpStatus = getResponseStatus(url);

		if ((httpStatus >= 200) && (httpStatus < 300))
			return true;
		else
			return false;
	}

	/**
	 * Get the response code for a URL. If something goes wrong opening the URL,
	 * a response code of 0 is returned.
	 * 
	 * @param url
	 *            The url to open
	 * @return The HTTP response code (e.g. 200 / 301 / 404 / 500)
	 */
	private int getResponseStatus(String url) {
		try {
			URL theURL = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) theURL.openConnection();
			connection.setConnectTimeout(30000);
			int code = connection.getResponseCode();
			connection.disconnect();

			return code;

		} catch (IOException ioe) {
			// Must be a bad URL
			// log.debug("Bad link: " + ioe.getMessage());
			return 0;
		}
	}

	/**
	 * Internal utitity method to get a description of the handle
	 * 
	 * @param item
	 *            The item to get a description of
	 * @return The handle, or in workflow
	 */
	private String getItemHandle(Item item) {
		String handle = item.getHandle();
		return (handle != null) ? handle : " in workflow";
	}

	public AssessResult getResult() {
		// The results that we'll return
		String status = score > 0.0 ? "Success": "Fail";
		String stringScore = new DecimalFormat("#.##").format(score);
		AssessResult  assessResult =  new AssessResult("Availability", score, handle, status,  stringScore + ". " + result, assessmentExecuted);
		return assessResult;
	}
}