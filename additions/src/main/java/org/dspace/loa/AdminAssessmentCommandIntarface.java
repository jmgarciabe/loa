package org.dspace.loa;

import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;

public interface AdminAssessmentCommandIntarface {
	/**
	 * Execute specific assessment over the given item
	 * @param dso - the DSpace item
	 * @throws AdminAssessmentException - may throw an custom exception
	 */
	public void executeAssessment(DSpaceObject dso, Context context) throws AdminAssessmentException;
	/**
	 * Return object with the assessment result in convenient way with extra data
	 * @return assessment result object 
	 */
	public AssessResult getResult();
}
