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
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
 * Servlet for perform the assessment logic of each of the expert layer metrics
 *
 * @author Andres Salazar
 * @version $Revision$
 */
public class ExpertAssessServlet extends DSpaceServlet
{
	/** Users sets weights to dimensions to indicate their experience in each one */
    public static final int DIM_PARAM = 10;
    
    /** Users send their concept for an item through an answered survey */
    public static final int EXP_SURVEY = 15;
    
    /** Variables to assess content metrics */
    private int contWght = 0;
    private int contWghtDb = 0;
    private double rigor = 0.0;
    
    /** Variables to assess educational metrics */
    private int eduWght = 0;
    private int eduWghtDb = 0;
    private double effect = 0.0;
    
    /** Variables to assess esthetic metrics */
    private int estWght = 0; 
    private int estWghtDb = 0;
    private double visual = 0.0;
    
    /** Variables to assess functional metrics */
    private int funcWght = 0;
    private int funcWghtDb = 0;
    private double reuse = 0.0;
    private double use = 0.0;
    private double access = 0.0;
    
    /** Variables to assess metadata metrics */
    private int metWght = 0;
    private int metWghtDb = 0;
    private double complete = 0.0;
    private double accuracy = 0.0;
    
    protected void doDSGet(Context context, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException
    {
    		    	
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
    	
    	int action = UIUtil.getIntParameter(request, "action");
    	
		int itemID = UIUtil.getIntParameter(request,"item_id");
		
        Item item = Item.find(context, itemID);
        
        String handle = HandleManager.findHandle(context, item);
        
        switch (action)
        {
        	case DIM_PARAM:	
        		//Firstly, we get the values set by user in GUI and check if weights are correct
        		boolean validWeights = validateExpertWeights(context, request, response);
        		
        		if (!validWeights)
                {
                	JSPManager.showInternalError(request, response);
        			System.out.println("Please check your expert's weights, they must be between 1 and 5");
                }
        		
        		//Later, we check values previously parameterized and update them if necessary
        		Vector assessParamList = AssessItemServlet.loadAssessParam(context, request, response, itemID);
        		
        		if(assessParamList != null && !assessParamList.isEmpty())
        		{
        			for (int i = 0;i < assessParamList.size(); i++)
            		{
            			AssessParam assessParam = (AssessParam) assessParamList.elementAt(i);
            			
            			if (assessParam.getMetricValue() != null && assessParam.getExpWeight() > 0 
            					&& assessParam.getLayerID() == 2)
            			{
            				if (request.getParameter("exp_Content") != null && assessParam.getDimID() == 1)
            				{
            					contWght = Integer.valueOf(request.getParameter("exp_Content")).intValue();
            					contWghtDb = assessParam.getExpWeight();
            					rigor = Double.valueOf(assessParam.getMetricValue()).doubleValue();
            					Dimension.updateExpertWeight(context, assessParam.getDimWeightID(), itemID, contWght);
            				}		
                    		if (request.getParameter("exp_Educational") != null && assessParam.getDimID() == 3)
                    		{
                    			eduWght = Integer.valueOf(request.getParameter("exp_Educational")).intValue();
                    			eduWghtDb = assessParam.getExpWeight();
                    			effect = Double.valueOf(assessParam.getMetricValue()).doubleValue();
                    			Dimension.updateExpertWeight(context, assessParam.getDimWeightID(), itemID, eduWght);
                    		}
                    		if (request.getParameter("exp_Esthetic") != null && assessParam.getDimID() == 4)
                    		{
                    			estWght = Integer.valueOf(request.getParameter("exp_Esthetic")).intValue();
                    			estWghtDb = assessParam.getExpWeight();
                    			visual = Double.valueOf(assessParam.getMetricValue()).doubleValue();
                    			Dimension.updateExpertWeight(context, assessParam.getDimWeightID(), itemID, estWght);
                    		}
                    		if (request.getParameter("exp_Functional") != null && assessParam.getDimID() == 5)
                    		{
                    			funcWght = Integer.valueOf(request.getParameter("exp_Functional")).intValue();
                    			funcWghtDb = assessParam.getExpWeight();
                    			if (assessParam.getAssessMetricID() == 10)
                    				reuse = Double.valueOf(assessParam.getMetricValue()).doubleValue();
                    			if (assessParam.getAssessMetricID() == 11)
                    				use = Double.valueOf(assessParam.getMetricValue()).doubleValue();
                    			if (assessParam.getAssessMetricID() == 12)
                    				access = Double.valueOf(assessParam.getMetricValue()).doubleValue();
                    			Dimension.updateExpertWeight(context, assessParam.getDimWeightID(), itemID, funcWght);
                    		}
                    		if (request.getParameter("exp_Metadata") != null && assessParam.getDimID() == 6)
                    		{
                    			metWght = Integer.valueOf(request.getParameter("exp_Metadata")).intValue();
                    			metWghtDb = assessParam.getExpWeight();
                    			if (assessParam.getAssessMetricID() == 13)
                    				complete = Double.valueOf(assessParam.getMetricValue()).doubleValue();
                    			if (assessParam.getAssessMetricID() == 14)
                    				accuracy = Double.valueOf(assessParam.getMetricValue()).doubleValue();
                    			Dimension.updateExpertWeight(context, assessParam.getDimWeightID(), itemID, metWght);
                    		}                    			
            			}else{
            				if (request.getParameter("exp_Content") != null && assessParam.getDimID() == 1 
            						&& assessParam.getLayerID() == 2)
                        	{
            					contWght = Integer.valueOf(request.getParameter("exp_Content")).intValue();
                        		Dimension.updateExpertWeight(context, assessParam.getDimWeightID(), itemID, contWght);
                        	}
                			if (request.getParameter("exp_Educational") != null && assessParam.getDimID() == 3 
                					&& assessParam.getLayerID() == 2)
                		    {
                				eduWght = Integer.valueOf(request.getParameter("exp_Educational")).intValue();
                		        Dimension.updateExpertWeight(context, assessParam.getDimWeightID(), itemID, eduWght);
                		    }
                			if (request.getParameter("exp_Esthetic") != null && assessParam.getDimID() == 4 
                					&& assessParam.getLayerID() == 2)
                		    {
                				estWght = Integer.valueOf(request.getParameter("exp_Esthetic")).intValue();
                		        Dimension.updateExpertWeight(context, assessParam.getDimWeightID(), itemID, estWght);
                		    }
                		    if (request.getParameter("exp_Functional") != null && assessParam.getDimID() == 5 
                		    		&& assessParam.getLayerID() == 2)
                		    {
                		    	funcWght = Integer.valueOf(request.getParameter("exp_Functional")).intValue();
                		        Dimension.updateExpertWeight(context, assessParam.getDimWeightID(), itemID, funcWght);
                		    }	
                		    if (request.getParameter("exp_Metadata") != null && assessParam.getDimID() == 6 
                		    		&& assessParam.getLayerID() == 2)
                		    {
                		    	metWght = Integer.valueOf(request.getParameter("exp_Metadata")).intValue();
                		        Dimension.updateExpertWeight(context, assessParam.getDimWeightID(), itemID, metWght);
                		    }
            			}		
            		}
        		}
        	
        		Vector expMetrics = new Vector();
        		
        		if (expMetrics != null && !expMetrics.isEmpty())
        			expMetrics.clear();
        		
        		expMetrics = AssessItemServlet.getMetrics(context, assessParamList, 2);
        		  
                request.setAttribute("item", item);
                request.setAttribute("handle", handle);
                
                session.setAttribute("LOA.expMetrics", expMetrics);
                
                // Redirect to expert survey
                JSPManager.showJSP(request, response, "/tools/expert-survey.jsp");
        		
        	break;
        	
        	case EXP_SURVEY:
        		
        		int div = 0;
        		
        		if ((request.getParameter("acs1") != null) && (request.getParameter("acs2") != null))
        		{
        			double acs1 = Double.valueOf(request.getParameter("acs1")).doubleValue();
        			double acs2 = Double.valueOf(request.getParameter("acs2")).doubleValue();
        			
        			if (access == 0.0)
        				access = (acs1 + acs2) / 10;
        			else{
        				if (funcWghtDb > funcWght)
        					div = funcWghtDb - funcWght;
        				else if (funcWghtDb < funcWght)
        					div = funcWght - funcWghtDb;
        				else
        					div = 2;
        				
        				if (div == 1)
        					access = (access + ((acs1 + acs2) / 10)) / 2;
        				else
        					access = (access + ((acs1 + acs2) / 10)) / div;
        			}
     
        			Metric.addAssessValue(context, access, "Accessibility", 2, itemID);
        		}
        		if (request.getParameter("acc1") != null)
        		{
        			double acc1 = Double.valueOf(request.getParameter("acc1")).doubleValue();
        			
        			if (accuracy == 0.0)
        				accuracy = acc1 / 5;
        			else{
        				if (metWghtDb > metWght)
        					div = metWghtDb - metWght;
        				else if (metWghtDb < metWght)
        					div = metWght - metWghtDb;
        				else
        					div = 2;
        				
        				if (div == 1)
        					accuracy = (accuracy + (acc1 / 5)) / 2;
        				else
        					accuracy = (accuracy + (acc1 / 5)) / div;
        			}
        			
        			Metric.addAssessValue(context, accuracy, "Accuracy", 2, itemID);
        		}
        		if (request.getParameter("com1") != null)
        		{
        			double com1 = Double.valueOf(request.getParameter("com1")).doubleValue();
        			
        			if (complete == 0.0)
        				complete = com1;
        			else{
        				if (metWghtDb > metWght)
        					div = metWghtDb - metWght;
        				else if (metWghtDb < metWght)
        					div = metWght - metWghtDb;
        				else
        					div = 2;
        				
        				if (div == 1)
        					complete = (complete + com1) / 2;
        				else
        					complete = (complete + com1) / div;
        			}
        			
        			Metric.addAssessValue(context, complete, "Completeness", 2, itemID);
        		}
        		if ((request.getParameter("eou1") != null) && (request.getParameter("eou2") != null))
        		{
        			double eou1 = Double.valueOf(request.getParameter("eou1")).doubleValue();
        			double eou2 = Double.valueOf(request.getParameter("eou2")).doubleValue();
        			
        			if (use == 0.0)
        				use = (eou1 + eou2) / 10;
        			else{
        				if (funcWghtDb > funcWght)
        					div = funcWghtDb - funcWght;
        				else if (funcWghtDb < funcWght)
        					div = funcWght - funcWghtDb;
        				else
        					div = 2;
        				
        				if (div == 1)
        					use = (use + ((eou1 + eou2) / 10)) / 2;
        				else
        					use = (use + ((eou1 + eou2) / 10)) / div;
        			}

        			Metric.addAssessValue(context, use, "Ease to use", 2, itemID);
        		}
        		if ((request.getParameter("poe1") != null) && (request.getParameter("poe2") != null))
        		{
        			double poe1 = Double.valueOf(request.getParameter("poe1")).doubleValue();
        			double poe2 = Double.valueOf(request.getParameter("poe2")).doubleValue();
        			
        			if (effect == 0.0)
        				effect = (poe1 + poe2) / 10;
        			else{
        				if (eduWghtDb > eduWght)
        					div = eduWghtDb - eduWght;
        				else if (eduWghtDb < eduWght)
        					div = eduWght - eduWghtDb;
        				else 
        					div = 2;
        				
        				if (div == 1)
        					effect = (effect + ((poe1 + poe2) / 10)) / 2;
        				else
        					effect = (effect + ((poe1 + poe2) / 10)) / div;
        			}
        			
        			Metric.addAssessValue(context, effect, "Potential Effectiveness", 2, itemID);
        		}
        		if ((request.getParameter("reu1") != null) && (request.getParameter("reu2") != null))
        		{
        			double reu1 = Double.valueOf(request.getParameter("reu1")).doubleValue();
        			double reu2 = Double.valueOf(request.getParameter("reu2")).doubleValue();
        			
        			if (reuse == 0.0)
        				reuse = (reu1 + reu2) / 10;
        			else{
        				if (funcWghtDb > funcWght)
        					div = funcWghtDb - funcWght;
        				else if (funcWghtDb < funcWght)
        					div = funcWght - funcWghtDb;
        				else
        					div = 2;
        				
        				if (div == 1)
        					reuse = (reuse + ((reu1 + reu2) / 10)) / 2;
        				else
        					reuse = (reuse + ((reu1 + reu2) / 10)) / div;
        			}
        			
        			Metric.addAssessValue(context, reuse, "Reusability", 2, itemID);
        		}
        		if ((request.getParameter("rar1") != null) && (request.getParameter("rar2") != null))
        		{
        			double rar1 = Double.valueOf(request.getParameter("rar1")).doubleValue();
        			double rar2 = Double.valueOf(request.getParameter("rar2")).doubleValue();
        			
        			if (rigor == 0.0)
        				rigor = (rar1 + rar2) / 10;
        			else{
        				if (contWghtDb > contWghtDb)
        					div = contWghtDb - contWght;
        				else if (contWghtDb < contWght)
        					div = contWght - contWghtDb;
        				else
        					div = 2;
        				
        				if (div == 1)
        					rigor = (rigor + ((rar1 + rar2) / 10)) / 2;
        				else
        					rigor = (rigor + ((rar1 + rar2) / 10)) / div;
        			}
        			
        			Metric.addAssessValue(context, rigor, "Rigor and Relevance", 2, itemID);
        		}
        		if ((request.getParameter("vid1") != null) && (request.getParameter("vid2") != null))
        		{
        			double vid1 = Double.valueOf(request.getParameter("vid1")).doubleValue();
        			double vid2 = Double.valueOf(request.getParameter("vid2")).doubleValue();
        			
        			if (visual == 0.0)
        				visual = (vid1 + vid2) / 2;
        			else{
        				if (estWghtDb > estWght)
        					div = estWghtDb - estWght;
        				else if (estWghtDb < estWght)
        					div = estWght - estWghtDb;
        				else
        					div = 2;
        				
        				if (div == 1)
        					visual = (visual + ((vid1 + vid2) / 2)) / 2;
        				else
        					visual = (visual + ((vid1 + vid2) / 2)) / div;
        			}
        			
        			Metric.addAssessValue(context, visual, "Visual Design", 2, itemID);
        		}
        		
        		request.setAttribute("item", item);
        		
            	JSPManager.showJSP(request, response, "/tools/success-page.jsp");
        			
        	break;
        
        }   
    }
	
	private boolean validateExpertWeights(Context context, HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException
	{
		boolean ok = false;
		int expWeight = 0;
		int totWeight = 0;
		
        // Expert weightings 
        if (request.getParameter("exp_Content") != null)
		{
			expWeight = Integer.valueOf(request.getParameter("exp_Content")).intValue();
			if (expWeight >= 1 && expWeight <= 5)
				totWeight += expWeight;
			else
				totWeight = -500;
		}
        if (request.getParameter("exp_Educational") != null)
        {
        	expWeight = Integer.valueOf(request.getParameter("exp_Educational")).intValue();
        	if (expWeight >= 1 && expWeight <= 5)
				totWeight += expWeight;
			else
				totWeight = -500;
        }
        if (request.getParameter("exp_Esthetic") != null)
        {
        	expWeight = Integer.valueOf(request.getParameter("exp_Esthetic")).intValue();
        	if (expWeight >= 1 && expWeight <= 5)
				totWeight += expWeight;
			else
				totWeight = -500;
        }	
        if (request.getParameter("exp_Functional") != null)
        {
        	expWeight = Integer.valueOf(request.getParameter("exp_Functional")).intValue();
        	if (expWeight >= 1 && expWeight <= 5)
				totWeight += expWeight;
			else
				totWeight = -500;
        }	
        if (request.getParameter("exp_Metadata") != null)
        {
        	expWeight = Integer.valueOf(request.getParameter("exp_Metadata")).intValue();
        	if (expWeight >= 1 && expWeight <= 5)
				totWeight += expWeight;
			else
				totWeight = -500;
        }
		
        // Validation for total dimension weightings
        if(totWeight > 0)
        	ok = true;
        
		return ok;		
	}
	
}