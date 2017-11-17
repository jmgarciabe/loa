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

<%@page import="org.dspace.loa.Dimension"%>
<%@page import="java.util.List"%>
<%@page import="org.dspace.loa.Layer"%>
<%@ page contentType="text/html;charset=UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace"%>

<%@ page import="org.dspace.app.webui.servlet.admin.InitialParamServlet"%>
<%@ page import="org.dspace.content.Item"%>
<%@ page import="org.dspace.core.ConfigurationManager"%>
<%@ page import="org.dspace.eperson.EPerson"%>
<%@ page import="java.util.Vector"%>

<%@ page session="true"%>

<%
	Item item = (Item) request.getAttribute("item");
	String handle = (String) request.getAttribute("handle");
	Layer layer = (Layer) request.getAttribute("layer");
%>

<dspace:layout style="submission" title="Learning Object Assessment Parametrization" navbar="admin"
	locbar="link" parenttitlekey="jsp.administer" parentlink="/dspace-admin" nocache="true">

	<h1>
		Item: <small><%=item.getName()%> </small>
	</h1>
	<h1>
		Item ID: <small><%=item.getID()%> </small>
	</h1>
	<h1>
		Item handle: <small><%=handle%> </small>
	</h1>
	<br>
	<h3>
		<span class="label label-default"> Please set the weighting in each dimension in <%=layer.getName()%>
			layer.
		</span>
	</h3>

	<div class="alert alert-warning" role="alert">Be aware that for each layer assigned weights
		must total 100%!</div>

	<form method="post" action="<%=request.getContextPath()%>/tools/LOAssessment/assess-param"
		id="param-values-form">

		<input type="hidden" name="item_id" value="<%=item.getID()%>" /> <input type="hidden"
			name="layerId" value="<%=layer.getId()%>" />
		<div class="panel panel-info">
			<div class="panel-heading">
				<h2 class="panel-title"><%=layer.getName() + " Layer"%></h2>
			</div>
			<div class="panel-body">
				<%
				List<Dimension> checkedDimensions = (List<Dimension>) request.getAttribute("LOA.ckDimensionList");			
					for (Dimension dim : checkedDimensions) {
									
				%>
				<div class="form-group">
					<label for="<%=dim.getName()%>"><%=dim.getName()%></label>
					<div class="input-group">
						<input type="number" class="form-control dim-value" aria-describedby="percentage"
							name="<%=dim.getName()%>" id="<%=dim.getName()%>" required> <span
							class="input-group-addon" id="percentage">%</span>
					</div>
				</div>
				<%
					}
				%>
			</div>
		</div>
		
		<script type="text/javascript">
			function validar() {
				var sum = 0.0;
				var nodes = document.querySelectorAll(".dim-value");
				for (var i = 0; i < nodes.length; i++) {
					sum += nodes[i].valueAsNumber;
				}
				if (sum == 100) {
					document.getElementById("param-values-form").submit();
				} else {
					document.getElementById("error-message").innerHTML = "Invalid input, be aware it must sum up 100!";
				}

			}
		</script>
		<p id="error-message" style="color: red;"></p>
		<div class="pull-left">
			<button type="button" class="btn btn-success btn-lg" onclick="validar()">Confirm</button>
		</div>
	</form>

	<div class="pull-left" style="padding-left: 5px;">
		<form method="get" action="<%=request.getContextPath()%>/handle/<%=item.getHandle()%>">
			<input class="btn btn-primary btn-lg" type="submit" name="submit_cancel" value="Cancel" />
		</form>
	</div>
</dspace:layout>