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
import java.util.List;
import java.util.Map;

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
import org.dspace.loa.AssessmentMetric;
import org.dspace.loa.Dimension;
import org.dspace.loa.Layer;
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
		int layerId;

		switch (action) {

		case START_PARAM:
			List<Layer> layers = helper.getLayers(context);
			request.setAttribute("LOA.layerList", layers);
			JSPManager.showJSP(request, response, "/tools/param-form.jsp");
			break;

		case LAYER_PARAM:

			layerId = Integer.valueOf(request.getParameter("layerId"));
			int itemId = item.getID();
			List<Dimension> dimenisons = helper.getDimensions(context, layerId);
			List<AssessmentMetric> metrics = helper.getAssessmentMetrics(context, itemId, layerId);
			request.setAttribute("handle", handle);
			request.setAttribute("layerId", layerId);
			request.setAttribute("LOA.dimensionList", dimenisons);
			session.setAttribute("LOA.metricList", metrics);
			JSPManager.showJSP(request, response, "/tools/dim-display.jsp");
			break;

		case METRIC_PARAM:

			layerId = Integer.valueOf(request.getParameter("layerId"));
			Layer layer = helper.findLayer(context, layerId);
			String[] ckMetrics = request.getParameterValues("metrics");
			List<AssessmentMetric> metricList = (List<AssessmentMetric>) session.getAttribute("LOA.metricList");
			// Actualizamos las métricas que fueron seleccionadas
			for (int i = 0; i < metricList.size(); i++) {
				AssessmentMetric metric = metricList.get(i);
				boolean wasChecked = false;
				for (String m : ckMetrics) {
					if (Integer.valueOf(m).intValue() == metric.getId()) {
						wasChecked = true;
						break;
					}
				}
				metric.setChecked(wasChecked);
				metricList.set(i, metric);
			}

			List<Dimension> checkedDimensions = helper.getDimensionsInMetrics(metricList);

			request.setAttribute("handle", handle);
			request.setAttribute("layer", layer);
			session.setAttribute("LOA.metricList", metricList);
			request.setAttribute("LOA.ckDimensionList", checkedDimensions);
			JSPManager.showJSP(request, response, "/tools/dim-param-form.jsp");
			break;

		}

	}

	protected void doDSPost(Context context, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, SQLException, AuthorizeException {
		HttpSession session = request.getSession(false);

		int itemId = UIUtil.getIntParameter(request, "item_id");
		int layerId = Integer.valueOf(request.getParameter("layerId"));
		Item item = Item.find(context, itemId);
		String handle = HandleManager.findHandle(context, item);
		StartAssessHelper helper = new StartAssessHelper();

		// We check values previously parameterized and update them if
		// necessary
		List<AssessmentMetric> metrics = (List<AssessmentMetric>) session.getAttribute("LOA.metricList");
		helper.updateSelectedMetrics(context, metrics, itemId);

		List<Dimension> dimensionsList = helper.getDimensions(context, layerId);
		Map<String, Double> weightsPerDimension = new HashMap<String, Double>();
		for (Dimension dimension : dimensionsList) {
			String value = request.getParameter(dimension.getName());
			if (value != null && value.length() > 0) {
				weightsPerDimension.put(dimension.getName(), Double.valueOf(value));
			}
		}
		helper.updateWeights(context, metrics, itemId, layerId, weightsPerDimension);

		request.setAttribute("item", item);
		request.setAttribute("handle", handle);
		List<Layer> layers = helper.getLayers(context);
		request.setAttribute("LOA.layerList", layers);
		JSPManager.showJSP(request, response, "/tools/param-form.jsp");

	}

}