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
  
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"
    prefix="fmt" %>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
	
<%@ page import="org.dspace.app.webui.servlet.admin.InitialParamServlet" %>
<%@ page import="org.dspace.content.Item" %>
<%@ page import="org.dspace.core.ConfigurationManager" %>
<%@ page import="org.dspace.eperson.EPerson" %>
<%@ page import="org.dspace.loa.Dimension" %>
<%@ page import="org.dspace.loa.Metric" %>
<%@ page import="java.util.Vector" %>

<%@ page session="true"%>

<%
    Item item = (Item) request.getAttribute("item");
	String handle = (String) request.getAttribute("handle");
	String layerName = (String) request.getAttribute("layer_name");
%>

<dspace:layout style="submission" titlekey="Learning Object Assessment Parametrization"
               navbar="admin"
               locbar="link"
               parenttitlekey="jsp.administer"
               parentlink="/dspace-admin"
               nocache="true">
               
    <h1>Item: <small><%= item.getName() %> </small></h1>
	<h1>Item ID: <small><%= item.getID() %> </small></h1>
	<h1>Item handle: <small><%= handle %> </small></h1>
               
	<form class="form-inline" method="get" action="<%=request.getContextPath()%>/tools/LOAssessment/assess-param">
	
		<input type="hidden" name="item_id" value="<%=item.getID()%>" />
		<input type="hidden" name="action" value="<%=InitialParamServlet.METRIC_PARAM%>" />
		<input type="hidden" name="layer" value="<%=layerName%>" />

		<div class="panel panel-primary">
			<div class="panel-heading">
				<h2 class="panel-title">Please select metrics to assess
					Learning Object</h2>
			</div>
			<%
 				Vector dimensionList = (Vector) session.getAttribute("LOA.dimensionList");
 				if (dimensionList != null && !dimensionList.isEmpty()) {
			%>
			<%
				for (int i = 0;i < dimensionList.size();i++) {
					Dimension dimension = (Dimension) dimensionList.elementAt(i);
			%>
			<div class="panel-body">
				<div class="well">
					<h4><%=dimension.getName()%></h4>
				<%
				Vector metricList = (Vector)session.getAttribute("LOA.metricList");
				for (int j = 0;j < metricList.size();j++) {
					Metric metric = (Metric) metricList.elementAt(j);
					if(dimension.getName().equals("Content") && metric.getName().equals("Rigor and Relevance")) {
				%>
					<div class="checkbox">
						<label> <input type="checkbox" name="metrics" value="rigrel"><%=metric.getName()%> </label>
						<input type="hidden" name="rigrel_id" value="<%=metric.getID()%>" />
						<input type="hidden" name="rigrel_lay" value="<%=dimension.getLayerID()%>" />
						<input type="hidden" name="rigrel_dim" value="<%=dimension.getID()%>" />
					</div>
					<%
						}
					%>
				<%	
					if(dimension.getName().equals("Contextual") && metric.getName().equals("Relevance")) {
				%>
					<div class="checkbox">
						<label> <input type="checkbox" name="metrics" value="relev"><%=metric.getName()%> </label>
						<input type="hidden" name="relev_id" value="<%=metric.getID()%>" />
						<input type="hidden" name="relev_lay" value="<%=dimension.getLayerID()%>" />
						<input type="hidden" name="relev_dim" value="<%=dimension.getID()%>" />
					</div>
					<%
						}
					%>
				<%	
					if(dimension.getName().equals("Contextual") && metric.getName().equals("Visibility")) {
				%>
					<div class="checkbox">
						<label> <input type="checkbox" name="metrics" value="visib"><%=metric.getName()%> </label>
						<input type="hidden" name="visib_id" value="<%=metric.getID()%>" />
						<input type="hidden" name="visib_lay" value="<%=dimension.getLayerID()%>" />
						<input type="hidden" name="visib_dim" value="<%=dimension.getID()%>" />
					</div>
					<%
						}
					%>
				<%	
					if(dimension.getName().equals("Educational") && metric.getName().equals("Effectiveness")) {
				%>
					<div class="checkbox">
						<label> <input type="checkbox" name="metrics" value="effect"><%=metric.getName()%> </label>
						<input type="hidden" name="effect_id" value="<%=metric.getID()%>" />
						<input type="hidden" name="effect_lay" value="<%=dimension.getLayerID()%>" />
						<input type="hidden" name="effect_dim" value="<%=dimension.getID()%>" />
					</div>
					<%
						}
					%>
				<%	
					if(dimension.getName().equals("Educational") && metric.getName().equals("Motivation")) {
				%>
					<div class="checkbox">
						<label> <input type="checkbox" name="metrics" value="motiva"><%=metric.getName()%> </label>
						<input type="hidden" name="motiva_id" value="<%=metric.getID()%>" />
						<input type="hidden" name="motiva_lay" value="<%=dimension.getLayerID()%>" />
						<input type="hidden" name="motiva_dim" value="<%=dimension.getID()%>" />
					</div>
					<%
						}
					%>
				<%	
					if(dimension.getName().equals("Educational") && metric.getName().equals("Potential Effectiveness")) {
				%>
					<div class="checkbox">
						<label> <input type="checkbox" name="metrics" value="poteffect"><%=metric.getName()%> </label>
						<input type="hidden" name="poteffect_id" value="<%=metric.getID()%>" />
						<input type="hidden" name="poteffect_lay" value="<%=dimension.getLayerID()%>" />
						<input type="hidden" name="poteffect_dim" value="<%=dimension.getID()%>" />
					</div>
					<%
						}
					%>
				<%	
					if(dimension.getName().equals("Esthetic") && metric.getName().equals("Visual Design")) {
				%>
					<div class="checkbox">
						<label> <input type="checkbox" name="metrics" value="visdes"><%=metric.getName()%> </label>
						<input type="hidden" name="visdes_id" value="<%=metric.getID()%>" />
						<input type="hidden" name="visdes_lay" value="<%=dimension.getLayerID()%>" />
						<input type="hidden" name="visdes_dim" value="<%=dimension.getID()%>" />
					</div>
					<%
						}
					%>
				<%	
					if(dimension.getName().equals("Functional") && metric.getName().equals("Accessibility")) {
				%>
					<div class="checkbox">
						<label> <input type="checkbox" name="metrics" value="access"><%=metric.getName()%> </label>
						<input type="hidden" name="access_id" value="<%=metric.getID()%>" />
						<input type="hidden" name="access_lay" value="<%=dimension.getLayerID()%>" />
						<input type="hidden" name="access_dim" value="<%=dimension.getID()%>" />
					</div>
					<%
						}
					%>
				<%	
					if(dimension.getName().equals("Functional") && metric.getName().equals("Availability")) {
				%>
					<div class="checkbox">
						<label> <input type="checkbox" name="metrics" value="avail"><%=metric.getName()%> </label>
						<input type="hidden" name="avail_id" value="<%=metric.getID()%>" />
						<input type="hidden" name="avail_lay" value="<%=dimension.getLayerID()%>" />
						<input type="hidden" name="avail_dim" value="<%=dimension.getID()%>" />
					</div>
					<%
						}
					%>
				<%	
					if(dimension.getName().equals("Functional") && metric.getName().equals("Ease to use")) {
				%>
					<div class="checkbox">
						<label> <input type="checkbox" name="metrics" value="easeuse"><%=metric.getName()%> </label>
						<input type="hidden" name="easeuse_id" value="<%=metric.getID()%>" />
						<input type="hidden" name="easeuse_lay" value="<%=dimension.getLayerID()%>" />
						<input type="hidden" name="easeuse_dim" value="<%=dimension.getID()%>" />
					</div>
					<%
						}
					%>
				<%	
					if(dimension.getName().equals("Functional") && metric.getName().equals("Reusability")) {
				%>
					<div class="checkbox">
						<label> <input type="checkbox" name="metrics" value="usable"><%=metric.getName()%> </label>
						<input type="hidden" name="usable_id" value="<%=metric.getID()%>" />
						<input type="hidden" name="usable_lay" value="<%=dimension.getLayerID()%>" />
						<input type="hidden" name="usable_dim" value="<%=dimension.getID()%>" />
					</div>
					<%
						}
					%>
				<%	
					if(dimension.getName().equals("Metadata") && metric.getName().equals("Accuracy")) {
				%>
					<div class="checkbox">
						<label> <input type="checkbox" name="metrics" value="accuracy"><%=metric.getName()%> </label>
						<input type="hidden" name="accuracy_id" value="<%=metric.getID()%>" />
						<input type="hidden" name="accuracy_lay" value="<%=dimension.getLayerID()%>" />
						<input type="hidden" name="accuracy_dim" value="<%=dimension.getID()%>" />
					</div>
					<%
						}
					%>
				<%	
					if(dimension.getName().equals("Metadata") && metric.getName().equals("Coherence")) {
				%>
					<div class="checkbox">
						<label> <input type="checkbox" name="metrics" value="coherence"><%=metric.getName()%> </label>
						<input type="hidden" name="coherence_id" value="<%=metric.getID()%>" />
						<input type="hidden" name="coherence_lay" value="<%=dimension.getLayerID()%>" />
						<input type="hidden" name="coherence_dim" value="<%=dimension.getID()%>" />
					</div>
					<%
						}
					%>
				<%	
					if(dimension.getName().equals("Metadata") && metric.getName().equals("Completeness")) {
				%>
					<div class="checkbox">
						<label> <input type="checkbox" name="metrics" value="complete"><%=metric.getName()%> </label>
						<input type="hidden" name="complete_id" value="<%=metric.getID()%>" />
						<input type="hidden" name="complete_lay" value="<%=dimension.getLayerID()%>" />
						<input type="hidden" name="complete_dim" value="<%=dimension.getID()%>" />
					</div>
					<%
						}
					%>
				<%	
					if(dimension.getName().equals("Metadata") && metric.getName().equals("Consistency")) {
				%>
					<div class="checkbox">
						<label> <input type="checkbox" name="metrics" value="consist"><%=metric.getName()%> </label>
						<input type="hidden" name="consist_id" value="<%=metric.getID()%>" />
						<input type="hidden" name="consist_lay" value="<%=dimension.getLayerID()%>" />
						<input type="hidden" name="consist_dim" value="<%=dimension.getID()%>" />
					</div>
					<%
						}
					%>
				<%
					}
				%>
				</div>
			</div>
			<%
				}
			%>
		<%
			}
		%>
		</div>

		<div class="btn-group" role="group" aria-label="...">
			<input class="btn btn-success btn-lg" type="submit" 
				name="send_param" value="OK" />
		</div>
	</form>

	<div class="btn-group" role="group" aria-label="...">
		<form method="get"
			action="<%=request.getContextPath()%>/handle/<%=item.getHandle()%>">
			<input class="btn btn-primary btn-lg" type="submit"
				name="submit_cancel"
				value="<fmt:message key="jsp.tools.general.cancel"/>" />
		</form>
	</div>
	
</dspace:layout>