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

	<h1>Assessment results report</h1>
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
							style="width: <%=adminIndex%>%"><%=adminIndex%></div>
					</div>
				</div>
				<div class="panel-body">
				<%
 					Vector results = (Vector) session.getAttribute("LOA.results");
 					if (results != null && !results.isEmpty()) {
 						String dimTemp = null;
 						for (int index = 0; index < results.size(); index++) {
 							String resultsInfo = results.elementAt(index).toString();
 							System.out.println(resultsInfo);
 							String[] data = resultsInfo.split(",");
 							String layer = data[0];
 							String dimension = data[1];
 							String metric = data[2];
 							String val = data[3];
 							dimTemp = dimension;
 							if (layer.equals("Administrator")){
				%>
					<div class="panel panel-default">
						<%
							//if (!dimension.equals(dimTemp)){
						%>
						<div class="panel-heading">
							<h3 class="panel_title">
								<%=dimension%>
							</h3>
						</div>
						<%
							//}
						%>
						<div class="panel-body">
						<%
							if (Double.valueOf(val).doubleValue() > 75){
						%>
							<div class="progress">
								<div class="progress-bar progress-bar-success" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>% <%=metric%></div>
							</div>
						<%
							}
							if (Double.valueOf(val).doubleValue() > 50 && Double.valueOf(val).doubleValue() <= 75){
						%>
							<div class="progress">
								<div class="progress-bar progress-bar-info" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>% <%=metric%></div>
							</div>
						<%
							}
							if (Double.valueOf(val).doubleValue() > 25 && Double.valueOf(val).doubleValue() <= 50){
						%>
							<div class="progress">
								<div class="progress-bar progress-bar-warning" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>% <%=metric%></div>
							</div>
						<%
							}
							if (Double.valueOf(val).doubleValue() <= 25){
						%>
							<div class="progress">
								<div class="progress-bar progress-bar-danger" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>% <%=metric%></div>
							</div>
						<%
							}
						%>
						</div>
					</div>
				<%
 							}
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
							style="width: <%=expIndex%>%"><%=expIndex%></div>
					</div>
				</div>
				<div class="panel-body">
				<%
 					Vector results = (Vector) session.getAttribute("LOA.results");
 					if (results != null && !results.isEmpty()) {
 						String dimTemp = null;
 						for (int index = 0; index < results.size(); index++) {
 							String resultsInfo = results.elementAt(index).toString();
 							String[] data = resultsInfo.split(",");
 							String layer = data[0];
 							String dimension = data[1];
 							String metric = data[2];
 							String val = data[3];
 							dimTemp = dimension;
 							if (layer.equals("Expert")){
				%>
					<div class="panel panel-default">
						<%
							if (!dimension.equals(dimTemp)){
						%>
						<div class="panel-heading">
							<h3 class="panel_title">
								<%=dimension%>
							</h3>
						</div>
						<%
							}
						%>
						<div class="panel-body">
						<%
							if (Double.valueOf(val).doubleValue() > 75){
						%>
							<div class="progress">
								<div class="progress-bar progress-bar-success" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>% <%=metric%></div>
							</div>
						<%
							}
							if (Double.valueOf(val).doubleValue() > 50 && Double.valueOf(val).doubleValue() <= 75){
						%>
							<div class="progress">
								<div class="progress-bar progress-bar-info" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>% <%=metric%></div>
							</div>
						<%
							}
							if (Double.valueOf(val).doubleValue() > 25 && Double.valueOf(val).doubleValue() <= 50){
						%>
							<div class="progress">
								<div class="progress-bar progress-bar-warning" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>% <%=metric%></div>
							</div>
						<%
							}
							if (Double.valueOf(val).doubleValue() <= 25){
						%>
							<div class="progress">
								<div class="progress-bar progress-bar-danger" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>% <%=metric%></div>
							</div>
						<%
							}
						%>
						</div>
					</div>
				<%
 							}
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
							style="width: <%=stdIndex%>%"><%=stdIndex%></div>
					</div>
				</div>
				<div class="panel-body">
				<%
 					Vector results = (Vector) session.getAttribute("LOA.results");
 					if (results != null && !results.isEmpty()) {
 						String dimTemp = null;
 						for (int index = 0; index < results.size(); index++) {
 							String resultsInfo = results.elementAt(index).toString();
 							String[] data = resultsInfo.split(",");
 							String layer = data[0];
 							String dimension = data[1];
 							String metric = data[2];
 							String val = data[3];
 							dimTemp = dimension;
 							if (layer.equals("Student")){
				%>
					<div class="panel panel-default">
						<%
							if (!dimension.equals(dimTemp)){
						%>
						<div class="panel-heading">
							<h3 class="panel_title">
								<%=dimension%>
							</h3>
						</div>
						<%
							}
						%>
						<div class="panel-body">
						<%
							if (Double.valueOf(val).doubleValue() > 75){
						%>
							<div class="progress">
								<div class="progress-bar progress-bar-success" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>% <%=metric%></div>
							</div>
						<%
							}
							if (Double.valueOf(val).doubleValue() > 50 && Double.valueOf(val).doubleValue() <= 75){
						%>
							<div class="progress">
								<div class="progress-bar progress-bar-info" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>% <%=metric%></div>
							</div>
						<%
							}
							if (Double.valueOf(val).doubleValue() > 25 && Double.valueOf(val).doubleValue() <= 50){
						%>
							<div class="progress">
								<div class="progress-bar progress-bar-warning" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>% <%=metric%></div>
							</div>
						<%
							}
							if (Double.valueOf(val).doubleValue() <= 25){
						%>
							<div class="progress">
								<div class="progress-bar progress-bar-danger" role="progressbar"
									aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
									style="width: <%=val%>%"><%=val%>% <%=metric%></div>
							</div>
						<%
							}
						%>
						</div>
					</div>
				<%
 							}
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

	<div class="btn-group" role="group" aria-label="...">
		<form method="get"
			action="<%=request.getContextPath()%>/handle/<%=item.getHandle()%>">
			<input class="btn btn-info btn-lg" type="submit"
				name="submit_ok" value="OK" />
		</form>
		<input class="btn btn-success btn-lg" type="submit"
					name="submit_item_assess"
					value="Delete all" />
	</div>
	
</dspace:layout>