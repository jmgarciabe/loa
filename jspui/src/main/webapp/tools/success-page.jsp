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
	
<%@ page import="org.dspace.content.Item" %>
<%@ page import="org.dspace.core.ConfigurationManager" %>
<%@ page import="org.dspace.eperson.EPerson" %>

<%@ page session="true"%>

<%
    Item item = (Item) request.getAttribute("item");
	
	// Is anyone logged in?
    EPerson user = (EPerson) request.getAttribute("dspace.current.user");
%>

<dspace:layout style="submission" title="Learning Object Assessment parameterization"
               locbar="link"
               nocache="true">

	<div class="alert alert-success">
		<strong>Operation performed successfully! Thank you for your time.</strong>

		<h1>
			Item: <small><%=item.getName()%> </small>
		</h1>

		<h1>
			Item ID: <small><%=item.getID()%> </small>
		</h1>

		<h1>
			Item handle: <small><%=item.getHandle()%> </small>
		</h1>
	</div>

	<div class="btn-group" role="group" aria-label="...">
		<form method="get"
			action="<%=request.getContextPath()%>/handle/<%=item.getHandle()%>">
			<input class="btn btn-success btn-lg" type="submit"
				name="submit_ok" value="OK" />
		</form>
	</div>
	
</dspace:layout>