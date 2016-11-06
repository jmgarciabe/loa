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
  
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"
    prefix="fmt" %>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
	
<%@ page import="org.dspace.app.webui.servlet.ExpertAssessServlet" %>
<%@ page import="org.dspace.content.Item" %>
<%@ page import="org.dspace.core.ConfigurationManager" %>
<%@ page import="org.dspace.eperson.EPerson" %>
<%@ page import="java.util.Vector" %>

<%@ page session="true"%>

<%
	Item item = (Item) request.getAttribute("item");
%>

<dspace:layout style="submission" titlekey="Learning Object Assessment parameterization"
               locbar="link"
               nocache="true">
	
	<h1>Item: <small><%=item.getName()%> </small></h1>
	<h1>Item ID: <small><%=item.getID()%> </small></h1>
	<h1>Item handle: <small><%=item.getHandle()%> </small></h1>
	
	<h3><span class="label label-primary">
	Please set the value (from 1 to 5) in each dimension according to your experience. 
	</span></h3>
	
	<div class="alert alert-warning" role="alert">
	1 means a low level of experience, while 5 means that you are an expert in that field of knowledge  
	</div>
	
	<form class="form-inline" method="post" action="<%=request.getContextPath()%>/tools/LOAssessment/expassess-param">
	
		<input type="hidden" name="item_id" value="<%=item.getID()%>" />
		<input type="hidden" name="action" value="<%=ExpertAssessServlet.DIM_PARAM%>" /> 
		
		<%
 			Vector paramDimensions = (Vector) session.getAttribute("LOA.paramDimensions");
 			if (paramDimensions != null && !paramDimensions.isEmpty()) {
		%>
		
		<div class="panel panel-info">
			<div class="panel-heading">
				<h2 class="panel-title">Expert Layer</h2>
			</div>				
			<div class="panel-body">
			<%
				for (int index = 0;index < paramDimensions.size();index++) {
					String dimensionName = (String) paramDimensions.elementAt(index);
			%>
				<div class="input-group">
					<input type="text" class="form-control"
						aria-describedby="percentage" name="exp_<%=dimensionName%>">
					Level of <%=dimensionName%> experience 
				</div>
			<%
				}
			%>
			</div>
		</div>
		<%
			}
		%>

		<div class="btn-group" role="group" aria-label="...">

			<!-- Trigger the modal with a button -->
			<button type="button" class="btn btn-success btn-lg" data-toggle="modal"
				data-target="#modalInitParam">Confirm</button>

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
							<div class="alert alert-success">
								<strong>Parameterized weights successfully!</strong> Please continue 
								with your assessment...
							</div>
						</div>
						<div class="modal-footer">
							<input class="btn btn-primary pull-right col-md-12" type="submit"
								name="submit_ok" value="OK" /> 
						</div>
					</div>
				</div>
			</div>
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