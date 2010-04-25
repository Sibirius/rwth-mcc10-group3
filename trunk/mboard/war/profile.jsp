<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="javax.jdo.PersistenceManager" %>

<%@ page import="java.util.List" %>

<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%@ page import="com.rwthmcc103.mboard.PMF" %>
<%@ page import="com.rwthmcc103.mboard.Profile" %>

<html>
	<head>
		<link type="text/css" rel="stylesheet" href="/css/main.css" />
		<title>Profile</title> 
	</head>

	<body>
		<%
  	 		UserService userService = UserServiceFactory.getUserService();
   			User user = userService.getCurrentUser();
   			
   			if (user != null) {
   				PersistenceManager pm = PMF.get().getPersistenceManager();
   				
		    	Profile p = Profile.getProfile(user);
   				
   			 	if (p == null) {
		%>
					<p>No Image uploaded yet.</p>
		<% 			 		
   			 	} else {
		%>   				 
					<p>
						Current profile image:<br>
   			 			<img width="150" height="150" src="/serve?blob-key=<%= p.getImg().getKeyString() %>" />   			 			
   			 		</p>
		<%
					
   			 	}
		%>
		<%
    		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		%>
			<p>Upload new profile image:</p>
			<form action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data"> 
				<input type="file" name="myFile"> <br>
				<input type="submit" value="Submit">
			</form>
		<%
   			} else {
   				//The user is not logged in, should not happen though
   				%>
   				<p>Please <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">log in</a> first!</p>
   				<%
   			}
			%>   				
   			<p> Back to <a href="/">mboard</a> </p>
	</body>
</html>