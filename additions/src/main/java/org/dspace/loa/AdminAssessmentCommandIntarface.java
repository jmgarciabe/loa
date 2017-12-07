package org.dspace.loa;

import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;

public interface AdminAssessmentCommandIntarface {
	/**
	 * Execute specific assessment over the given item
	 * @param dso - the DSpace item
	 * @throws AdminAssessmentException - may throw an custom exception
	 */
	public AdminAssessmentReport executeAssessment(DSpaceObject dso, Context context) throws AdminAssessmentException;

}
