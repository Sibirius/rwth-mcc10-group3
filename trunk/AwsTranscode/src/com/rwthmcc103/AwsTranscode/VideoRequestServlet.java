package com.rwthmcc103.AwsTranscode;

import java.io.*;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.Attribute;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.simpledb.model.UpdateCondition;


/**
 * Servlet implementation class VideoRequestServlet
 */
public class VideoRequestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/plain");
        //PrintWriter out = response.getWriter();		
		
		PropertiesCredentials pC = new PropertiesCredentials(new File("/home/stephan/Desktop/AwsCredentials.properties"));
        
		AmazonSimpleDB sdb = new AmazonSimpleDBClient(pC);	    		            
        String myDomain = "mcc10group3media";
        
        String selection = "";
        String fileName = "";      
        
        if(request.getParameter("type").equals("stream") ) selection = "streamfileReq";
        if(request.getParameter("type").equals("mobile") ) selection = "mobilefileReq";
        if(request.getParameter("fileName").matches("[a-z0-9-_.]*")) fileName = request.getParameter("fileName");    
        
        if(selection != "" && fileName != ""){
        
	        String selectExpression = "select " + selection + " from `" + myDomain + "` where FileName = '" + fileName + "'";
            SelectRequest selectRequest = new SelectRequest(selectExpression);	
            List<Item> items = sdb.select(selectRequest).getItems();
                        
            if(items.size() == 1){
            	
		        List<Attribute> attributes = items.get(0).getAttributes();
            	
		        if(attributes.size() == 1){
 
    		        String selectedValue = attributes.get(0).getValue();            		

    		        if(selectedValue != "1"){
    		        	
    		            AmazonSQS sqs = new AmazonSQSClient(pC);
    		            String myQueueUrl = "https://queue.amazonaws.com/585894605858/mcc10group3request";
    		            sqs.sendMessage(new SendMessageRequest(myQueueUrl, selection + ":" + fileName));	
    		            
    		            ReplaceableAttribute replaceableAttribute = new ReplaceableAttribute()
                        .withName(selection)
                        .withValue("1")
                        .withReplace(true);
    		            
    		            sdb.putAttributes(new PutAttributesRequest()
                        .withDomainName(myDomain).withExpected(
                        		new UpdateCondition()
                        		.withName("FileName")
                        		.withValue(fileName))
                        .withAttributes(replaceableAttribute));	
    		            
    		            response.sendRedirect("./success.jsp?what=request");
    		            
    		        } else {
    		            response.sendRedirect("./success.jsp?what=reqExists");
    		        }    		          		        
            	}
            }
        }
	}
}
