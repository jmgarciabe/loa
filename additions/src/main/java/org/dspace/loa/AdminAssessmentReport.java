package org.dspace.loa;


/**
 * Object used to store info about the result of an administration assessment
 */
public class AdminAssessmentReport
{

    /** the assess identifier*/
    private String task;
    
    /** the asses result score  */
    private double score;

    /** the handle of the dspace-object */
    private String handle;

    /** the status string */
    private String status;

    /** the result string */
    private String result;

    /** Is the assessment */
    private boolean isSuccess;
    
    public AdminAssessmentReport(){
    	this.task = "";
        this.score = 0.0;
        this.handle = "";
        this.status = "";
        this.result = "";
        this.isSuccess = false;
    }

    public AdminAssessmentReport(String assess, double score, String handle, String status, String result, boolean isSuccess)
    {
        this.task = assess;
        this.score = score;
        this.handle = handle;
        this.status = status;
        this.result = result;
        this.isSuccess = isSuccess;
    }

    public String getTask()
    {
        return task;
    }

    public String getStatus()
    {
        return status;
    }

    public String getResult()
    {
        return result;
    }

    public String getHandle()
    {
        return handle;
    }

    public boolean isSuccess()
    {
        return isSuccess;
    }

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

}


