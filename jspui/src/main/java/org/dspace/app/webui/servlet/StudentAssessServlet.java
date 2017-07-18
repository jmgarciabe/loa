/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.webui.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dspace.app.webui.util.JSPManager;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.handle.HandleManager;
import org.dspace.loa.AssessParam;
import org.dspace.loa.Metric;

/**
 * Servlet for perform the assessment logic of each of the student layer metrics
 * 
 * @author Andres Salazar
 * @version $Revision$
 */
public class StudentAssessServlet extends DSpaceServlet {

	protected void doDSGet(Context context, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, SQLException, AuthorizeException {

	}

	protected void doDSPost(Context context, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, SQLException, AuthorizeException {
		HttpSession session = request.getSession(false);

		if (session == null) {
			JSPManager.showInternalError(request, response);
		}

		int itemID = UIUtil.getIntParameter(request, "item_id");

		Item item = Item.find(context, itemID);

		String handle = HandleManager.findHandle(context, item);
		
		// Cargamos solo los asses param de la capa
		Vector<AssessParam> assessParamList = AssessParam.findParam(context, itemID, 3);

		double formValue = 0;
		double dbValue = 0;
		// Se crean mapas con información de los variables y ids de las
		// dimensiones para almacenar respuestas
		Map<String, String[]> questions = new HashMap<String, String[]>();
		Map<String, String> criteriaIds = new HashMap<String, String>();

		questions.put("Availability", new String[] { "ava1" });
		questions.put("Accuracy", new String[] { "acc1" });
		questions.put("Ease to use", new String[] { "eou1" });
		questions.put("Effectiveness", new String[] { "eff1" });
		questions.put("Motivation", new String[] { "mot1" });
		questions.put("Relevance", new String[] { "rel1" });
		questions.put("Visual Design", new String[] { "vid1", "vid2" });

		criteriaIds.put("19", "Availability");
		criteriaIds.put("21", "Accuracy");
		criteriaIds.put("20", "Ease to use");
		criteriaIds.put("17", "Effectiveness");
		criteriaIds.put("16", "Motivation");
		criteriaIds.put("15", "Relevance");
		criteriaIds.put("18", "Visual Design");

		// Recorremos los param para actualizar o guardar el valor de la métrica
		for (AssessParam param : assessParamList) {
			if (param.getMetricValue() != null && param.getMetricValue() != "") {
				formValue = Double.valueOf(param.getMetricValue()).doubleValue();
			}
			String metricId = String.valueOf(param.getAssessMetricID());
			String criteria = criteriaIds.get(metricId);
			String[] questIds = questions.get(criteria);
			for (String q : questIds) {
				dbValue += request.getParameter(q) != null ? Double.valueOf(request.getParameter(q)).doubleValue() : 0;
			}
			if (questIds.length > 0) {
				dbValue /= questIds.length;
			}
			if (formValue > 0) {
				dbValue = ((dbValue/5) + formValue) / 2;
			}
			if (dbValue > 0) {
				Metric.addAssessValue(context, dbValue, criteria, 3, itemID);
			}
			// Se resetean los valores
			formValue = 0;
			dbValue = 0;
		}
		request.setAttribute("item", item);
		JSPManager.showJSP(request, response, "/tools/success-page.jsp");
	}
}