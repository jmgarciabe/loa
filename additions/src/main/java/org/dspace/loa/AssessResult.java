/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.loa;


/**
 *
 * @author Keiji Suzuki (adapted by Andres Salazar)
 */
public class AssessResult
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

    /** Is the curation success? */
    private boolean isSuccess = false;

    public AssessResult(String assess, double score, String handle, String status, String result, boolean isSuccess)
    {
        this.task = assess;
        this.score = 0.0;
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


