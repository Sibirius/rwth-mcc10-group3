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
        	//TODO: proper error, something went wrong
            resp.sendRedirect("/");
        } else {
        	//TODO: check if there already is one for the user, update instead of creating new        	
        	Profile profile = new Profile(user, blobKey);
        	
            PersistenceManager pm = PMF.get().getPersistenceManager();
            try {
                pm.makePersistent(profile);
            } finally {
                pm.close();
            }
        }        
            	    	    	
    	resp.sendRedirect("/mboard.jsp");
	}
}
