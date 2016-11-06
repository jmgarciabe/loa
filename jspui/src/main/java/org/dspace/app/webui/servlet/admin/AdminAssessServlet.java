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
import java.text.DecimalFormat;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dspace.app.webui.servlet.AssessItemServlet;
import org.dspace.app.webui.servlet.DSpaceServlet;
import org.dspace.app.webui.util.AssessResult;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.loa.AssessParam;
import org.dspace.loa.AvailabilityAssess;
import org.dspace.loa.CoherenceAssess;
import org.dspace.loa.CompletenessAssess;
import org.dspace.loa.ConsistencyAssess;
import org.dspace.loa.Dimension;
import org.dspace.loa.Layer;
import org.dspace.loa.Metric;
import org.dspace.loa.ReusabilityAssess;
import org.dspace.loa.VisibilityAssess;

/**
 * Servlet for perform the assessment logic of each of the administrator layer metrics
 *
 * @author Andres Salazar
 * @version $Revision$
 */
public class AdminAssessServlet extends DSpaceServlet
{
	/** assess completed successfully */
    public static boolean ASSESS_SUCCESS = false;
    
    /** success status */
    public static final String SUCCESS_STATUS = "Success";
    
    /** fail status */
    public static final String FAIL_STATUS = "Fail";
    
    /** counter of assessed metrics */
    private int metricsCounter = 0;
    
    /** variable to set results button in frontend */
    private String enableResults = null;
    
    
    /** Logger */
    //private static Logger log = Logger.getLogger(EditCommunitiesServlet.class);

    protected void doDSGet(Context context, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException
    {
    	HttpSession session = request.getSession(false);
    	
    	if(session==null)
    		JSPManager.showInternalError(request, response);
    	
    	Vector adminAvailAssess = (Vector)session.getAttribute("LOA.adminAvailAssess");
    	
    	int itemID = UIUtil.getIntParameter(request,"item_id");
    	
    	String assess2Perform = request.getParameter("admin_assess");
    	
    	int assessSize =  adminAvailAssess.size();
    	
    	Item item = Item.find(context, itemID);
    	
    	String handle = item.getHandle();
    	
    	double result = -1.0;
		
		if (assess2Perform == null)
			JSPManager.showInternalError(request, response);		
		
		if (assess2Perform.equals("Availability"))
		{
			metricsCounter ++;
			result = AvailabilityAssess.perform(item);
			if (result >= 0.0)
			{
				ASSESS_SUCCESS = true;
				request.setAttribute("task_result", new AssessResult("Availability", handle, SUCCESS_STATUS, new DecimalFormat("#.##").format(result) + ". " + AvailabilityAssess.getResults(item), ASSESS_SUCCESS));
				Metric.addAssessValue(context, result, "Availability", 1, itemID);
			}
			else
				request.setAttribute("task_result", new AssessResult("Availability", handle, FAIL_STATUS, null, ASSESS_SUCCESS));	
		}
		if (assess2Perform.equals("Coherence"))
		{
			metricsCounter ++;
			try {
				result = CoherenceAssess.perform(item);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result >= 0.0)
			{
				ASSESS_SUCCESS = true;
				request.setAttribute("task_result", new AssessResult("Coherence", handle, SUCCESS_STATUS, new DecimalFormat("#.##").format(result) + ". " + CoherenceAssess.getResults(item), ASSESS_SUCCESS));
				Metric.addAssessValue(context, result, "Coherence", 1, itemID);
			}
			else
				request.setAttribute("task_result", new AssessResult("Coherence", handle, FAIL_STATUS, null, ASSESS_SUCCESS));
		}
		if (assess2Perform.equals("Completeness"))
		{
			metricsCounter ++;
			result = CompletenessAssess.perform(item);
			if (result >= 0.0)
			{
				ASSESS_SUCCESS = true;		
				request.setAttribute("task_result", new AssessResult("Completeness", handle, SUCCESS_STATUS, new DecimalFormat("#.##").format(result) + ". " + CompletenessAssess.getResults(item), ASSESS_SUCCESS));
				Metric.addAssessValue(context, result, "Completeness", 1, itemID);
			}
			else
				request.setAttribute("task_result", new AssessResult("Completeness", handle, FAIL_STATUS, null, ASSESS_SUCCESS));
		}
		if (assess2Perform.equals("Consistency"))
		{
			metricsCounter ++;
			try {
				result = ConsistencyAssess.perform(item);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (result >= 0.0)
			{
				ASSESS_SUCCESS = true;
				request.setAttribute("task_result", new AssessResult("Consistency", handle, SUCCESS_STATUS, new DecimalFormat("#.##").format(result) + ". " + ConsistencyAssess.getResults(item), ASSESS_SUCCESS));
				Metric.addAssessValue(context, result, "Consistency", 1, itemID);
			}
			else
				request.setAttribute("task_result", new AssessResult("Consistency", handle, FAIL_STATUS, null, ASSESS_SUCCESS));
		}
		if (assess2Perform.equals("Reusability"))
		{
			metricsCounter ++;
			result = ReusabilityAssess.perform(item);
			if (result >= 0.0)
			{
				ASSESS_SUCCESS = true;
				request.setAttribute("task_result", new AssessResult("Reusability", handle, SUCCESS_STATUS, new DecimalFormat("#.##").format(result) + ". " + ReusabilityAssess.getResults(item), ASSESS_SUCCESS));
				Metric.addAssessValue(context, result, "Reusability", 1, itemID);
			}
			else
				request.setAttribute("task_result", new AssessResult("Reusability", handle, FAIL_STATUS, null, ASSESS_SUCCESS));
		}
		if (assess2Perform.equals("Visibility"))
		{
			metricsCounter ++;
			result = VisibilityAssess.perform(context, item);
			if (result >= 0.0)
			{
				ASSESS_SUCCESS = true;
				request.setAttribute("task_result", new AssessResult("Visibility", handle, SUCCESS_STATUS, new DecimalFormat("#.##").format(result) + ". " + VisibilityAssess.getResults(context, item), ASSESS_SUCCESS));
				Metric.addAssessValue(context, result, "Visibility", 1, itemID);
			}
			else
				request.setAttribute("task_result", new AssessResult("Visibility", handle, FAIL_STATUS, null, ASSESS_SUCCESS));
		}
		 
		if (metricsCounter >= assessSize)
			enableResults = "Y";
		else
			enableResults = "N";
				
		session.setAttribute("LOA.adminAvailAssess", adminAvailAssess);
		
		request.setAttribute("enaRstBtn", enableResults);
		request.setAttribute("item", item);
		
		JSPManager.showJSP(request, response, "/tools/admin-assess.jsp");		
    }

	protected void doDSPost(Context context, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException
    {
		HttpSession session = request.getSession(false);
    	
    	if(session==null)
    		JSPManager.showInternalError(request, response);
		
		int itemID = UIUtil.getIntParameter(request,"item_id");
		Item item = Item.find(context, itemID);
		
		double adminIndex, expIndex, stdIndex, totalIndex;
		boolean metricIsNull = false;
		
		Vector assessParamList = AssessItemServlet.loadAssessParam(context, request, response, itemID);
		
		if(assessParamList != null && !assessParamList.isEmpty())
		{
			for (int i = 0;i < assessParamList.size(); i++)
    		{
    			AssessParam assessParam = (AssessParam) assessParamList.elementAt(i);
    			
    			if (assessParam.getMetricValue() == null)
    				metricIsNull = true;
    			else{
    				String layer, dimensionName, metricName, mtrVal = null;
    				double metricValue = 0;
    				Vector results = new Vector();
    				
    				if (assessParam.getLayerID() == 1)
    				{
    					layer = "Administrator";
    					dimensionName = Dimension.findNameByID(context, assessParam.getDimID());
    					metricName = Metric.findNameByID(context, assessParam.getAssessMetricID());
    					metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue() * 100;
    					mtrVal = new DecimalFormat("#.##").format(metricValue);
    					results.addElement(layer + "," + dimensionName + "," + metricName + "," + mtrVal);
    				}
    				
    				if (assessParam.getLayerID() == 2)
    				{
    					layer = "Expert";
    					dimensionName = Dimension.findNameByID(context, assessParam.getDimID());
    					metricName = Metric.findNameByID(context, assessParam.getAssessMetricID());
    					metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue() * 100;
    					mtrVal = new DecimalFormat("#.##").format(metricValue);
    					results.addElement(layer + "," + dimensionName + "," + metricName + "," + mtrVal);
    				}
    				
    				if (assessParam.getLayerID() == 3)
    				{
    					layer = "Student";
    					dimensionName = Dimension.findNameByID(context, assessParam.getDimID());
    					metricName = Metric.findNameByID(context, assessParam.getAssessMetricID());
    					metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue() * 100;
    					mtrVal = new DecimalFormat("#.##").format(metricValue);
    					results.addElement(layer + "," + dimensionName + "," + metricName + "," + mtrVal);
    				}
    				
    				session.setAttribute("LOA.results", results);
    			
    			}
    		}
		}
		
		request.setAttribute("item", item);
		
		if (metricIsNull)
			JSPManager.showJSP(request, response, "/tools/layer-index-error.jsp");
		else{
			adminIndex = calculateLayerIndex(assessParamList, 1);
			expIndex = calculateLayerIndex(assessParamList, 2);
			stdIndex = calculateLayerIndex(assessParamList, 3);
			int indexID = Layer.findIndexByItem(context, itemID);
			if (indexID > 0)
			{
				totalIndex = calculateTotalIndex(context, adminIndex, expIndex, stdIndex, itemID);
				Layer.updateAssessIndexes(context, adminIndex, expIndex, stdIndex, totalIndex, indexID);
			}else{
				totalIndex = calculateTotalIndex(context, adminIndex, expIndex, stdIndex, itemID);
				try {
					Layer.addAssessIndexes(context, itemID, adminIndex, expIndex, stdIndex, totalIndex);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (adminIndex != 0){
				adminIndex = adminIndex * 100;
				String admIndex = new DecimalFormat("#.##").format(adminIndex);
				request.setAttribute("adminIndex", admIndex);
			}
				
			if (expIndex != 0)
			{
				expIndex = expIndex * 100;
				String expertIndex = new DecimalFormat("#.##").format(expIndex);
				request.setAttribute("expIndex", expertIndex);
			}
				
			if (stdIndex != 0)
			{
				stdIndex = stdIndex * 100;
				String stuIndex = new DecimalFormat("#.##").format(stdIndex);
				request.setAttribute("stdIndex", stuIndex);
			}
			
			String totIndex = new DecimalFormat("##.##").format(totalIndex);
			request.setAttribute("totalIndex", totIndex);
			
			JSPManager.showJSP(request, response, "/tools/results-report.jsp");
		}
		
    }

	public double calculateLayerIndex(Vector assessParamList, int layerID)
	{
		double metricValue,dimWght;
		double layerIndex = 0.0;
		
		double cont = 0.0;
		double ctxt = 0.0;
		double edu = 0.0;
		double est = 0.0;
		double fun = 0.0;
		double met = 0.0;
		
		double contSum = 0.0;
		double ctxtSum = 0.0;
		double eduSum = 0.0;
		double estSum = 0.0;
		double funSum = 0.0;
		double metSum = 0.0;
		
		int contCount = 0;
		int ctxtCount = 0;
		int eduCount = 0;
		int estCount = 0;
		int funCount = 0;
		int metCount = 0;
		
		if(assessParamList != null && !assessParamList.isEmpty())
		{
			for (int i = 0;i < assessParamList.size(); i++)
    		{
    			AssessParam assessParam = (AssessParam) assessParamList.elementAt(i);
    			
    			if (assessParam.getLayerID() == layerID)
		    	{
    				if (layerID == 1)
    				{
        				if (assessParam.getDimID() == 2)
        				{
        					ctxtCount ++;
        					metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue();
        					ctxtSum += metricValue;
        					dimWght = Double.valueOf(assessParam.getAdmWeight()).doubleValue() / 100;
        					ctxt = (ctxtSum / ctxtCount) * dimWght;
        				}
        				if (assessParam.getDimID() == 5)
        				{
        					funCount ++;
        					metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue();
        					funSum += metricValue;
        					dimWght = Double.valueOf(assessParam.getAdmWeight()).doubleValue() / 100;
        					fun = (funSum / funCount) * dimWght;
        				}
        				if (assessParam.getDimID() == 6)
        				{
        					metCount ++;
        					metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue();
        					metSum += metricValue;
        					dimWght = Double.valueOf(assessParam.getAdmWeight()).doubleValue() / 100;
        					met = (metSum / metCount) * dimWght;
        				}
        				
        				layerIndex = ctxt + fun + met;
    					
    				}
    				
    				if (layerID == 2)
    				{
    					if (assessParam.getDimID() == 1)
    					{
    						contCount ++;
    						metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue();
    						contSum += metricValue;
    						dimWght = Double.valueOf(assessParam.getAdmWeight()).doubleValue() / 100;
    						cont = (contSum / contCount) * dimWght;
    					}
    					if (assessParam.getDimID() == 3)
    					{
    						eduCount ++;
    						metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue();
    						eduSum += metricValue;
    						dimWght = Double.valueOf(assessParam.getAdmWeight()).doubleValue() / 100;
    						edu = (eduSum / eduCount) * dimWght;
    					}
    					if (assessParam.getDimID() == 4)
    					{
    						estCount ++;
    						metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue();
    						estSum += metricValue;
    						dimWght = Double.valueOf(assessParam.getAdmWeight()).doubleValue() / 100;
    						est = (estSum / estCount) * dimWght;
    					}
    					if (assessParam.getDimID() == 5)
    					{
    						funCount ++;
    						metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue();
    						funSum += metricValue;
    						dimWght = Double.valueOf(assessParam.getAdmWeight()).doubleValue() / 100;
    						fun = (funSum / funCount) * dimWght;
    					}
    					if (assessParam.getDimID() == 6)
    					{
    						metCount ++;
    						metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue();
    						metSum += metricValue;
    						dimWght = Double.valueOf(assessParam.getAdmWeight()).doubleValue() / 100;
    						met = (metSum / metCount) * dimWght;
    					}
    					
    					layerIndex = cont + edu + est + fun + met;
    				
    				}
    				
    				if (layerID == 3)
    				{
    					if (assessParam.getDimID() == 2)
    					{
    						ctxtCount ++;
        					metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue();
        					ctxtSum += metricValue;
        					dimWght = Double.valueOf(assessParam.getAdmWeight()).doubleValue() / 100;
        					ctxt = (ctxtSum / ctxtCount) * dimWght;
    					}
    					if (assessParam.getDimID() == 3)
    					{
    						eduCount ++;
    						metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue();
    						eduSum += metricValue;
    						dimWght = Double.valueOf(assessParam.getAdmWeight()).doubleValue() / 100;
    						edu = (eduSum / eduCount) * dimWght;
    					}
    					if (assessParam.getDimID() == 4)
    					{
    						estCount ++;
    						metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue();
    						estSum += metricValue;
    						dimWght = Double.valueOf(assessParam.getAdmWeight()).doubleValue() / 100;
    						est = (estSum / estCount) * dimWght;	
    					}
    					if (assessParam.getDimID() == 5)
    					{
    						funCount ++;
    						metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue();
    						funSum += metricValue;
    						dimWght = Double.valueOf(assessParam.getAdmWeight()).doubleValue() / 100;
    						fun = (funSum / funCount) * dimWght;
    					}
    					if (assessParam.getDimID() == 6)
    					{
    						metCount ++;
    						metricValue = Double.valueOf(assessParam.getMetricValue()).doubleValue();
    						metSum += metricValue;
    						dimWght = Double.valueOf(assessParam.getAdmWeight()).doubleValue() / 100;
    						met = (metSum / metCount) * dimWght;
    					}
    					
    					layerIndex = ctxt + edu + est + fun + met;
    					
    				}	
		    	}
    		}
		}
		
		return layerIndex;
		
	}
	
	public double calculateTotalIndex(Context context, double adminIndex,
			double expIndex, double stdIndex, int itemID) {
		// TODO Auto-generated method stub
		double totIndex = 0.0;
		
		if (adminIndex > 0 && expIndex > 0 && stdIndex > 0)
			totIndex = (adminIndex * 0.5) + (expIndex * 0.3) + (stdIndex * 0.2);
		else if (adminIndex > 0 && expIndex > 0)
			totIndex = (adminIndex * 0.6) + (expIndex * 0.4);
		else if (adminIndex > 0 && stdIndex > 0)
			totIndex = (adminIndex * 0.7) + (stdIndex * 0.3);
		else if (expIndex > 0 && stdIndex > 0)
			totIndex = (expIndex * 0.6) + (stdIndex * 0.4);
		else if (adminIndex > 0)
			totIndex = adminIndex;
		else if (expIndex > 0)
			totIndex = expIndex;
		else if (stdIndex > 0)
			totIndex = stdIndex;
		
		totIndex = totIndex * 10;
			
		return totIndex;
	}

}
  