<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  - Show form allowing edit of collection metadata
  -
  - Attributes:
  -    item        - item to edit
  -    collections - collections the item is in, if any
  -    handle      - item's Handle, if any (String)
  -    dc.types    - MetadataField[] - all metadata fields in the registry
  --%>

<%@page import="org.dspace.loa.AssessmentMetric"%>
<%@page import="java.util.List"%>
<%@ page contentType="text/html;charset=UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace"%>

<%@ page import="org.dspace.app.webui.servlet.admin.InitialParamServlet"%>
<%@ page import="org.dspace.content.Item"%>
<%@ page import="org.dspace.core.ConfigurationManager"%>
<%@ page import="org.dspace.eperson.EPerson"%>
<%@ page import="org.dspace.loa.Dimension"%>
<%@ page import="java.util.Vector"%>

<%@ page session="true"%>

<%
	Item item = (Item) request.getAttribute("item");
	String handle = (String) request.getAttribute("handle");
	int layerId =  (Integer) request.getAttribute("layerId");
%>
<dspace:layout style="submission" title="Learning Object Assessment Parametrization" navbar="admin"
	locbar="link" parenttitlekey="jsp.administer" parentlink="/dspace-admin" nocache="true">

	<h1>
		Item name: <small><%=item.getName()%> </small>
	</h1>
	<h1>
		Item ID: <small><%=item.getID()%> </small>
	</h1>
	<h1>
		Item handle: <small><%=handle%> </small>
	</h1>
	<br>
	<form class="form-inline" method="get"
		action="<%=request.getContextPath()%>/tools/LOAssessment/assess-param">

		<input type="hidden" name="item_id" value="<%=item.getID()%>" /> <input type="hidden"
			name="action" value="<%=InitialParamServlet.METRIC_PARAM%>" /> <input type="hidden" name="layerId"
			value="<%=layerId%>" />

		<div class="panel panel-primary">
			<div class="panel-heading">
				<h2 class="panel-title">Please select metrics to assess Learning Object</h2>
			</div>
			<br>
			<%
				List<Dimension> dimensionList = (List<Dimension>) request.getAttribute("LOA.dimensionList");
					for(Dimension dim : dimensionList){
			%>
			<div class="panel-body">
				<div class="well">
					<h4><%=dim.getName()%></h4>
					<%
						List<AssessmentMetric> metricList = (List<AssessmentMetric>) session.getAttribute("LOA.metricList");
							for (AssessmentMetric metric: metricList){
								if(metric.getDimension().getId() == dim.getId()){
					%>
					<div class="checkbox col-lg-3 col-md-4 col-sm-6">
						<label> <input type="checkbox" name="metrics" value="<%=metric.getId()%>"
							<%=metric.isChecked() ? "Checked" : ""%>><%=metric.getCriteria().getName()%>
						</label>
					</div>
					<%
						}}
					%>
					<div class="row"></div>
				</div>

			</div>
			<%
				}
			%>

			<script type="text/javascript">
				//Script to select/unselect all of the checkboxes 
				function selectAllMetrics() {
					var list = document.getElementsByName('metrics');
					for (var i = 0; i < list.length; i++) {
						if (list[i].type == 'checkbox') {
							list[i].checked = true;
						}
					}

				}
				function unselectAllMetrics() {
					var list = document.getElementsByName('metrics');
					for (var i = 0; i < list.length; i++) {
						if (list[i].type == 'checkbox') {
							list[i].checked = false;
						}
					}
				}
			</script>

			<div class="btn-group col-lg-12" role="group" aria-label="...">
				<button type="button" class="btn btn-default" onclick="selectAllMetrics()">Select all</button>
				<button type="button" class="btn btn-default" onclick="unselectAllMetrics()">Unselect
					all</button>
			</div>
			<br>
			<br>
		</div>
		<br>
		<div class="btn-group pull-left" role="group" aria-label="...">
			<input class="btn btn-success btn-lg" type="submit" name="send_param" value="OK" />
		</div>
	</form>

	<div class="btn-group pull-left" style="padding-left: 5px;" role="group" aria-label="...">
		<form method="get" action="<%=request.getContextPath()%>/handle/<%=item.getHandle()%>">
			<input class="btn btn-primary btn-lg" type="submit" name="submit_cancel"
				value="<fmt:message key="jsp.tools.general.cancel"/>" />
		</form>
	</div>

</dspace:layout>