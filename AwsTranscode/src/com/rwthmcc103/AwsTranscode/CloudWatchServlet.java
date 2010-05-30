package com.rwthmcc103.AwsTranscode;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.auth.PropertiesCredentials;

import com.amazonaws.cloudwatch.AmazonCloudWatch;
import com.amazonaws.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.cloudwatch.AmazonCloudWatchException;
import com.amazonaws.cloudwatch.model.Dimension;
import com.amazonaws.cloudwatch.model.ListMetricsRequest;
import com.amazonaws.cloudwatch.model.ListMetricsResponse;
import com.amazonaws.cloudwatch.model.ListMetricsResult;
import com.amazonaws.cloudwatch.model.Metric;
import com.amazonaws.cloudwatch.model.ResponseMetadata;


import java.util.ArrayList;





/**
 * Servlet implementation class DeleteFileServlet
 */
public class CloudWatchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/plain");
        PrintWriter out = response.getWriter();		
		
		        
		AmazonCloudWatch service = new AmazonCloudWatchClient(accessKeyId, secretAccessKey);
		
		ListMetricsRequest requestMetrics = new ListMetricsRequest();
		
		ListMetricsResponse responseMetrics = service.listMetrics(requestMetrics);

         //sample
         System.out.println ("ListMetrics Action Response");
         System.out.println ("=============================================================================");
         System.out.println ();

         System.out.println("    ListMetricsResponse");
         System.out.println();
         if (responseMetrics.isSetListMetricsResult()) {
             System.out.println("        ListMetricsResult");
             System.out.println();
             ListMetricsResult  listMetricsResult = responseMetrics.getListMetricsResult();
             java.util.List<Metric> metricsList = listMetricsResult.getMetrics();
             for (Metric metrics : metricsList) {
                 System.out.println("            Metrics");
                 System.out.println();
                 if (metrics.isSetMeasureName()) {
                     System.out.println("                MeasureName");
                     System.out.println();
                     System.out.println("                    " + metrics.getMeasureName());
                     System.out.println();
                 }
                 java.util.List<Dimension> dimensionsList = metrics.getDimensions();
                 for (Dimension dimensions : dimensionsList) {
                     System.out.println("                Dimensions");
                     System.out.println();
                     if (dimensions.isSetName()) {
                         System.out.println("                    Name");
                         System.out.println();
                         System.out.println("                        " + dimensions.getName());
                         System.out.println();
                     }
                     if (dimensions.isSetValue()) {
                         System.out.println("                    Value");
                         System.out.println();
                         System.out.println("                        " + dimensions.getValue());
                         System.out.println();
                     }
                 }
                 if (metrics.isSetNamespace()) {
                     System.out.println("                Namespace");
                     System.out.println();
                     System.out.println("                    " + metrics.getNamespace());
                     System.out.println();
                 }
             }
             if (listMetricsResult.isSetNextToken()) {
                 System.out.println("            NextToken");
                 System.out.println();
                 System.out.println("                " + listMetricsResult.getNextToken());
                 System.out.println();
             }
         } 
         if (responseMetrics.isSetResponseMetadata()) {
             System.out.println("        ResponseMetadata");
             System.out.println();
             ResponseMetadata  responseMetadata = responseMetrics.getResponseMetadata();
             if (responseMetadata.isSetRequestId()) {
                 System.out.println("            RequestId");
                 System.out.println();
                 System.out.println("                " + responseMetadata.getRequestId());
                 System.out.println();
             }
         } 
         System.out.println();
         
         response.sendRedirect("./success.jsp?what=delete");
     
 
		  
		
	}
}
