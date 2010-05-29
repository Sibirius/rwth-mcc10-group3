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
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;

/**
 * Servlet implementation class UploadServlet
 */
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final String TMP = "/tmp/"; 
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */	
    @SuppressWarnings("unchecked")
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
    		        String fileName = item.getName();
    		        String contentType = item.getContentType();  		        
    		        
    		        Boolean isVideo = contentType.startsWith("video");
    		        Boolean isPicture = contentType.startsWith("image"); 
    		        
    		        if( isPicture || isVideo ) {
 	
    		        	String id = ""+UUID.randomUUID();
    		        	String uploadedFileName = id + "_" + fileName;
    		        	File uploadedFile = new File(TMP+uploadedFileName);
	    		        item.write(uploadedFile);
	    		        	    		        
	    		        String cmd;
	    		        if(isVideo){
	    		        	cmd = "sh moviethumb.sh " + TMP + uploadedFileName;
	    		        } else {
	    		        	cmd = "sh imagethumb.sh " + TMP + uploadedFileName;
	    		        }
	    		        
	    		        Process p = Runtime.getRuntime().exec(cmd);
		        	    		        
	    		        if( p.waitFor() == 0){
	    		        	
	    		        	// cut off 3-letter ending if there is one to get the base name 
	    		        	String uploadedFileBaseName = uploadedFileName.contains(".") ? (uploadedFileName.substring(0, uploadedFileName.lastIndexOf('.'))) : uploadedFileName; 
	    		        	String thumbFileName = uploadedFileBaseName + "_thumb.gif";
	    		        	File thumbFile = new File(TMP+thumbFileName);        	
	    		        	
	    		    		PropertiesCredentials pC = new PropertiesCredentials(new File("AwsCredentials.properties"));
	    		        	
	    		            AmazonS3 s3 = new AmazonS3Client(pC);	
	    		            String bucketName = "7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media";
 
	    		            AmazonSimpleDB sdb = new AmazonSimpleDBClient(pC);	    		            
	    		            String myDomain = "mcc10group3media";	    		            

	    		            s3.putObject(new PutObjectRequest(bucketName, uploadedFileName, uploadedFile));
	    		            s3.setObjectAcl(bucketName, uploadedFileName, CannedAccessControlList.PublicRead);	    		            
	    		            s3.putObject(new PutObjectRequest(bucketName, thumbFileName, thumbFile));
	    		            s3.setObjectAcl(bucketName, thumbFileName, CannedAccessControlList.PublicRead);

	    		            List<ReplaceableAttribute> data = new ArrayList<ReplaceableAttribute>();
	    		            
	    		            if(isVideo){
	    		            	String animatedThumbFileName = uploadedFileBaseName+"_thumba.gif";
	    		            	File animatedThumbFile = new File(TMP+animatedThumbFileName);
		    		            s3.putObject(new PutObjectRequest(bucketName, animatedThumbFileName, animatedThumbFile));	
		    		            
		    		            data.add(new ReplaceableAttribute().withName("Type").withValue("Video"));
		    		            data.add(new ReplaceableAttribute().withName("Title").withValue(title));
		    		            data.add(new ReplaceableAttribute().withName("Description").withValue(description));
	            				data.add(new ReplaceableAttribute().withName("Tags").withValue(tags));
	            				data.add(new ReplaceableAttribute().withName("FileName").withValue(uploadedFileName));
	            				data.add(new ReplaceableAttribute().withName("ThumbnailName").withValue(thumbFileName));
	            				data.add(new ReplaceableAttribute().withName("aniThumbnailName").withValue(animatedThumbFileName));
	            				data.add(new ReplaceableAttribute().withName("streamFileName").withValue(""));
	            				data.add(new ReplaceableAttribute().withName("streamFileReq").withValue("0"));
	            				data.add(new ReplaceableAttribute().withName("mobileFileName").withValue(""));		    		                    
	            				data.add(new ReplaceableAttribute().withName("mobileFileReq").withValue("0"));
		    		            
	    		            } else {
	    		            	data.add(new ReplaceableAttribute().withName("Type").withValue("Picture"));
	    		            	data.add(new ReplaceableAttribute().withName("Title").withValue(title));
	    		            	data.add(new ReplaceableAttribute().withName("Description").withValue(description));
	    		            	data.add(new ReplaceableAttribute().withName("Tags").withValue(tags));
	    		            	data.add(new ReplaceableAttribute().withName("FileName").withValue(uploadedFileName));
	    		            	data.add(new ReplaceableAttribute().withName("ThumbnailName").withValue(thumbFileName));	    		            
	    		            }
	    		            
	    		            sdb.putAttributes(new PutAttributesRequest(myDomain, id, data));
	    		            
	    		            response.sendRedirect("./success.jsp?what=upload");
	    		            
	    		        } else {
	    		        	out.println("Error while generating thumbs");
	    		        }
   
    		        } else {
    		        	out.println("Wrong filetype");
    		        } 
    		    }
    		}
    		} catch(Exception e){
    			e.printStackTrace();
    		}
    	}

    }

 
}