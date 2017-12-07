<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  --%>

<%@page import="org.dspace.loa.AssessmentMetric"%>
<%@page import="org.dspace.loa.AssessmentResult"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Map"%>
<%@ page contentType="text/html;charset=UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace"%>

<%@ page import="org.dspace.app.webui.servlet.admin.AdminAssessServlet"%>
<%@ page import="org.dspace.content.Item"%>
<%@ page import="org.dspace.core.ConfigurationManager"%>
<%@ page import="org.dspace.eperson.EPerson"%>

<%@ page session="true"%>

<%
	Item item = (Item) request.getAttribute("item");
	String totalIndex = (String) request.getAttribute("totalIndex");
	Map<String, String> layerIndexes = (Map<String, String>) request.getAttribute("layerIndexes");
%>

<dspace:layout style="submission" title="Learning Object Assessment Results Report" locbar="link"
	nocache="true">

	<h1>Assessment result report</h1>
	<h1><%=item.getName()%>: <small>Total Score = <%=totalIndex%> / 10
		</small>
	</h1>

	<div class="alert alert-info" role="alert">
		<p>The total score is a number between 1 and 10 that corresponds to the overall assessment of
			the learning object quality based on the judgments of the layers, dimensions and metrics
			parameterized by the system administrator.</p>
	</div>

	<div class="row">

		<%
			for (Entry<String, String> index : layerIndexes.entrySet()) {
					if (index.getValue() != null && index.getValue().length() > 0) {
						int idxLayer = Integer.valueOf(index.getKey());
						String tittle = idxLayer == 1 ? "Administrator" : idxLayer == 2 ? "Expert" : idxLayer == 3 ? "Student"
								: "";
						tittle += " Layer Index";
		%>
		<div class="col-xs-6 col-md-4">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title"><%=tittle%></h3>
					<br>
					<div class="progress">
						<div class="progress-bar" role="progressbar" aria-valuenow="<%=index.getValue()%>"
							aria-valuemin="0" aria-valuemax="100" style="width: <%=index.getValue()%>%"><%=index.getValue()%>%
						</div>
					</div>
				</div>
				<div class="panel-body">
					<%
						List<AssessmentResult> results = (List<AssessmentResult>) session.getAttribute("LOA.results");
									for (AssessmentResult res : results) {
										AssessmentMetric m = res.getAssessmentMetric();
										String progresBarType = "";

										if (m.getLayer().getId() == idxLayer && res.getValue() != null) {
					%>
					<h4>
						<small><%=m.getDimension().getName() %>: <%=m.getCriteria().getName()%></small>
					</h4>
					<%
						double val = res.getValue()*100;
						if (val > 75) {
												progresBarType = "progress-bar-success";
											} else if (val > 50 && val <= 75) {
												progresBarType = "progress-bar-info";
											} else if (val > 25 && val <= 50) {
												progresBarType = "progress-bar-warning";
											} else if (val <= 25) {
												progresBarType = "progress-bar-danger";
											}
					%>
					<div class="progress">
						<div class="progress-bar <%=progresBarType%>" role="progressbar"
							aria-valuenow="<%=val%>" aria-valuemin="0" aria-valuemax="100"
							style="min-width: 2em; width: <%=val%>%;"><%=val%>%
						</div>
					</div>

					<%
						}
									}
					%>
				</div>
			</div>
		</div>
		<%
			}
				}
		%>
	</div>
	<div class="row">
		<div class="col-xs-12">
			<div class="pull-left">
				<form method="get" action="<%=request.getContextPath()%>/tools/LOAssessment">
					<input type="hidden" name="item_id" value="<%=item.getID()%>" /> <input
						class="btn btn-success btn-lg" type="submit" name="submit_ok" value="OK" />
				</form>
			</div>
			<div class="pull-left" style="padding-left: 5px;">
				<button type="button" class="btn btn-warning btn-lg" data-toggle="modal"
					data-target="#modalDeleteAssess">Delete all</button>
			</div>

			<!-- Trigger the modal with a button -->
			<form method="post" action="<%=request.getContextPath()%>/tools/LOAssessment/admin-assess">
				<input type="hidden" name="item_id" value="<%=item.getID()%>" /> <input type="hidden"
					name="action" value="<%=AdminAssessServlet.DELETE_ASSESS%>" />

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
									<strong>Caution!!!</strong> This action will delete all assessment data for this object, it
									will not longer available. Are you sure?
								</div>
							</div>
							<div class="modal-footer">
								<input class="btn btn-warning" type="submit" name="submit_yes" value="Yes" />
								<button class="btn btn-primary" type="button" data-dismiss="modal" value="No">No</button>
							</div>
						</div>
					</div>
				</div>

			</form>
		</div>
	</div>

</dspace:layout>