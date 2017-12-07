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

	public AdminAssessmentReport executeAssessment(DSpaceObject dso, Context context) {

		if (dso.getType() != Constants.ITEM) {
			return null;
		}

		double score = 0.0;
		boolean assessmentExecuted = true;
		StringBuilder result = new StringBuilder();
		Item item = (Item) dso;
		String handle = item.getHandle();
		
		result.append("Item: ").append(handle);

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
			}
		}
		
		//Build assessment result
		String status = score > 0.0 ? "Success" : "Fail";
		String stringScore = new DecimalFormat("#.##").format(score);
		AdminAssessmentReport assessResult = new AdminAssessmentReport("Availability", score, handle, status, stringScore + ". " + result,
				assessmentExecuted);
		return assessResult;


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

}