package com.rwthmcc103.AwsTranscode;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
 
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
 
public class UploadServlet extends HttpServlet {
 
	private final String TMP = "/tmp/"; 
	
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	if(ServletFileUpload.isMultipartContent(request)){
    		response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
           
    		try{
    		// Create a factory for disk-based file items
    		DiskFileItemFactory factory = new DiskFileItemFactory();
    		factory.setSizeThreshold(10485760);
    		
    		// Create a new file upload handler
    		ServletFileUpload upload = new ServletFileUpload(factory);

    		// Parse the request
    		List /* FileItem */ items = upload.parseRequest(request);
    		
    		// Process the uploaded items
    		Iterator iter = items.iterator();
  		    
    		String title = "";
		    String description = "";
		    String tags = "";
    		while (iter.hasNext()) {
    		    FileItem item = (FileItem) iter.next();
      		    
    		    if (item.isFormField()) {
    		    	if(item.getFieldName().equals("title")){
    		    		title = item.getString();
    		    	} else if(item.getFieldName().equals("description")){
    		    		description = item.getString();
    		    	} else if(item.getFieldName().equals("tags")){
    		    		tags = item.getString();
    		    	}    		    			        
    		    } else {
    		        String fieldName = item.getFieldName();
    		        String fileName = item.getName();
    		        String contentType = item.getContentType();
    		        long sizeInBytes = item.getSize();
    		        out.println(fieldName + ": " + fileName + ": " + contentType + ": " + sizeInBytes + "\n");       		        
    		        
    		        Boolean isVideo = contentType.startsWith("video");
    		        Boolean isPicture = contentType.startsWith("image"); 
    		        out.println(contentType);
    		        out.println(isVideo);
    		        
    		        if( isPicture || isVideo ) {
 	
    		        	String id = ""+UUID.randomUUID();
    		        	String uploadedFileName = id + "_" + fileName;
    		        	File uploadedFile = new File(TMP+uploadedFileName);
	    		        item.write(uploadedFile);
	    		        	    		        
	    		        String cmd;
	    		        if(isVideo){
	    		        	cmd = "sh /home/stephan/Desktop/moviethumb.sh " + TMP + uploadedFileName;
	    		        } else {
	    		        	cmd = "sh /home/stephan/Desktop/imagethumb.sh " + TMP + uploadedFileName;
	    		        }
	    		        
	    		        Process p = Runtime.getRuntime().exec(cmd);
		        	    		        
	    		        if( p.waitFor() == 0){
	    		        	
	    		        	String uploadedFileBaseName = uploadedFileName.contains(".") ? (uploadedFileName.substring(0, uploadedFileName.lastIndexOf('.'))) : uploadedFileName; 
	    		        	String thumbFileName = uploadedFileBaseName + "_thumb.gif";
	    		        	File thumbFile = new File(TMP+thumbFileName);        	
	    		        	
	    		            AmazonS3 s3 = new AmazonS3Client(new PropertiesCredentials(new File("/home/stephan/Desktop/AwsCredentials.properties")));	
	    		            String bucketName = "7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media";
 
	    		            AmazonSimpleDB sdb = new AmazonSimpleDBClient(new PropertiesCredentials(new File("/home/stephan/Desktop/AwsCredentials.properties")));	    		            
	    		            String myDomain = "mcc10group3media";	    		            

	    		            s3.putObject(new PutObjectRequest(bucketName, uploadedFileName, uploadedFile));
	    		            s3.putObject(new PutObjectRequest(bucketName, thumbFileName, thumbFile));
	    		            
	    		            List<ReplaceableItem> data = new ArrayList<ReplaceableItem>();
	    		            
	    		            if(isVideo){
	    		            	String animatedThumbFileName = uploadedFileBaseName+"_thumba.gif";
	    		            	File animatedThumbFile = new File(TMP+animatedThumbFileName);
		    		            s3.putObject(new PutObjectRequest(bucketName, animatedThumbFileName, animatedThumbFile));	
		    		            
		    		            data.add(new ReplaceableItem().withName(id).withAttributes(
		    		            		new ReplaceableAttribute().withName("Type").withValue("Video"),
		    		                    new ReplaceableAttribute().withName("Title").withValue(title),
		    		                    new ReplaceableAttribute().withName("Description").withValue(description),
		    		                    new ReplaceableAttribute().withName("Tags").withValue(tags),
		    		                    new ReplaceableAttribute().withName("FileName").withValue(uploadedFileName),
		    		                    new ReplaceableAttribute().withName("ThumbnailName").withValue(thumbFileName),
		    		                    new ReplaceableAttribute().withName("aniThumbnailName").withValue(animatedThumbFileName),
		    		                    new ReplaceableAttribute().withName("streamFileName").withValue(""),
		    		                    new ReplaceableAttribute().withName("mobileFileName").withValue("")));
		    		            
	    		            } else {
	    		            	data.add(new ReplaceableItem().withName(id).withAttributes(
		    		            		new ReplaceableAttribute().withName("Type").withValue("Picture"),
		    		                    new ReplaceableAttribute().withName("Title").withValue(title),
		    		                    new ReplaceableAttribute().withName("Description").withValue(description),
		    		                    new ReplaceableAttribute().withName("Tags").withValue(tags),
		    		                    new ReplaceableAttribute().withName("FileName").withValue(uploadedFileName),
		    		                    new ReplaceableAttribute().withName("ThumbnailName").withValue(thumbFileName)));	    		            
	    		            }
	    		            
	    		            sdb.batchPutAttributes(new BatchPutAttributesRequest(myDomain, data));
	    		            
	    		            response.sendRedirect("./index.jsp");
	    		            
	    		        }
   
    		        }
    		    }
    		}
    		} catch(Exception e){
    			e.printStackTrace();
    		}
    	}

    }

 
}