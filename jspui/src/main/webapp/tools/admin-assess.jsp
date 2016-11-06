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

<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>
<%@ page import="org.dspace.app.webui.servlet.admin.EditCommunitiesServlet" %>
<%@ page import="org.dspace.app.webui.util.AssessResult" %>
<%@ page import="java.util.Vector" %>
<%@ page import="org.dspace.content.Item" %>
<%@ page import="org.dspace.content.Metadatum" %>
<%@ page import="org.dspace.core.ConfigurationManager" %>

<%@ page session="true"%>

<%
	Item item = (Item) request.getAttribute("item");
	String enaRstBtn = (String) request.getAttribute("enaRstBtn");
    int itemID = (item != null ? item.getID() : -1);
    String title = "Unknown Item";
    if (item != null)
    {
        Metadatum[] dcvs = item.getMetadataByMetadataString("dc.title");
        if (dcvs != null && dcvs.length > 0)
        {
            title = dcvs[0].value;
        }
    }
%>

<dspace:layout style="submission" titlekey="jsp.dspace-admin.curate.item.title"
               navbar="admin"
               locbar="link"
               parenttitlekey="jsp.administer"
               parentlink="/dspace-admin">

<%@ include file="/tools/admassess-message.jsp" %>

    <h1>Assess Item: <small><%= title %> </small></h1>
    
	<div class="row container">
    <form method="get" action="<%=request.getContextPath()%>/tools/LOAssessment/admin-assess">
    
    	<input type="hidden" name="item_id" value="<%=itemID%>" />

			<div class="input-group">
				<label class="input-group-addon">Available assessments:</label> <select
					class="form-control" name="admin_assess" id="admin_assess">
					<%
						Vector adminAvailAssess = (Vector) session
									.getAttribute("LOA.adminAvailAssess");
							if (adminAvailAssess != null && !adminAvailAssess.isEmpty()) {
					%>
							<%
								for (int index = 0; index < adminAvailAssess.size(); index++) {
									String adminMetrics = (String) adminAvailAssess.elementAt(index);			
							%>
									<%=adminMetrics%>
							<%
								}
							%>
						<%
							}
						%>
				</select> <br />
			</div>

			<div class="btn-group" role="group" aria-label="...">
				<input class="btn btn-success btn-lg" type="submit"
					name="submit_item_assess"
					value="<fmt:message key="jsp.tools.curate.perform.button"/>" />
			</div>
		</form>
      </div>

	<div class="btn-group" role="group" aria-label="...">
		<form method="get"
			action="<%=request.getContextPath()%>/handle/<%=item.getHandle()%>">
			<input class="btn btn-primary btn-lg" type="submit"
				name="submit_cancel"
				value="<fmt:message key="jsp.tools.general.cancel"/>" />
		</form>
	</div>
	
	<%
		if (enaRstBtn == "Y")
		{
	%>
			<div class="btn-group" role="group" aria-label="...">
				<form method="post"
					action="<%=request.getContextPath()%>/tools/LOAssessment/admin-assess">
					<input type="hidden" name="item_id" value="<%=itemID%>" />
					<input class="btn btn-info btn-lg" type="submit"
					name="show_results" value="Show Results">
				</form>
			</div>
	<%		
		}else{
	%>
			<div class="btn-group" role="group" aria-label="...">
				<form method="post"
					action="<%=request.getContextPath()%>/tools/LOAssessment/admin-assess">
					<input class="btn btn-info btn-lg disabled" type="submit"
					name="show_results" value="Show Results">
				</form>
			</div>
	<%		
		}
	%>
</dspace:layout>