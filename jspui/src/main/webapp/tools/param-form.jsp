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
	
<%@ page import="org.dspace.app.webui.servlet.admin.InitialParamServlet" %>
<%@ page import="java.util.Vector" %>
<%@ page import="org.dspace.content.Item" %>
<%@ page import="org.dspace.core.ConfigurationManager" %>
<%@ page import="org.dspace.eperson.EPerson" %>

<%
    Item item = (Item) request.getAttribute("item");
	
	// Is anyone logged in?
    EPerson user = (EPerson) request.getAttribute("dspace.current.user");
%>

<dspace:layout style="submission" titlekey="Learning Object Assessment Parametrization"
               navbar="admin"
               locbar="link"
               parenttitlekey="jsp.administer"
               parentlink="/dspace-admin"
               nocache="true">
	
	<h1>Item: <small><%= item.getName() %> </small></h1>
	
	<h1>Item ID: <small><%= item.getID() %> </small></h1>

	<form class="form-inline" method="get" action="<%= request.getContextPath() %>/tools/LOAssessment/assess-param">
	
		<input type="hidden" name="item_id" value="<%=item.getID()%>" />
		<input type="hidden" name="action" value="<%=InitialParamServlet.LAYER_PARAM%>" />
		
		<div class="form-group">
			<label for="selectLayer" class="control-label">Please select the
				layer to parameterize:</label> 
			<select class="form-control" id="selectLayer" name="layer_name">
				<%
					Vector adminAssessOpt = (Vector) session
								.getAttribute("LOA.adminAssessOpt");
						if (adminAssessOpt != null && !adminAssessOpt.isEmpty()) {
				%>
				<%
						for (int index = 0; index < adminAssessOpt.size(); index++) {
									String layers = (String) adminAssessOpt
											.elementAt(index);
				%>
				<%=layers%>
				<%
						}
				%>
				<%
					}
				%>
			</select>
			<div class="btn-group" role="group" aria-label="...">
				<input class="btn btn-success btn-lg" type="submit"
					name="send_assess" value="Send" />
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