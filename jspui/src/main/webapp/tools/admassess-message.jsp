<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%
	AdminAssessmentReport result = (AdminAssessmentReport) request.getAttribute("task_result");
    if (result != null)
    {
        boolean isSuccess = result.isSuccess();
        String resultClass = (isSuccess ? "success" : "danger");
%>
    <div class="alert alert-<%= resultClass %>">
      <b>
      	<h4>ASSESSED METRIC: <%= result.getTask() %></h4>
      </b>
<%
		if (isSuccess)
        {
%>
      <p class=".text-success"> The assessment was completed successfully.</p>
      <br>
      <div class="task-message">
      	<h4>STATUS: <%= result.getStatus() %></h4>
      	<h4>RESULT: <%= result.getResult() %></h4>
      </div>
<%
        }
        else
        {
%>
	<p class=".text-danger">The assessment exited unexpectedly or failed. For
		more information, please contact the site administrator or check your
		system logs.</p>
	<br>
	<div class="task-message">
       <h4>STATUS: <%= result.getStatus() %></h4>
    </div>
<%
        }
%>
     
    </div>
<%   
     }
%>
   