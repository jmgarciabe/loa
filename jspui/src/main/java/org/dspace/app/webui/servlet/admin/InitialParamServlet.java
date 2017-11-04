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
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.dspace.app.webui.servlet.DSpaceServlet;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.handle.HandleManager;
import org.dspace.loa.AssessParam;
import org.dspace.loa.Dimension;
import org.dspace.loa.Metric;
import org.dspace.loa.StartAssessHelper;

/**
 * Servlet for setting up the initial parameters of each LO's assessment
 * 
 * @author Andres Salazar
 * @version $Revision$
 */
public class InitialParamServlet extends DSpaceServlet {

	/** User start set of parameters */
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
		request.setAttribute("item", item);
		String handle = HandleManager.findHandle(context, item);

		/*
		 * Respond to submitted forms. Each form includes an "action" parameter
		 * indicating what needs to be done (from the constants above.)
		 */
		
		StartAssessHelper helper = new StartAssessHelper();
		String layer;

		switch (action) {

		case START_PARAM:
			Vector<String> layers = helper.getLayers(context);
			
			session.setAttribute("LOA.adminAssessOpt", layers);
			JSPManager.showJSP(request, response, "/tools/param-form.jsp");
			break;

		case LAYER_PARAM:

			layer = request.getParameter("layer_name");
			if (layer == null) {
				JSPManager.showInternalError(request, response);
				System.out.println("Selected layer is not valid");
			}
			Vector<Dimension> dimensionList = helper.getDimensions(context, layer);
			Vector<Metric> metricList = Metric.findByLayer(context, layer);
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

			Vector<String> checkedDimensions = helper.verifyCheckedDimensions(context, ckMetricsID, layer);

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

		int layerId = layer.equals("Administrator") ? 1 : layer.equals("Expert") ? 2 : layer.equals("Student") ? 3 : 0;

		// We check values previously parameterized and update them if
		// necessary

		if (ckMetricsID == null) {
			JSPManager.showInternalError(request, response);
			System.out.println("You must select at least a metric!!!");
		}

		Dimension[] dimensionsList = Dimension.findByLayer(context, layer);

		// Buscamos solo los assess params de la capa
		assessParamList = AssessParam.findParam(context, itemId, layerId);

		// Eliminamos assessments de metricas que existian pero no se
		// seleccionaron
		if (assessParamList != null) {
			for (int i = 0; i < assessParamList.size(); i++) {
				AssessParam assessParam = assessParamList.elementAt(i);
				boolean delete = true;
				for (int j = 0; j < ckMetricsID.size(); j++) {
					String metricInfo = ckMetricsID.elementAt(j).toString();
					String[] data = metricInfo.split(",");
					int metricID = Integer.valueOf(data[2]).intValue();
					if (assessParam.getAssessMetricID() == metricID) {
						delete = false;
						break;
					}
				}
				if (delete) {
					Metric.deleteAssessMetric(context, assessParam.getAssessMetricID(), itemId);
				}
			}
		}
		
		

		// Eliminamos los dimension weigthing previos que no se vayan a utilizar
		for(Dimension dimension : dimensionsList){
			boolean delete = true;
			String[] data = null;
			for (int j = 0; j < ckMetricsID.size(); j++) {
				String metricInfo = ckMetricsID.elementAt(j).toString();
				data = metricInfo.split(",");
				int dimId = Integer.valueOf(data[1]).intValue();
				if (dimension.getId() == dimId) {
					delete = false;
					break;
				}
			}
			if (delete) {
				Dimension.deleteDimensionWeighting(context, layerId, dimension.getId(), itemId);
			}
		}

		// Agregamos dimension weighting y assessment result
		for (int i = 0; i < ckMetricsID.size(); i++) {
			Dimension dimension = null;
			String metricInfo = ckMetricsID.elementAt(i).toString();
			String[] data = metricInfo.split(",");
			int layerID = Integer.valueOf(data[0]).intValue();
			int dimensionID = Integer.valueOf(data[1]).intValue();
			int metricID = Integer.valueOf(data[2]).intValue();

			Metric.addAssessMetric(context, metricID, itemId);

			for (Dimension dim : dimensionsList) {
				if (dim.getId() == dimensionID) {
					dimension = dim;
					break;
				}
			}

			if (request.getParameter(dimension.getName()) != null) {
				String weight = request.getParameter(dimension.getName());
				Dimension.addDimensionWeight(context, dimensionID, layerID, itemId, weight);
			}
		}

		request.setAttribute("item", item);
		request.setAttribute("handle", handle);

		JSPManager.showJSP(request, response, "/tools/param-form.jsp");

	}

}