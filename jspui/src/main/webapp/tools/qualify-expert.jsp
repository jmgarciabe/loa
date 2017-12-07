<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  -
  - 
  --%>

<%@page import="org.dspace.loa.DimensionWeighting"%>
<%@page import="java.util.List"%>
<%@ page contentType="text/html;charset=UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace"%>

<%@ page import="org.dspace.app.webui.servlet.ExpertAssessServlet"%>
<%@ page import="org.dspace.content.Item"%>
<%@ page import="org.dspace.core.ConfigurationManager"%>
<%@ page import="org.dspace.eperson.EPerson"%>

<%@ page session="true"%>

<%
	Item item = (Item) request.getAttribute("item");
%>
<script type="text/javascript">
	function validarNumeros() {
		var isValid = true;
		var nodes = document.querySelectorAll(".dim-value");
		for (var i = 0; i < nodes.length; i++) {
			if( isNaN(nodes[i].valueAsNumber) || nodes[i].valueAsNumber > 5 || nodes[i].valueAsNumber < 1){
				isValid = false;
				break;
			}
		}
		if (isValid) {
			document.getElementById("expert-dim-weighting").submit();
		} else {
			document.getElementById("error-message").innerHTML = "Invalid input, be aware that valid values must rate between 1 and 5";
		}

	}
</script>
<dspace:layout style="submission" title="Learning Object Expert Survey Parameterization" locbar="link"
	nocache="true">

	<h1>
		Item: <small><%=item.getName()%> </small>
	</h1>
	<h1>
		Item ID: <small><%=item.getID()%> </small>
	</h1>
	<h1>
		Item handle: <small><%=item.getHandle()%> </small>
	</h1>

	<br>
	<h3>
		<span class="label label-default"> Please set the value (from 1 to 5) in each dimension
			according to your experience. </span>
	</h3>

	<div class="alert alert-warning" role="alert">1 means a low level of experience, while 5
		means that you are an expert in that field of knowledge</div>

	<form method="post" action="<%=request.getContextPath()%>/tools/LOAssessment/expassess-param" id="expert-dim-weighting">

		<input type="hidden" name="item_id" value="<%=item.getID()%>" /> <input type="hidden"
			name="action" value="<%=ExpertAssessServlet.DIM_PARAM%>" />

		<%
			List<DimensionWeighting> dimensions = (List<DimensionWeighting>) session.getAttribute("LOA.dimensionWList");
		%>

		<div class="panel panel-info">
			<div class="panel-heading">
				<h2 class="panel-title">Expert Layer</h2>
			</div>
			<div class="panel-body">
				<div class="col-md-6 col-lg-4">
					<%
						for (DimensionWeighting dim : dimensions) {
					%>
					<div class="form-group">
						<label for="exp_<%=dim.getId() %>">Level of <%=dim.getDimension().getName()%> experience
						</label> <input type="number" class="form-control dim-value" name="<%=dim.getId()%>" required>
					</div>
					<%
						}
					%>
				</div>
			</div>
		</div>
		
		<p id="error-message" style="color: red;"></p>
		<div class="pull-left">
			<button type="button" class="btn btn-success btn-lg" onclick="validarNumeros()">Confirm</button>
		</div>
	</form>

	<div class="pull-left" style="padding-left: 5px;">
		<form method="get" action="<%=request.getContextPath()%>/handle/<%=item.getHandle()%>">
			<input class="btn btn-primary btn-lg" type="submit" name="submit_cancel"
				value="<fmt:message key="jsp.tools.general.cancel"/>" />
		</form>
	</div>
</dspace:layout>