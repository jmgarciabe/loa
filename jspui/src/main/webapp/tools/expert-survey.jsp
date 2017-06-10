<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  --%>

<%@ page contentType="text/html;charset=UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace"%>

<%@ page import="org.dspace.app.webui.servlet.ExpertAssessServlet"%>
<%@ page import="org.dspace.content.Item"%>
<%@ page import="org.dspace.core.ConfigurationManager"%>
<%@ page import="org.dspace.eperson.EPerson"%>
<%@ page import="java.util.Vector"%>

<%@ page session="true"%>

<%
	Item item = (Item) request.getAttribute("item");
%>

<dspace:layout style="submission" title="Learning Object Expert Survey" locbar="link" nocache="true">

	<h1>
		Item: <small><%=item.getName()%> </small>
	</h1>
	<h1>
		Item handle: <small><%=item.getHandle()%> </small>
	</h1>

	<form class="form-inline" method="post"
		action="<%=request.getContextPath()%>/tools/LOAssessment/expassess-param">

		<input type="hidden" name="item_id" value="<%=item.getID()%>" /> <input type="hidden"
			name="action" value="<%=ExpertAssessServlet.EXP_SURVEY%>" />

		<div class="panel panel-primary">
			<div class="panel-heading">
				<h2 class="panel-title">
					<span class="fa fa-question-circle"></span> Expert Survey
				</h2>
			</div>
			<%
				Vector metrics = (Vector) session
							.getAttribute("LOA.expMetrics");
					if (metrics != null && !metrics.isEmpty()) {
						for (int index = 0; index < metrics.size(); index++) {
							String metricName = (String) metrics.elementAt(index);
			%>
			<div class="panel-body">
				<%
					if (metricName.equals("Accessibility")) {
				%>
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">
							Functional :
							<%=metricName%>
						</h3>
					</div>
					<div class="panel-body">
						<p>How likely is it that learning object does not require additional software or devices
							when accessing it?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="acs1" value="1">Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="acs1" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="acs1" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="acs1" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="acs1" value="5">Very
							High
						</label>
						<br><br>
						<p>To what extent the resource is working properly and is easy for the user to visualize
							it from different platforms?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="acs2" value="1">Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="acs2" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="acs2" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="acs2" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="acs2" value="5">Very
							High
						</label><br>
					</div>
				</div>
				<%
					}
								if (metricName.equals("Accuracy")) {
				%>
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">
							Metadata :
							<%=metricName%>
						</h3>
					</div>
					<div class="panel-body">
						<p>To what extent the metadata actually describes content found?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="acc1" value="1">Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="acc1" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="acc1" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="acc1" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="acc1" value="5">Very
							High
						</label>
						<br>
					</div>
				</div>
				<%
					}
								if (metricName.equals("Completeness")) {
				%>
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">
							Metadata :
							<%=metricName%>
						</h3>
					</div>
					<div class="panel-body">
						<p>The metadata describe completely the learning object?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="com1" value="1">Yes
						</label> <label class="radio-inline"> <input type="radio" name="com1" value="0">No
						</label>
						<br>
					</div>
				</div>
				<%
					}
								if (metricName.equals("Ease to use")) {
				%>
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">
							Functional :
							<%=metricName%>
						</h3>
					</div>
					<div class="panel-body">
						<p>To what level of clarity has user for using the learning object?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="eou1" value="1">Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="eou1" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="eou1" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="eou1" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="eou1" value="5">Very
							High
						</label>
						<br><br>
						<p>How would you rate the relation between needs and help provided?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="eou2" value="1">Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="eou2" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="eou2" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="eou2" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="eou2" value="5">Very
							High
						</label>
						<br>
					</div>
				</div>
				<%
					}
								if (metricName.equals("Potential Effectiveness")) {
				%>
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">
							Educational :
							<%=metricName%>
						</h3>
					</div>
					<div class="panel-body">
						<p>To what level is possible to identify educational objectives that is intended to reach
							with the learning object?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="poe1" value="1">Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="poe1" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="poe1" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="poe1" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="poe1" value="5">Very
							High
						</label>
						<br><br>
						<p>To what extent object's structure and content are supporting topic's learning?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="poe2" value="1">Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="poe2" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="poe2" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="poe2" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="poe2" value="5">Very
							High
						</label><br>
					</div>
				</div>
				<%
					}
								if (metricName.equals("Reusability")) {
				%>
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">
							Functional :
							<%=metricName%>
						</h3>
					</div>
					<div class="panel-body">
						<p>How likely is it that learning object can be used in various educational contexts?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="reu1" value="1">Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="reu1" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="reu1" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="reu1" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="reu1" value="5">Very
							High
						</label><br><br>
						<p>To what level learning object is self-contained and does not require any dependencies?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="reu2" value="1">Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="reu2" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="reu2" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="reu2" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="reu2" value="5">Very
							High
						</label><br>
					</div>
				</div>
				<%
					}
								if (metricName.equals("Rigor and Relevance")) {
				%>
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">
							Content :
							<%=metricName%>
						</h3>
					</div>
					<div class="panel-body">
						<p>To what level the content that is shown is clear, coherent, complete, impartial and
							accomplishes with author's rights?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="rar1" value="1">Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="rar1" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="rar1" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="rar1" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="rar1" value="5">Very
							High
						</label><br><br>
						<p>To what level the content that is shown have ortographic and gramatical errors?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="rar2" value="1">Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="rar2" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="rar2" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="rar2" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="rar2" value="5">Very
							High
						</label><br>
					</div>
				</div>
				<%
					}
								if (metricName.equals("Visual Design")) {
				%>
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">
							Esthetic :
							<%=metricName%>
						</h3>
					</div>
					<div class="panel-body">
						<p>Distribution and size of graphical elements, visual hierarchy, letter design and
							contrast of colors is appropiate?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="vid1" value="1">Yes
						</label> <label class="radio-inline"> <input type="radio" name="vid1" value="0">No
						</label><br><br>
						<p>Election of texts, images, sounds or other multimedia elements contributes to learning
							objectives?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="vid2" value="1">Yes
						</label> <label class="radio-inline"> <input type="radio" name="vid2" value="0">No
						</label><br>
					</div>
				</div>
				<%
					}
				%>
			</div>
			<%
				}
					}
			%>
		</div>

		<div class="pull-left">
			<input class="btn btn-success btn-lg" type="submit" name="submit_survey" value="Submit" />
		</div>
	</form>

	<form method="get" action="<%=request.getContextPath()%>/handle/<%=item.getHandle()%>">
		<div class="pull-left" style="padding-left: 5px;">
			<input class="btn btn-primary btn-lg" type="submit" name="submit_cancel"
				value="<fmt:message key="jsp.tools.general.cancel"/>" />
		</div>
	</form>

</dspace:layout>