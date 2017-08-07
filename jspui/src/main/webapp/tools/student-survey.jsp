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

<dspace:layout style="submission" title="Learning Object Assessment parameterization" locbar="link"
	nocache="true">

	<h1>
		Item: <small><%=item.getName()%> </small>
	</h1>
	<h1>
		Item handle: <small><%=item.getHandle()%> </small>
	</h1>

	<form class="form-inline" method="post"
		action="<%=request.getContextPath()%>/tools/LOAssessment/stdassess">

		<input type="hidden" name="item_id" value="<%=item.getID()%>" />

		<div class="panel panel-primary">
			<div class="panel-heading">
				<h2 class="panel-title">
					<span class="fa fa-question-circle"></span> Student Survey
				</h2>
			</div>
			<%
				Vector metrics = (Vector) session
							.getAttribute("LOA.paraMetrics");
					if (metrics != null && !metrics.isEmpty()) {
						for (int index = 0; index < metrics.size(); index++) {
							String metricName = (String) metrics.elementAt(index);
			%>
			<div class="panel-body">
				<%
					if (metricName.equals("Availability")) {
				%>
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">
							Functional :
							<%=metricName%>
						</h3>
					</div>
					<div class="panel-body">
						<p>Can you access to the learning object content?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="ava1" value="1" required>Yes
						</label> <label class="radio-inline"> <input type="radio" name="ava1" value="0">No
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
						<label class="radio-inline"> <input type="radio" name="acc1" value="1" required>Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="acc1" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="acc1" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="acc1" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="acc1" value="5">Very
							High
						</label><br>
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
						<p>How would you rate easiness and clarity when using the learning object?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="eou1" value="1" required>Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="eou1" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="eou1" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="eou1" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="eou1" value="5">Very
							High
						</label><br>
					</div>
				</div>
				<%
					}
								if (metricName.equals("Effectiveness")) {
				%>
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">
							Educational :
							<%=metricName%>
						</h3>
					</div>
					<div class="panel-body">
						<p>To what extent content found lets you learn on the subject?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="eff1" value="1" required>Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="eff1" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="eff1" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="eff1" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="eff1" value="5">Very
							High
						</label><br>
					</div>
				</div>
				<%
					}
								if (metricName.equals("Motivation")) {
				%>
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">
							Educational :
							<%=metricName%>
						</h3>
					</div>
					<div class="panel-body">
						<p>To what extent the learning object motivates you to keep searching on the subject?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="mot1" value="1" required>Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="mot1" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="mot1" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="mot1" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="mot1" value="5">Very
							High
						</label><br>
					</div>
				</div>
				<%
					}
								if (metricName.equals("Relevance")) {
				%>
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">
							Contextual :
							<%=metricName%>
						</h3>
					</div>
					<div class="panel-body">
						<p>To what level this learning object was important for you and was related with your
							expectations?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="rel1" value="1" required>Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="rel1" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="rel1" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="rel1" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="rel1" value="5">Very
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
						<p>To what level the size, color and distribution of elements of learning object are
							suitable for its purposes?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="vid1" value="1" required>Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="vid1" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="vid1" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="vid1" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="vid1" value="5">Very
							High
						</label>
						<br><br>
						<p>To what level the texts, images, sounds and other multimedia elements of learning
							object contribute on learning of the subject?</p>
						<!-- Radio group options -->
						<label class="radio-inline"> <input type="radio" name="vid2" value="1" required>Very
							Low
						</label> <label class="radio-inline"> <input type="radio" name="vid2" value="2">Low
						</label> <label class="radio-inline"> <input type="radio" name="vid2" value="3">Average
						</label> <label class="radio-inline"> <input type="radio" name="vid2" value="4">High
						</label> <label class="radio-inline"> <input type="radio" name="vid2" value="5">Very
							High
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