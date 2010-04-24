package com.rwthmcc103.mboard;

import java.io.IOException;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.rwthmcc103.mboard.Profile;

@SuppressWarnings("serial")
public class UploadImageServlet extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
    public void doPost(HttpServletRequest req, HttpServletResponse resp) 
    	throws IOException {
    	    
        Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        BlobKey blobKey = blobs.get("myFile");
    	
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        
        if (blobKey == null) {
        	//TODO: proper error message, something went wrong
            resp.setContentType("text/plain");
            resp.getWriter().println("Upload failed.");
        } else {
        	PersistenceManager pm = PMF.get().getPersistenceManager();
            try {
    		    if( Profile.getProfile(user) == null){
    		    	Profile profile = new Profile(user, blobKey);
            	    pm.makePersistent(profile);
    		    }else {
    		    	Profile p = pm.getObjectById(Profile.class,user.getNickname());
    		    	p.setImg(blobKey);
    		    }
            } finally {
                pm.close();
            }
            
        	resp.sendRedirect("/mboard.jsp");
        }                    	    	    	
	}
}
