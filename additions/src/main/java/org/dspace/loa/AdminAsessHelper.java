package org.dspace.loa;

import java.text.DecimalFormat;

import org.dspace.content.DSpaceObject;


public class AdminAsessHelper {

	private AdminAssessmentCommandIntarface criteriaComand;
	
	public AssessResult assess(String criteria, DSpaceObject dso){
		
		switch (criteria){
			case "Availability":
				criteriaComand = new AvailabilityAssessCommand();
				break;
			case "Coherence":
				criteriaComand = new CoherenceAssessCommand();
				break;
			case "Completeness":
				criteriaComand = new CompletenessAssessCommand(); 
				break;
			case "Consistency":
				criteriaComand = new ConsistencyAssessCommand();
				break;
			case "Reusability":
				
				break;
			case "Visibility":
				
				break;
		}
		criteriaComand.executeAssessment(dso);
		return criteriaComand.getResult();
	}
}
if (assess2Perform.equals("Visibility")) {
	result = VisibilityAssess.perform(context, item);
	if (result >= 0.0) {
		ASSESS_SUCCESS = true;
		request.setAttribute("task_result", new AssessResult("Visibility", handle, SUCCESS_STATUS, new DecimalFormat(
				"#.##").format(result) + ". " + VisibilityAssess.getResults(context, item), ASSESS_SUCCESS));
		Metric.addAssessValue(context, result, "Visibility", 1, itemID);
	} else {
		request.setAttribute("task_result", new AssessResult("Visibility", handle, FAIL_STATUS, null, ASSESS_SUCCESS));
	}
}