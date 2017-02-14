<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  --%>
  
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"
    prefix="fmt" %>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<%@ page import="org.dspace.app.webui.servlet.admin.AdminAssessServlet" %>	
<%@ page import="java.util.Vector" %>
<%@ page import="org.dspace.content.Item" %>
<%@ page import="org.dspace.core.ConfigurationManager" %>
<%@ page import="org.dspace.eperson.EPerson" %>

<%@ page session="true"%>

<%
    Item item = (Item) request.getAttribute("item");

	String totalIndex = (String) request.getAttribute("totalIndex");
	String adminIndex = (String) request.getAttribute("adminIndex");
	String expIndex = (String) request.getAttribute("expIndex");
	String stdIndex = (String) request.getAttribute("stdIndex");
%>

<dspace:layout style="submission" titlekey="Learning Object Assessment parameterization"
               locbar="link"
               nocache="true">

	<h1>Assessment result report</h1>
	<h1><%=item.getName()%>: <small>Total Score = <%=totalIndex%> / 10 </small></h1>

	<div class="alert alert-info" role="alert">
		<p>The total score is a number between 1 and 10 that corresponds
			to the overall assessment of the learning object quality based on the
			judgments of the layers, dimensions and metrics parameterized by the
			system administrator.</p>
	</div>

	<div class="row">
	<%
		if (adminIndex != null){
	%>
		<div class="col-xs-6 col-md-4">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">Administrator Layer Index</h3>
					<div class="progress">
						<div class="progress-bar" role="progressbar"
							aria-valuenow="<%=adminIndex%>" aria-valuemin="0" aria-valuemax="100"
							style="width: <%=adminIndex%>%"><%=adminIndex%>%</div>
					</div>
				</div>
				<div class="panel-body">
					<%
 						Vector results = (Vector) session.getAttribute("LOA.results");
 						if (results != null && !results.isEmpty()) {
 							for (int index = 0; index < results.size(); index++) {
 								String resultsInfo = results.elementAt(index).toString();
 								String[] data = resultsInfo.split(",");
 								String layer = data[0];
 								String dimension = data[1];
 								String metric = data[2];
 								String val = data[3];
					%>
							<%
								if (layer.equals("1") && dimension.equals("Contextual")){
							%>
					<div class="well">
						<h4><%=dimension%> : <%=metric%></h4>
								<%
									if (Double.valueOf(val).doubleValue() > 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-success" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 50 && Double.valueOf(val).doubleValue() <= 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-info" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 25 && Double.valueOf(val).doubleValue() <= 50){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-warning" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() <= 25){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-danger" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
								%>
					</div>
							<%
								}
							%>
							
							<%
								if (layer.equals("1") && dimension.equals("Metadata")){
							%>
					<div class="well">
						<h4><%=dimension%> : <%=metric%></h4>
								<%
									if (Double.valueOf(val).doubleValue() > 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-success" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 50 && Double.valueOf(val).doubleValue() <= 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-info" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 25 && Double.valueOf(val).doubleValue() <= 50){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-warning" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() <= 25){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-danger" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
								%>
					</div>
							<%
								}
							%>
							
								<%
								if (layer.equals("1") && dimension.equals("Functional")){
							%>
					<div class="well">
						<h4><%=dimension%> : <%=metric%></h4>
								<%
									if (Double.valueOf(val).doubleValue() > 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-success" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 50 && Double.valueOf(val).doubleValue() <= 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-info" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 25 && Double.valueOf(val).doubleValue() <= 50){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-warning" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() <= 25){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-danger" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
								%>
					</div>
							<%
								}
							%>
					<%
 							}
 						}
					%>	
				</div>
			</div>
		</div>
	<%
		}
	%>
	<%
		if (expIndex != null){
	%>
		<div class="col-xs-6 col-md-4">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">Expert Layer Index</h3>
					<div class="progress">
						<div class="progress-bar" role="progressbar"
							aria-valuenow="<%=expIndex%>" aria-valuemin="0" aria-valuemax="100"
							style="width: <%=expIndex%>%"><%=expIndex%>%</div>
					</div>
				</div>
				<div class="panel-body">
					<%
 						Vector results = (Vector) session.getAttribute("LOA.results");
 						if (results != null && !results.isEmpty()) {
 							for (int index = 0; index < results.size(); index++) {
 								String resultsInfo = results.elementAt(index).toString();
 								String[] data = resultsInfo.split(",");
 								String layer = data[0];
 								String dimension = data[1];
 								String metric = data[2];
 								String val = data[3];
					%>
							<%
								if (layer.equals("2") && dimension.equals("Content")){
							%>
					<div class="well">
						<h4><%=dimension%> : <%=metric%></h4>
								<%
									if (Double.valueOf(val).doubleValue() > 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-success" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 50 && Double.valueOf(val).doubleValue() <= 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-info" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 25 && Double.valueOf(val).doubleValue() <= 50){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-warning" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() <= 25){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-danger" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
								%>
					</div>
							<%
								}
							%>
							
							<%
								if (layer.equals("2") && dimension.equals("Educational")){
							%>
					<div class="well">
						<h4><%=dimension%> : <%=metric%></h4>
								<%
									if (Double.valueOf(val).doubleValue() > 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-success" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 50 && Double.valueOf(val).doubleValue() <= 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-info" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 25 && Double.valueOf(val).doubleValue() <= 50){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-warning" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() <= 25){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-danger" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
								%>
					</div>
							<%
								}
							%>
							
							<%
								if (layer.equals("2") && dimension.equals("Esthetic")){
							%>
					<div class="well">
						<h4><%=dimension%> : <%=metric%></h4>
								<%
									if (Double.valueOf(val).doubleValue() > 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-success" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 50 && Double.valueOf(val).doubleValue() <= 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-info" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 25 && Double.valueOf(val).doubleValue() <= 50){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-warning" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() <= 25){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-danger" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
								%>
					</div>
							<%
								}
							%>
							
							<%
								if (layer.equals("2") && dimension.equals("Functional")){
							%>
					<div class="well">
						<h4><%=dimension%> : <%=metric%></h4>
								<%
									if (Double.valueOf(val).doubleValue() > 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-success" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 50 && Double.valueOf(val).doubleValue() <= 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-info" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 25 && Double.valueOf(val).doubleValue() <= 50){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-warning" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() <= 25){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-danger" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
								%>
					</div>
							<%
								}
							%>
							
							<%
								if (layer.equals("2") && dimension.equals("Metadata")){
							%>
					<div class="well">
						<h4><%=dimension%> : <%=metric%></h4>
								<%
									if (Double.valueOf(val).doubleValue() > 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-success" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 50 && Double.valueOf(val).doubleValue() <= 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-info" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 25 && Double.valueOf(val).doubleValue() <= 50){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-warning" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() <= 25){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-danger" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
								%>
					</div>
							<%
								}
							%>
					<%
 							}
 						}
					%>	
				
				</div>
			</div>
		</div>
	<%
		}
	%>	
	<%
		if (stdIndex != null){
	%>
		<div class="col-xs-6 col-md-4">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">Student Layer Index</h3>
					<div class="progress">
						<div class="progress-bar" role="progressbar"
							aria-valuenow="<%=stdIndex%>" aria-valuemin="0" aria-valuemax="100"
							style="width: <%=stdIndex%>%"><%=stdIndex%>%</div>
					</div>
				</div>
				<div class="panel-body">
				<%
 						Vector results = (Vector) session.getAttribute("LOA.results");
 						if (results != null && !results.isEmpty()) {
 							for (int index = 0; index < results.size(); index++) {
 								String resultsInfo = results.elementAt(index).toString();
 								String[] data = resultsInfo.split(",");
 								String layer = data[0];
 								String dimension = data[1];
 								String metric = data[2];
 								String val = data[3];
					%>
							<%
								if (layer.equals("3") && dimension.equals("Contextual")){
							%>
					<div class="well">
						<h4><%=dimension%> : <%=metric%></h4>
								<%
									if (Double.valueOf(val).doubleValue() > 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-success" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 50 && Double.valueOf(val).doubleValue() <= 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-info" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 25 && Double.valueOf(val).doubleValue() <= 50){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-warning" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() <= 25){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-danger" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
								%>
					</div>
							<%
								}
							%>
							
							<%
								if (layer.equals("3") && dimension.equals("Educational")){
							%>
					<div class="well">
						<h4><%=dimension%> : <%=metric%></h4>
								<%
									if (Double.valueOf(val).doubleValue() > 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-success" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 50 && Double.valueOf(val).doubleValue() <= 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-info" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 25 && Double.valueOf(val).doubleValue() <= 50){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-warning" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() <= 25){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-danger" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
								%>
					</div>
							<%
								}
							%>
							
							<%
								if (layer.equals("3") && dimension.equals("Esthetic")){
							%>
					<div class="well">
						<h4><%=dimension%> : <%=metric%></h4>
								<%
									if (Double.valueOf(val).doubleValue() > 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-success" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 50 && Double.valueOf(val).doubleValue() <= 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-info" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 25 && Double.valueOf(val).doubleValue() <= 50){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-warning" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() <= 25){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-danger" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
								%>
					</div>
							<%
								}
							%>
							
							<%
								if (layer.equals("3") && dimension.equals("Functional")){
							%>
					<div class="well">
						<h4><%=dimension%> : <%=metric%></h4>
								<%
									if (Double.valueOf(val).doubleValue() > 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-success" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 50 && Double.valueOf(val).doubleValue() <= 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-info" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 25 && Double.valueOf(val).doubleValue() <= 50){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-warning" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() <= 25){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-danger" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
								%>
					</div>
							<%
								}
							%>
							
							<%
								if (layer.equals("3") && dimension.equals("Metadata")){
							%>
					<div class="well">
						<h4><%=dimension%> : <%=metric%></h4>
								<%
									if (Double.valueOf(val).doubleValue() > 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-success" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 50 && Double.valueOf(val).doubleValue() <= 75){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-info" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() > 25 && Double.valueOf(val).doubleValue() <= 50){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-warning" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
									if (Double.valueOf(val).doubleValue() <= 25){
								%>
							<div class="progress">
								<div class="progress-bar progress-bar-danger" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>%</div>
							</div>
								<%
									}
								%>
					</div>
							<%
								}
							%>
					<%
 							}
 						}
					%>	
				</div>
			</div>
		</div>
	<%
		}
	%>	
	</div>

	<div class="row">
		<div class="btn-group" role="group" aria-label="...">
			<form method="get"
				action="<%=request.getContextPath()%>/handle/<%=item.getHandle()%>">
				<input class="btn btn-success btn-lg" type="submit" name="submit_ok"
					value="OK" />
			</form>
		</div>

		<div class="btn-group" role="group" aria-label="...">

			<!-- Trigger the modal with a button -->
			<form method="post"
				action="<%=request.getContextPath()%>/tools/LOAssessment/admin-assess">
				<input type="hidden" name="item_id" value="<%=item.getID()%>" />
				<input type="hidden" name="action" value="<%=AdminAssessServlet.DELETE_ASSESS%>" />
				<button type="button" class="btn btn-warning btn-lg"
					data-toggle="modal" data-target="#modalDeleteAssess">Delete
					all</button>

				<!-- Modal -->
				<div id="modalDeleteAssess" class="modal fade" role="dialog">
					<div class="modal-dialog">

						<!-- Modal content-->
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal">&times;</button>
								<h4 class="modal-title">Confirm assessment deletion</h4>
							</div>
							<div class="modal-body">
								<div class="alert alert-warning">
									<strong>Caution!!!</strong> This action will delete all
									assessment data for this object, it will not longer available.
									Are you sure?
								</div>
							</div>
							<div class="modal-footer">
								<input class="btn btn-primary pull-right col-md-12"
									type="submit" name="submit_yes" value="Yes" /> <input
									class="btn btn-primary pull-right col-md-12" type="submit"
									name="submit_no" value="No" />
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>

</dspace:layout>