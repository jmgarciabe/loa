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
	String layer = (String) request.getAttribute("layer");
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
		<span class="label label-default"> Please set the weighting in each dimension in <%=layer%>
			layer.
		</span>
	</h3>

	<div class="alert alert-warning" role="alert">Be aware that for each layer assigned weights
		must total 100%!</div>

	<form method="post" action="<%=request.getContextPath()%>/tools/LOAssessment/assess-param">

		<input type="hidden" name="item_id" value="<%=item.getID()%>" /> <input type="hidden"
			name="layer" value="<%=layer%>" />

		<%
			Vector checkedDimensions = (Vector) session
						.getAttribute("LOA.ckDimensionList");
				if (checkedDimensions != null && !checkedDimensions.isEmpty()) {
					if (layer != null) {
		%>

		<div class="panel panel-info">
			<div class="panel-heading">
				<h2 class="panel-title"><%=layer + " Layer"%></h2>
			</div>
			<div class="panel-body">
				<%
					for (int index = 0; index < checkedDimensions.size(); index++) {
									String dimensionName = (String) checkedDimensions
											.elementAt(index);
				%>
				<div class="form-group">
					<label for="<%=dimensionName%>"><%=dimensionName%></label>
					<div class="input-group">
						<input type="text" class="form-control" aria-describedby="percentage"
							name="<%=dimensionName%>" id="<%=dimensionName%>">
						<span class="input-group-addon" id="percentage">%</span>
					</div>
				</div>
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
		<!-- Modal -->
		<div id="modalInitParam" class="modal fade" role="dialog">
			<div class="modal-dialog">

				<!-- Modal content-->
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						<h4 class="modal-title">Confirm assessment parametrization</h4>
					</div>
					<div class="modal-body">
						<div>
							<strong>Parameterized layer successfully!</strong> Do you want to configure a further layer
							to this assessment?
						</div>
					</div>
					<div class="modal-footer">
						<input class="btn btn-success" type="submit" name="submit_yes"
							value="Yes" /> <input class="btn btn-primary" type="submit"
							name="submit_no" value="No" />
					</div>
				</div>
			</div>
		</div>

		<div class="btn-group pull-left" role="group" aria-label="...">

			<!-- Trigger the modal with a button -->
			<button type="button" class="btn btn-success btn-lg" data-toggle="modal"
				data-target="#modalInitParam">Confirm</button>


		</div>
	</form>

	<div class="btn-group pull-left" style="padding-left: 5px;" role="group" aria-label="...">
		<form method="get" action="<%=request.getContextPath()%>/handle/<%=item.getHandle()%>">
			<input class="btn btn-primary btn-lg" type="submit" name="submit_cancel"
				value="<fmt:message key="jsp.tools.general.cancel"/>" />
		</form>
	</div>
</dspace:layout>