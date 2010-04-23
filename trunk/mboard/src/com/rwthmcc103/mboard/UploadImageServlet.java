package com.rwthmcc103.mboard;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class UploadImageServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp) 
    	throws IOException {
    	
    	//TODO: actually do something, like saving stuff
    	
    	resp.sendRedirect("/mboard.jsp");
	}
}
