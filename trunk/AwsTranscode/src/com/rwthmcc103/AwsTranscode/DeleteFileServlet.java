package com.rwthmcc103.AwsTranscode;

import java.io.*;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.Attribute;


/**
 * Servlet implementation class DeleteFileServlet
 */
public class DeleteFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/plain");
        PrintWriter out = response.getWriter();		
		
		PropertiesCredentials pC = new PropertiesCredentials(new File("AwsCredentials.properties"));
        
		AmazonSimpleDB sdb = new AmazonSimpleDBClient(pC);	    		            
        String myDomain = "mcc10group3media";

        AmazonS3 s3 = new AmazonS3Client(pC);	
        String bucketName = "7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media";        
        
        String fileId = "";      
        
        String argument = "" + request.getParameter("fileID");
        
        if(argument.matches("[a-z0-9-]*")) fileId = argument; // any combination of upper/lower case letters, numbers underscore, dash and dot    
        
        if(fileId != ""){

        	String selectExpression = "select FileName, ThumbnailName, AniThumbnailName, streamFileName, mobileFileName from `" + myDomain + "` where ItemName() = '" + fileId + "'";
        	out.println(selectExpression);
        	SelectRequest selectRequest = new SelectRequest(selectExpression);
            List<Item> items = sdb.select(selectRequest).getItems();
            
            if(items.size() == 1){
        	    
        	    for (Attribute a : items.get(0).getAttributes()) {
        	    	if(a.getValue() != ""){
            	    	s3.deleteObject(bucketName, a.getValue());
        	    	}   	   	
        	    }
        	    
        	    sdb.deleteAttributes(new DeleteAttributesRequest().withDomainName(myDomain).withItemName(fileId));
        	    
	            response.sendRedirect("./success.jsp?what=delete");        	    
        	    
        	} else {
            	out.println("SDB: No such entry!");
            }        		
        } else {
        	out.println("Wrong parameter!");
        }
	}
}
