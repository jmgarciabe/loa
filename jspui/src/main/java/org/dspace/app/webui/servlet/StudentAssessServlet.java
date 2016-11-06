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
import org.dspace.loa.Metric;

/**
 * Servlet for perform the assessment logic of each of the student layer metrics
 *
 * @author Andres Salazar
 * @version $Revision$
 */
public class StudentAssessServlet extends DSpaceServlet
{
   
  
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
    	
		int itemID = UIUtil.getIntParameter(request,"item_id");
		
        Item item = Item.find(context, itemID);
        
        String handle = HandleManager.findHandle(context, item);
        
        Vector assessParamList = AssessItemServlet.loadAssessParam(context, request, response, itemID);
        
        double relev = 0.0;
        double rel1 = 0.0;
        
        double motiv = 0.0;
        double mot1 = 0.0;
        
        double effect = 0.0;
        double eff1 = 0.0;
        
        double visual = 0.0;
        double vid1 = 0.0;
        double vid2 = 0.0;
        
        double avail = 0.0;
        double ava1 = 0.0;
        
        double use = 0.0;
        double eou1 = 0.0;
        
        double accuracy = 0.0;
        double acc1 = 0.0;
		
		if(assessParamList != null && !assessParamList.isEmpty())
		{
			for (int i = 0;i < assessParamList.size(); i++)
    		{
    			AssessParam assessParam = (AssessParam) assessParamList.elementAt(i);
    			
    			if (assessParam.getMetricValue() != null && assessParam.getLayerID() == 3)
    			{
    				if (request.getParameter("rel1") != null && assessParam.getAssessMetricID() == 15)
    				{
    					rel1 = Double.valueOf(request.getParameter("rel1")).doubleValue();
    					relev = Double.valueOf(assessParam.getMetricValue()).doubleValue();
    					relev = (relev + (rel1 / 5)) / 2;
            			Metric.addAssessValue(context, relev, "Relevance", 3, itemID);
    				}		
            		if (request.getParameter("mot1") != null && assessParam.getAssessMetricID() == 16)
            		{
            			mot1 = Double.valueOf(request.getParameter("mot1")).doubleValue();
    					motiv = Double.valueOf(assessParam.getMetricValue()).doubleValue();
    					motiv = (motiv + (mot1 / 5)) / 2;
            			Metric.addAssessValue(context, motiv, "Motivation", 3, itemID);
            		}
            		if (request.getParameter("eff1") != null && assessParam.getAssessMetricID() == 17)
            		{
            			eff1 = Double.valueOf(request.getParameter("eff1")).doubleValue();
    					effect = Double.valueOf(assessParam.getMetricValue()).doubleValue();
    					effect = (effect + (eff1 / 5)) / 2;
            			Metric.addAssessValue(context, effect, "Effectiveness", 3, itemID);
            		}
            		if (request.getParameter("vid1") != null && request.getParameter("vid2") != null 
            				&& assessParam.getAssessMetricID() == 18)
            		{
            			vid1 = Double.valueOf(request.getParameter("vid1")).doubleValue();
            			vid2 = Double.valueOf(request.getParameter("vid2")).doubleValue();
            			visual = Double.valueOf(assessParam.getMetricValue()).doubleValue();
            			visual = (visual + ((vid1 + vid2) / 10)) / 2;
            			Metric.addAssessValue(context, visual, "Visual Design", 3, itemID);
            		}
            		if (request.getParameter("ava1") != null && assessParam.getAssessMetricID() == 19)
            		{
            			ava1 = Double.valueOf(request.getParameter("ava1")).doubleValue();
    					avail = Double.valueOf(assessParam.getMetricValue()).doubleValue();
    					avail = (avail + ava1) / 2;
            			Metric.addAssessValue(context, avail, "Availability", 3, itemID);
            		}
            		if (request.getParameter("eou1") != null && assessParam.getAssessMetricID() == 20)
            		{
            			eou1 = Double.valueOf(request.getParameter("eou1")).doubleValue();
    					use = Double.valueOf(assessParam.getMetricValue()).doubleValue();
    					use = (use + (eou1 / 5)) / 2;
            			Metric.addAssessValue(context, use, "Ease to use", 3, itemID);
            		}
            		if (request.getParameter("acc1") != null && assessParam.getAssessMetricID() == 21)
            		{
            			acc1 = Double.valueOf(request.getParameter("acc1")).doubleValue();
    					accuracy = Double.valueOf(assessParam.getMetricValue()).doubleValue();
    					accuracy = (accuracy + (acc1 / 5)) / 2;
            			Metric.addAssessValue(context, accuracy, "Accuracy", 3, itemID);
            		}
    			}else{
    				if (request.getParameter("rel1") != null)
    		        {
    					rel1 = Double.valueOf(request.getParameter("rel1")).doubleValue();
    					relev = (rel1 / 5) / 2;
            			Metric.addAssessValue(context, relev, "Relevance", 3, itemID);
    		        }
    				if (request.getParameter("mot1") != null)
    		        {
    					mot1 = Double.valueOf(request.getParameter("mot1")).doubleValue();
    					motiv = (mot1 / 5) / 2;
            			Metric.addAssessValue(context, motiv, "Motivation", 3, itemID);
    		        }
    				if (request.getParameter("eff1") != null)
    		        {
    					eff1 = Double.valueOf(request.getParameter("eff1")).doubleValue();
    					effect = (eff1 / 5) / 2;
            			Metric.addAssessValue(context, effect, "Effectiveness", 3, itemID);
    		        }
    				if (request.getParameter("vid1") != null && request.getParameter("vid2") != null)
    		        {
    					vid1 = Double.valueOf(request.getParameter("vid1")).doubleValue();
            			vid2 = Double.valueOf(request.getParameter("vid2")).doubleValue();
            			visual = ((vid1 + vid2) / 10) / 2;
            			Metric.addAssessValue(context, visual, "Visual Design", 3, itemID);
    		        }
    				if (request.getParameter("ava1") != null)
    		        {
    					ava1 = Double.valueOf(request.getParameter("ava1")).doubleValue();
    					avail = ava1;
            			Metric.addAssessValue(context, avail, "Availability", 3, itemID);
    		        }
    				if (request.getParameter("eou1") != null)
    		        {
    					eou1 = Double.valueOf(request.getParameter("eou1")).doubleValue();
    					use = (eou1 / 5) / 2;
            			Metric.addAssessValue(context, use, "Ease to use", 3, itemID);
    		        }
    				if (request.getParameter("acc1") != null)
    		        {
    					acc1 = Double.valueOf(request.getParameter("acc1")).doubleValue();
    					accuracy = (acc1 / 5) / 2;
            			Metric.addAssessValue(context, accuracy, "Accuracy", 3, itemID);
    		        }		
    			}		
    		}
		}
		
		request.setAttribute("item", item);
		
		JSPManager.showJSP(request, response, "/tools/success-page.jsp");
    }
}