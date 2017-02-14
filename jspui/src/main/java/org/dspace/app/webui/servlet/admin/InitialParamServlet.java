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
import org.dspace.loa.Metric;

/**
 * Servlet for setting up the initial parameters of each LO's assessment
 *
 * @author Andres Salazar
 * @version $Revision$
 */
public class InitialParamServlet extends DSpaceServlet
{
	/** User selects layers to be parameterized in assessment */
    public static final int LAYER_PARAM = 1;
	
	/** User selects metrics to take into account in assessment */
    public static final int METRIC_PARAM = 5;
	
    /** User's previous parameterized metrics vector */
    private Vector assessParamList = null;
    
    /** User's selected metrics ID's vector */
    private Vector ckMetricsID = null;
	
    /** Logger */
    private static Logger log = Logger.getLogger(InitialParamServlet.class);

    
    protected void doDSGet(Context context, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException
    {
    	HttpSession session = request.getSession(false);
    	
    	if(session==null)
    	{
    		JSPManager.showInternalError(request, response);
    	}
    	
    	int action = UIUtil.getIntParameter(request, "action");
    	
        Item item = Item.find(context, UIUtil.getIntParameter(request, "item_id"));
        String handle = HandleManager.findHandle(context, item);
        
        Vector dimensionList = (Vector)session.getAttribute("LOA.dimensionList");
    	Vector metricList = (Vector)session.getAttribute("LOA.metricList");
    	Vector checkedDimensions = (Vector)session.getAttribute("LOA.ckDimensionList");
    	    	
    	/*
         * Respond to submitted forms. Each form includes an "action" parameter
         * indicating what needs to be done (from the constants above.)
         */
            	
		switch (action)
		{
		
			case LAYER_PARAM:
				
				String layer = request.getParameter("layer_name");
				
				if(layer == null)
				{
					JSPManager.showInternalError(request, response);
		    		System.out.println("Selected layer is not valid");
				}
				
				dimensionList = setDimensions(context, dimensionList, layer);
				metricList = setMetrics(context, metricList, layer);
				
				request.setAttribute("item", item);
	    		request.setAttribute("handle", handle);
	    		request.setAttribute("layer_name", layer);
	            		    		   		
	    		session.setAttribute("LOA.dimensionList", dimensionList);
	    		session.setAttribute("LOA.metricList", metricList);
	    		
	    		JSPManager.showJSP(request, response, "/tools/dim-display.jsp");
				
			break;
													
			case METRIC_PARAM:
			
				layer = request.getParameter("layer");
						        
		        String[] ckMetrics = request.getParameterValues("metrics");
		        	        
		        ckMetricsID = new Vector();
		        String lay_id,dimension_id,metric_id = null;
				
		        for (int i = 0; i < ckMetrics.length; i++)
		        {
		        	if (ckMetrics[i].equals("rigrel"))
		        	{
		        		lay_id = request.getParameter("rigrel_lay");
		        		dimension_id = request.getParameter("rigrel_dim");
		        		metric_id = request.getParameter("rigrel_id");
		        		ckMetricsID.addElement(lay_id + "," + dimension_id + "," + metric_id);
		        	}		        		
		        	if (ckMetrics[i].equals("relev"))
		        	{
		        		lay_id = request.getParameter("relev_lay");
		        		dimension_id = request.getParameter("relev_dim");
		        		metric_id = request.getParameter("relev_id");
		        		ckMetricsID.addElement(lay_id + "," + dimension_id + "," + metric_id);
		        	}	
		        	if (ckMetrics[i].equals("visib"))
		        	{
		        		lay_id = request.getParameter("visib_lay");
		        		dimension_id = request.getParameter("visib_dim");
		        		metric_id = request.getParameter("visib_id");
		        		ckMetricsID.addElement(lay_id + "," + dimension_id + "," + metric_id);
		        	}	
		        	if (ckMetrics[i].equals("effect"))
		        	{
		        		lay_id = request.getParameter("effect_lay");
		        		dimension_id = request.getParameter("effect_dim");
		        		metric_id = request.getParameter("effect_id");
		        		ckMetricsID.addElement(lay_id + "," + dimension_id + "," + metric_id);
		        	}	
		        	if (ckMetrics[i].equals("motiva"))
		        	{
		        		lay_id = request.getParameter("motiva_lay");
		        		dimension_id = request.getParameter("motiva_dim");
		        		metric_id = request.getParameter("motiva_id");
		        		ckMetricsID.addElement(lay_id + "," + dimension_id + "," + metric_id);
		        	}
		        	if (ckMetrics[i].equals("poteffect"))
		        	{
		        		lay_id = request.getParameter("poteffect_lay");
		        		dimension_id = request.getParameter("poteffect_dim");
		        		metric_id = request.getParameter("poteffect_id");
		        		ckMetricsID.addElement(lay_id + "," + dimension_id + "," + metric_id);
		        	}
		        	if (ckMetrics[i].equals("visdes"))
		        	{
		        		lay_id = request.getParameter("visdes_lay");
		        		dimension_id = request.getParameter("visdes_dim");
		        		metric_id = request.getParameter("visdes_id");
		        		ckMetricsID.addElement(lay_id + "," + dimension_id + "," + metric_id);
		        	}	
		        	if (ckMetrics[i].equals("access"))
		        	{
		        		lay_id = request.getParameter("access_lay");
		        		dimension_id = request.getParameter("access_dim");
		        		metric_id = request.getParameter("access_id");
		        		ckMetricsID.addElement(lay_id + "," + dimension_id + "," + metric_id);
		        	}	
		        	if (ckMetrics[i].equals("avail"))
		        	{
		        		lay_id = request.getParameter("avail_lay");
		        		dimension_id = request.getParameter("avail_dim");
		        		metric_id = request.getParameter("avail_id");
		        		ckMetricsID.addElement(lay_id + "," + dimension_id + "," + metric_id);
		        	}	
		        	if (ckMetrics[i].equals("easeuse"))
		        	{
		        		lay_id = request.getParameter("easeuse_lay");
		        		dimension_id = request.getParameter("easeuse_dim");
		        		metric_id = request.getParameter("easeuse_id");
		        		ckMetricsID.addElement(lay_id + "," + dimension_id + "," + metric_id);
		        	}	
		        	if (ckMetrics[i].equals("usable"))
		        	{
		        		lay_id = request.getParameter("usable_lay");
		        		dimension_id = request.getParameter("usable_dim");
		        		metric_id = request.getParameter("usable_id");
		        		ckMetricsID.addElement(lay_id + "," + dimension_id + "," + metric_id);
		        	}	
		        	if (ckMetrics[i].equals("accuracy"))
		        	{
		        		lay_id = request.getParameter("accuracy_lay");
		        		dimension_id = request.getParameter("accuracy_dim");
		        		metric_id = request.getParameter("accuracy_id");
		        		ckMetricsID.addElement(lay_id + "," + dimension_id + "," + metric_id);
		        	}	
		        	if (ckMetrics[i].equals("coherence"))
		        	{
		        		lay_id = request.getParameter("coherence_lay");
		        		dimension_id = request.getParameter("coherence_dim");
		        		metric_id = request.getParameter("coherence_id");
		        		ckMetricsID.addElement(lay_id + "," + dimension_id + "," + metric_id);
		        	}	
		        	if (ckMetrics[i].equals("complete"))
		        	{
		        		lay_id = request.getParameter("complete_lay");
		        		dimension_id = request.getParameter("complete_dim");
		        		metric_id = request.getParameter("complete_id");
		        		ckMetricsID.addElement(lay_id + "," + dimension_id + "," + metric_id);
		        	}	
		        	if (ckMetrics[i].equals("consist"))
		        	{
		        		lay_id = request.getParameter("consist_lay");
		        		dimension_id = request.getParameter("consist_dim");
		        		metric_id = request.getParameter("consist_id");
		        		ckMetricsID.addElement(lay_id + "," + dimension_id + "," + metric_id);
		        	}	
		        }
				
				assessParamList = AssessItemServlet.loadAssessParam(context, request, response, item.getID());
		        
		        if(checkedDimensions == null)
		        {
					checkedDimensions = verifyCheckedDimensions(context, ckMetricsID, assessParamList, layer);
		        }else{
		        	checkedDimensions.clear();
					checkedDimensions = verifyCheckedDimensions(context, ckMetricsID, assessParamList, layer);
		        }
		        
		        request.setAttribute("item", item);
	            request.setAttribute("handle", handle);
	            request.setAttribute("layer", layer);
	            
	            session.setAttribute("LOA.ckDimensionList", checkedDimensions);
		        
		        JSPManager.showJSP(request, response, "/tools/dim-param-form.jsp");
			
			break;
		
		}
	    	
    }

    
	protected void doDSPost(Context context, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException
    {
		HttpSession session = request.getSession(false);
    	
    	if(session==null)
    	{
    		JSPManager.showInternalError(request, response);
    	}
		
		int itemID = UIUtil.getIntParameter(request,"item_id");
        
		String layer = request.getParameter("layer");
		
        Item item = Item.find(context, itemID);
        
        String handle = HandleManager.findHandle(context, item);
        
        String weight, dimWght = null;
        
        //Firstly, we get the values set by user in GUI and check total weight by layer
        
        boolean validWeights = validateTotalWeight(context, request, response);
        
        if (!validWeights)
        {
        	JSPManager.showInternalError(request, response);
			System.out.println("Assigned weights must complete 100%!!!");
        }
        	
		//Later, we check values previously parameterized and update them if necessary
		assessParamList = AssessItemServlet.loadAssessParam(context, request, response, itemID);
		
		if(assessParamList != null && !assessParamList.isEmpty())
		{
			for (int i = 0;i < assessParamList.size(); i++)
    		{
    			AssessParam assessParam = (AssessParam) assessParamList.elementAt(i);
    			
    			if (layer.equals("Administrator"))
    			{
    				if (request.getParameter("Contextual") != null && assessParam.getDimID() == 2 && assessParam.getLayerID() == 1)
    		        {
    		        	dimWght = request.getParameter("Contextual");
    		        	Dimension.updateDimensionWeight(context, assessParam.getDimWeightID(), itemID, dimWght);
    		        }
    				if (request.getParameter("Functional") != null && assessParam.getDimID() == 5 && assessParam.getLayerID() == 1)
    		        {
    		        	dimWght = request.getParameter("Functional");
    		        	Dimension.updateDimensionWeight(context, assessParam.getDimWeightID(), itemID, dimWght);
    		        }
    				if (request.getParameter("Metadata") != null && assessParam.getDimID() == 6 && assessParam.getLayerID() == 1)
    		        {
    		        	dimWght = request.getParameter("Metadata");
    		        	Dimension.updateDimensionWeight(context, assessParam.getDimWeightID(), itemID, dimWght);
    		        }				
    			}
    			if (layer.equals("Expert"))
    			{
    				if (request.getParameter("Content") != null && assessParam.getDimID() == 1 && assessParam.getLayerID() == 2)
            		{
            			dimWght = request.getParameter("Content");
            			Dimension.updateDimensionWeight(context, assessParam.getDimWeightID(), itemID, dimWght);
            		}
    				if (request.getParameter("Educational") != null && assessParam.getDimID() == 3 && assessParam.getLayerID() == 2)
    		        {
    		        	dimWght = request.getParameter("Educational");
    		        	Dimension.updateDimensionWeight(context, assessParam.getDimWeightID(), itemID, dimWght);
    		        }
    				if (request.getParameter("Esthetic") != null && assessParam.getDimID() == 4 && assessParam.getLayerID() == 2)
    		        {
    		        	dimWght = request.getParameter("Esthetic");
    		        	Dimension.updateDimensionWeight(context, assessParam.getDimWeightID(), itemID, dimWght);
    		        }
    		        if (request.getParameter("Functional") != null && assessParam.getDimID() == 5 && assessParam.getLayerID() == 2)
    		        {
    		        	dimWght = request.getParameter("Functional");
    		        	Dimension.updateDimensionWeight(context, assessParam.getDimWeightID(), itemID, dimWght);
    		        }
    		        if (request.getParameter("Metadata") != null && assessParam.getDimID() == 6 && assessParam.getLayerID() == 2)
    		        {
    		        	dimWght = request.getParameter("Metadata");
    		        	Dimension.updateDimensionWeight(context, assessParam.getDimWeightID(), itemID, dimWght);
    		        }	
    			}
    			if (layer.equals("Student"))
    			{
    				if (request.getParameter("Contextual") != null && assessParam.getDimID() == 2 && assessParam.getLayerID() == 3)
    		        {
    		        	dimWght = request.getParameter("Contextual");
    		        	Dimension.updateDimensionWeight(context, assessParam.getDimWeightID(), itemID, dimWght);
    		        }
    		        if (request.getParameter("Educational") != null && assessParam.getDimID() == 3 && assessParam.getLayerID() == 3)
    		        {
    		        	dimWght = request.getParameter("Educational");
    		        	Dimension.updateDimensionWeight(context, assessParam.getDimWeightID(), itemID, dimWght);
    		        }
    		        if (request.getParameter("Esthetic") != null && assessParam.getDimID() == 4 && assessParam.getLayerID() == 3)
    		        {
    		        	dimWght = request.getParameter("Esthetic");
    		        	Dimension.updateDimensionWeight(context, assessParam.getDimWeightID(), itemID, dimWght);
    		        }	
    		        if (request.getParameter("Functional") != null && assessParam.getDimID() == 5 && assessParam.getLayerID() == 3)
    		        {
    		        	dimWght = request.getParameter("Functional");
    		        	Dimension.updateDimensionWeight(context, assessParam.getDimWeightID(), itemID, dimWght);
    		        }	
    		        if (request.getParameter("Metadata") != null && assessParam.getDimID() == 6 && assessParam.getLayerID() == 3)
    		        {
    		        	dimWght = request.getParameter("Metadata");
    		        	Dimension.updateDimensionWeight(context, assessParam.getDimWeightID(), itemID, dimWght);
    		        }
    			}	
    		}
		}
		
		// Saves and updates parameterized metrics assessment for the item involved and show confirm page
		
		if (ckMetricsID == null)
		{
			JSPManager.showInternalError(request, response);
			System.out.println("You must select at least a metric!!!");
		}
		
		for (int i = 0;i < ckMetricsID.size();i ++)
        { 
    		try 
    		{
				String metricInfo = ckMetricsID.elementAt(i).toString();
				String[] data = metricInfo.split(",");
				int layerID = Integer.valueOf(data[0]).intValue();
				int dimensionID = Integer.valueOf(data[1]).intValue();
				int metricID = Integer.valueOf(data[2]).intValue();
				
				Metric.addAssessMetric(context, metricID, itemID);
				
        		if (request.getParameter("Content") != null && dimensionID == 1)
        		{
        			weight = request.getParameter("Content");
	        		Dimension.addDimensionWeight(context, dimensionID, layerID, itemID, weight);
        		}
		        if (request.getParameter("Contextual") != null && dimensionID == 2)
		        {
		        	weight = request.getParameter("Contextual");
    		        Dimension.addDimensionWeight(context, dimensionID, layerID, itemID, weight);
		        }
		        if (request.getParameter("Educational") != null && dimensionID == 3)
		        {
		        	weight = request.getParameter("Educational");
    		        Dimension.addDimensionWeight(context, dimensionID, layerID, itemID, weight);
		        }
		        if (request.getParameter("Esthetic") != null && dimensionID == 4)
		        {
		        	weight = request.getParameter("Esthetic");
    		        Dimension.addDimensionWeight(context, dimensionID, layerID, itemID, weight);
		        }	
		        if (request.getParameter("Functional") != null && dimensionID == 5)
		        {
		        	weight = request.getParameter("Functional");
    		        Dimension.addDimensionWeight(context, dimensionID, layerID, itemID, weight);
		        }	
		        if (request.getParameter("Metadata") != null && dimensionID == 6)
		        {
		        	weight = request.getParameter("Metadata");
    		        Dimension.addDimensionWeight(context, dimensionID, layerID, itemID, weight);
		        }
			} 
    		catch (SQLException e) 
    		{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }  
		
        request.setAttribute("item", item);
        request.setAttribute("handle", handle);
        
        if (request.getParameter("submit_yes") != null)
            JSPManager.showJSP(request, response, "/tools/param-form.jsp");
        
        if (layer.equals("Administrator"))
        {
        	Vector adminAvailAssess = (Vector)session.getAttribute("LOA.adminAvailAssess");
            
            String criteriaName,assessOptions = null;
            
            if (adminAvailAssess == null)
            {	//add first administrator assess options to the front-end
            	adminAvailAssess = new Vector();
            }else{
            	// if adminassessOpt list has been already invoked 
            	adminAvailAssess.clear();
            }
            
            if (assessParamList != null && !assessParamList.isEmpty())
        	{
        		for (int i = 0;i < assessParamList.size(); i++)
        		{
        			AssessParam assessParam = (AssessParam) assessParamList.elementAt(i);
        			
        			if (assessParam.getLayerID() == 1)
        			{
        				criteriaName = Metric.findNameByID(context, assessParam.getAssessMetricID());
        				assessOptions = AssessItemServlet.getListOptions(criteriaName);			
        			}
        			
        			if(!adminAvailAssess.contains(assessOptions))
        				adminAvailAssess.addElement(assessOptions);
        		}	
        	}
        	
    		for (int i = 0;i < ckMetricsID.size();i ++) 
    		{
    			String metricInfo = ckMetricsID.elementAt(i).toString();
				String[] data = metricInfo.split(",");
				int layerID = Integer.valueOf(data[0]).intValue();
				int metricID = Integer.valueOf(data[2]).intValue();
				
				if (layerID == 1)
				{
					criteriaName = Metric.findNameByID(context, metricID);
					assessOptions = AssessItemServlet.getListOptions(criteriaName);
				}
				
    			if(!adminAvailAssess.contains(assessOptions))
    				adminAvailAssess.addElement(assessOptions);
    		}
    		                  
    		session.setAttribute("LOA.adminAvailAssess", adminAvailAssess);
            
            JSPManager.showJSP(request, response, "/tools/admin-assess.jsp");        	
        }else
        	JSPManager.showJSP(request, response, "/tools/success-page.jsp");
    }

	private Vector setDimensions(Context context, Vector dimensionList, 
			String layer) {
		// TODO Auto-generated method stub
    	Dimension[] dimensions = null;
    	
    	if (dimensionList == null)
		{	//add first dimensions to the front-end
    		try {
    			dimensions = Dimension.findByLayer(context, layer);
    			dimensionList = new Vector(); //first order
    			for(int i = 0;i < dimensions.length;i++)
    				dimensionList.addElement(dimensions[i]);
    		} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
		}else{ 	// if dimension's list has been already invoked 			
			dimensionList.clear();
			try {
				dimensions = Dimension.findByLayer(context, layer);
				dimensionList.ensureCapacity(dimensions.length);
				for(int i = 0;i < dimensions.length;i++)
					dimensionList.addElement(dimensions[i]);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	
    	return dimensionList;
	}
	
	private Vector setMetrics(Context context, Vector metricList,
			String layer) {
		// TODO Auto-generated method stub
		Metric[] metrics = null;
		
		if (metricList == null)
		{	//add first dimensions to the front-end
			try {
				metrics = Metric.findByLayer(context, layer);
				metricList = new Vector(); //first order
				for(int j = 0;j < metrics.length;j++)
					metricList.addElement(metrics[j]);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{ 	// if metric's list has been already invoked
			metricList.clear();
			try {
				metrics = Metric.findByLayer(context, layer);
				metricList.ensureCapacity(metrics.length);
				for(int j = 0;j < metrics.length;j++)
					metricList.addElement(metrics[j]);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return metricList;
	}
	
	private boolean validateTotalWeight(Context context, HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException
	{
		boolean ok = false;
		double dimWeight = 0.0;
		double totWeight = 0.0;
        
		// Dimension weightings
		if (request.getParameter("Content") != null)
		{
			dimWeight = Double.valueOf(request.getParameter("Content")).doubleValue()/100.0;
			totWeight += dimWeight;
		}
        if (request.getParameter("Contextual") != null)
        {
        	dimWeight = Double.valueOf(request.getParameter("Contextual")).doubleValue()/100.0;
        	totWeight += dimWeight;
        }
        if (request.getParameter("Educational") != null)
        {
        	dimWeight = Double.valueOf(request.getParameter("Educational")).doubleValue()/100.0;
        	totWeight += dimWeight;
        }
        if (request.getParameter("Esthetic") != null)
        {
        	dimWeight = Double.valueOf(request.getParameter("Esthetic")).doubleValue()/100.0;
        	totWeight += dimWeight;
        }	
        if (request.getParameter("Functional") != null)
        {
        	dimWeight = Double.valueOf(request.getParameter("Functional")).doubleValue()/100.0;
        	totWeight += dimWeight;
        }	
        if (request.getParameter("Metadata") != null)
        {
        	dimWeight = Double.valueOf(request.getParameter("Metadata")).doubleValue()/100.0;
        	totWeight += dimWeight;
        }
		
        // Validation for total dimension weightings
        if(totWeight > 0.998 && totWeight < 1.002)
        		ok = true;
        
		return ok;		
	}
	
	private Vector verifyCheckedDimensions(Context context, Vector checkedMetrics, Vector assessmentParam, String layer) 
	{
		// TODO Auto-generated method stub
		Vector ckDimensions = new Vector();
		String dimensionName = null;
		
		// first, we verify metric's dimension previously parameterized
		if (assessmentParam != null && !assessmentParam.isEmpty())
		{
			for (int i = 0;i < assessmentParam.size();i ++)
    		{
    			AssessParam assessParam = (AssessParam) assessmentParam.elementAt(i);
				if (layer.equals("Administrator") && assessParam.getLayerID() == 1)
				{
					try {
						dimensionName = Dimension.findNameByID(context, assessParam.getDimID());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (layer.equals("Expert") && assessParam.getLayerID() == 2)
				{
					try {
						dimensionName = Dimension.findNameByID(context, assessParam.getDimID());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (layer.equals("Student") && assessParam.getLayerID() == 3)
				{
					try {
						dimensionName = Dimension.findNameByID(context, assessParam.getDimID());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
    			if(!ckDimensions.contains(dimensionName) && dimensionName != null)
    				ckDimensions.addElement(dimensionName);
    		}
		}
		
		// Now, we can add dimension from metrics recently checked to item's assessment
		if (checkedMetrics != null && !checkedMetrics.isEmpty())
		{
			for (int i = 0;i < checkedMetrics.size();i ++)
			{
				String metricInfo = checkedMetrics.elementAt(i).toString();
				String[] data = metricInfo.split(",");
				int layerID = Integer.valueOf(data[0]).intValue();
				int dimensionID = Integer.valueOf(data[1]).intValue();
				
				if (layer.equals("Administrator") && layerID == 1)
				{
					try {
						dimensionName = Dimension.findNameByID(context, dimensionID);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (layer.equals("Expert") && layerID == 2)
				{
					try {
						dimensionName = Dimension.findNameByID(context, dimensionID);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (layer.equals("Student") && layerID == 3)
				{
					try {
						dimensionName = Dimension.findNameByID(context, dimensionID);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if(!ckDimensions.contains(dimensionName) && dimensionName != null)
    				ckDimensions.addElement(dimensionName);
			}
		}
		
		return ckDimensions;	
	}
}