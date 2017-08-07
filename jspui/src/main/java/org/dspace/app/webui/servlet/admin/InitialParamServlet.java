/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.webui.servlet.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.dspace.app.webui.servlet.AssessItemServlet;
import org.dspace.app.webui.servlet.DSpaceServlet;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.handle.HandleManager;
import org.dspace.loa.AssessParam;
import org.dspace.loa.Dimension;
import org.dspace.loa.Layer;
import org.dspace.loa.Metric;

/**
 * Servlet for setting up the initial parameters of each LO's assessment
 * 
 * @author Andres Salazar
 * @version $Revision$
 */
public class InitialParamServlet extends DSpaceServlet {
	
	/** User start set of parameters*/
	public static final int START_PARAM = 3;
	
	/** User selects layers to be parameterized in assessment */
	public static final int LAYER_PARAM = 1;

	/** User selects metrics to take into account in assessment */
	public static final int METRIC_PARAM = 5;

	/** User's previous parameterized metrics vector */
	private Vector<AssessParam> assessParamList = null;

	/** User's selected metrics ID's vector */
	private Vector<String> ckMetricsID = null;

	/** Logger */
	private static Logger log = Logger.getLogger(InitialParamServlet.class);

	protected void doDSGet(Context context, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, SQLException, AuthorizeException {
		HttpSession session = request.getSession(false);

		if (session == null) {
			JSPManager.showInternalError(request, response);
		}

		int action = UIUtil.getIntParameter(request, "action");

		Item item = Item.find(context, UIUtil.getIntParameter(request, "item_id"));
		String handle = HandleManager.findHandle(context, item);

		

		/*
		 * Respond to submitted forms. Each form includes an "action" parameter
		 * indicating what needs to be done (from the constants above.)
		 */

		String layer = null;
		
		switch (action) {
		
		case START_PARAM:
			
			request.setAttribute("item", item);
			Vector<String> adminAssessOpt = new Vector<String>();
			Layer[] layersRetrieved = Layer.findAllLayers(context);
			for (int i = 0; i < layersRetrieved.length; i++) {
				layer = layersRetrieved[i].getName();
				String layerOptions = getListOptions(layer);
				if (!adminAssessOpt.contains(layerOptions))
					adminAssessOpt.addElement(layerOptions);
			}
			session.setAttribute("LOA.adminAssessOpt", adminAssessOpt);
			JSPManager.showJSP(request, response, "/tools/param-form.jsp");

		case LAYER_PARAM:

			layer = request.getParameter("layer_name");

			if (layer == null) {
				JSPManager.showInternalError(request, response);
				System.out.println("Selected layer is not valid");
			}

			Vector<Dimension> dimensionList = setDimensions(context, layer);
			Vector<Metric> metricList = setMetrics(context, layer);

			request.setAttribute("item", item);
			request.setAttribute("handle", handle);
			request.setAttribute("layer_name", layer);

			session.setAttribute("LOA.dimensionList", dimensionList);
			session.setAttribute("LOA.metricList", metricList);

			JSPManager.showJSP(request, response, "/tools/dim-display.jsp");

			break;

		case METRIC_PARAM:
			
			layer = request.getParameter("layer");
			int layerId = layer.equals("Administrator") ? 1 : layer.equals("Expert") ? 2 : layer.equals("Student") ? 3 : 0;

			String[] ckMetrics = request.getParameterValues("metrics");

			ckMetricsID = new Vector<String>();

			for (int i = 0; i < ckMetrics.length; i++) {

				String layerVarName = ckMetrics[i] + "_lay";
				String dimensioVarName = ckMetrics[i] + "_dim";
				String metricVarName = ckMetrics[i] + "_id";
				String lay_id = request.getParameter(layerVarName);
				String dimension_id = request.getParameter(dimensioVarName);
				String metric_id = request.getParameter(metricVarName);
				ckMetricsID.addElement(lay_id + "," + dimension_id + "," + metric_id);
			}

			Vector<String> checkedDimensions = verifyCheckedDimensions(context, ckMetricsID, layer);

			request.setAttribute("item", item);
			request.setAttribute("handle", handle);
			request.setAttribute("layer", layer);
			session.setAttribute("LOA.ckDimensionList", checkedDimensions);

			JSPManager.showJSP(request, response, "/tools/dim-param-form.jsp");

			break;

		}

	}

	protected void doDSPost(Context context, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, SQLException, AuthorizeException {
		HttpSession session = request.getSession(false);

		if (session == null) {
			JSPManager.showInternalError(request, response);
		}

		int itemId = UIUtil.getIntParameter(request, "item_id");

		String layer = request.getParameter("layer");

		Item item = Item.find(context, itemId);

		String handle = HandleManager.findHandle(context, item);

		String weight, dimWght = null;

		int layerId = layer.equals("Administrator") ? 1 : layer.equals("Expert") ? 2 : layer.equals("Student") ? 3 : 0;


		// We check values previously parameterized and update them if
		// necessary
		
		if (ckMetricsID == null) {
			JSPManager.showInternalError(request, response);
			System.out.println("You must select at least a metric!!!");
		}

		Dimension[] dimensionsList = Dimension.findByLayer(context, layer);
		
		// Buscanos solo los assess params de la capa
		assessParamList = AssessParam.findParam(context, itemId, layerId);
		
		//Eliminamos assessments de metricas que existian pero no se seleccionaron
		if (assessParamList != null) {
			for (int i = 0; i < assessParamList.size(); i++) {
				AssessParam assessParam = assessParamList.elementAt(i);
				boolean delete = true;
				for (int j = 0; j < ckMetricsID.size(); j++) {
					String metricInfo = ckMetricsID.elementAt(j).toString();
					String[] data = metricInfo.split(",");
					int metricID = Integer.valueOf(data[2]).intValue();
					if(assessParam.getAssessMetricID() == metricID){
						delete = false;
						break;
					}
				}
				if(delete){
					Metric.deleteAssessMetric(context, assessParam.getAssessMetricID() , itemId);
				}
			}
		}
		
		//Eliminamos todos los dimension weigthing previos
		Dimension.DeleteWeightsById(context, itemId, layerId);
				
		//Agregamos dimension weighting y assessment result
		for (int i = 0; i < ckMetricsID.size(); i++) {
			try {
				Dimension dimension = null;
				String metricInfo = ckMetricsID.elementAt(i).toString();
				String[] data = metricInfo.split(",");
				int layerID = Integer.valueOf(data[0]).intValue();
				int dimensionID = Integer.valueOf(data[1]).intValue();
				int metricID = Integer.valueOf(data[2]).intValue();

				Metric.addAssessMetric(context, metricID, itemId);

				for (Dimension dim : dimensionsList) {
					if (dim.getID() == dimensionID) {
						dimension = dim;
						break;
					}
				}

				if (request.getParameter(dimension.getName()) != null) {
					weight = request.getParameter(dimension.getName());
					Dimension.addDimensionWeight(context, dimensionID, layerID, itemId, weight);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		request.setAttribute("item", item);
		request.setAttribute("handle", handle);

		JSPManager.showJSP(request, response, "/tools/param-form.jsp");
		//JSPManager.showJSP(request, response, "/tools/success-page.jsp");
		
	}
	
	private String getListOptions(String option) {
		StringBuilder sb = new StringBuilder();
		sb.append("<option value=\"").append(option).append("\">").append(option).append("</option>\n");

		return sb.toString();
	}


	private Vector<Dimension> setDimensions(Context context, String layer) {

		Vector<Dimension> dimensionList = new Vector<Dimension>();

		try {
			Dimension[] dimensions = Dimension.findByLayer(context, layer);
			for (Dimension dim : dimensions) {
				dimensionList.add(dim);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dimensionList;
	}

	private Vector<Metric> setMetrics(Context context, String layer) {

		Vector<Metric> metricList = null;
		try {
			metricList = Metric.findByLayer(context, layer);
		} catch (SQLException e) {
			e.printStackTrace();

		}
		return metricList;
	}

	private Vector<String> verifyCheckedDimensions(Context context, Vector<String> checkedMetrics, String layer) {

		Vector<String> ckDimensions = new Vector<String>();
		String dimensionName = null;
		int layerId = layer.equals("Administrator") ? 1 : layer.equals("Expert") ? 2 : layer.equals("Student") ? 3 : 0;

		// Now, we can add dimension from metrics recently checked to item's
		// assessment
		if (checkedMetrics != null) {
			for (int i = 0; i < checkedMetrics.size(); i++) {
				String metricInfo = checkedMetrics.elementAt(i).toString();
				String[] data = metricInfo.split(",");
				int tempLayerId = Integer.valueOf(data[0]).intValue();
				int dimensionId = Integer.valueOf(data[1]).intValue();

				if (tempLayerId == layerId) {
					try {
						dimensionName = Dimension.findNameByID(context, dimensionId);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (!ckDimensions.contains(dimensionName))
					ckDimensions.addElement(dimensionName);
			}
		}

		return ckDimensions;
	}
}