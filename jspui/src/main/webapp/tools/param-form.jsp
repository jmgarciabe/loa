<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  --%>

<%@page import="java.util.List"%>
<%@page import="org.dspace.loa.Layer"%>
<%@ page contentType="text/html;charset=UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace"%>

<%@ page import="org.dspace.app.webui.servlet.admin.InitialParamServlet"%>
<%@ page import="org.dspace.content.Item"%>
<%@ page import="org.dspace.core.ConfigurationManager"%>
<%@ page import="org.dspace.eperson.EPerson"%>

<%
	Item item = (Item) request.getAttribute("item");

	// Is anyone logged in?
	EPerson user = (EPerson) request.getAttribute("dspace.current.user");
%>

<dspace:layout style="submission" title="Learning Object Assessment Parametrization" navbar="admin"
	locbar="link" parenttitlekey="jsp.administer" parentlink="/dspace-admin" nocache="true">

	<h1 class="col-md-12">
		Item name: <small><%=item.getName()%> </small>
	</h1>

	<h1 class="col-md-12">
		Item ID: <small><%=item.getID()%> </small>
	</h1>
	<br>
	<br>
	<div class="col-md-5 col-lg-4">
		<form method="get" action="<%=request.getContextPath()%>/tools/LOAssessment/assess-param">

			<input type="hidden" name="item_id" value="<%=item.getID()%>" /> <input type="hidden"
				name="action" value="<%=InitialParamServlet.LAYER_PARAM%>" />

			<div class="form-group">
				<label for="selectLayer" class="control-label">Please select the layer to set parameters</label>
				<select class="form-control" id="selectLayer" name="layerId">
					<%
						List<Layer> layerList = (List<Layer>) request.getAttribute("LOA.layerList");
							if (layerList != null) {
								for (Layer layer :  layerList) {
					%>
					<option value="<%=layer.getId()%>"><%=layer.getName()%></option>

					<%
						}
							}
					%>
				</select>

			</div>
			<br>
			<div class="pull-left">
				<input class="btn btn-success btn-lg" type="submit" name="send_assess" value="Send" />
			</div>
		</form>

		<div class="pull-left" style="padding-left: 5px;">
			<form method="get" action="<%=request.getContextPath()%>/tools/LOAssessment">
				<input type="hidden" name="item_id" value="<%=item.getID()%>" /> <input
					class="btn btn-primary btn-lg" type="submit" name="submit_cancel" value="Back" />
			</form>
		</div>
	</div>
</dspace:layout>