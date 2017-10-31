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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.dspace.loa.StudentAssessHelper;

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
		StudentAssessHelper helper = new StudentAssessHelper();

		// Se crean mapas con información de los variables y ids de las
		// dimensiones para almacenar respuestas
		Map<String, String[]> questions = new HashMap<String, String[]>();
		Map<String, List<Double>> perMetricResponses = new HashMap<String,List<Double>>();

		questions.put("19", new String[] { "ava1" });
		questions.put("21", new String[] { "acc1" });
		questions.put("20", new String[] { "eou1" });
		questions.put("17", new String[] { "eff1" });
		questions.put("16", new String[] { "mot1" });
		questions.put("15", new String[] { "rel1" });
		questions.put("18", new String[] { "vid1", "vid2" });
		
		for(Entry<String,String[]> entry: questions.entrySet()){
			List<Double> valores = new ArrayList<Double>();
			for(String id: entry.getValue()){
				if(request.getParameter(id) != null){
					valores.add(Double.valueOf(request.getParameter(id)));
				}
			}
			perMetricResponses.put(entry.getKey(), valores);
		}
		
		helper.setExpertAssessment(context, item.getID(), perMetricResponses);

		request.setAttribute("item", item);
		JSPManager.showJSP(request, response, "/tools/success-page.jsp");
	}
}