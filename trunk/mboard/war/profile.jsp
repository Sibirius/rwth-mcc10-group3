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
					<p>No Image uploaded.</p>
		<% 			 		
   			 	} else {
		%>   				 
   			 		<p>Image uploaded.</p>
   			 		<img width="150" height="150" src="/serve?blob-key=<%= p.getImg().getKeyString() %>" />
		<%   			 		
   			 	}
		%>
		<%
    		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		%>
			<form action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data"> 
				<input type="file" name="myFile"> <br>
				<input type="submit" value="Submit">
			</form>
		<%
   			} else {
   				//The user is not logged in
   				%>
   				<p>Please <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">log in</a> first!</p>
   				<%
   			}
		%>
	

	</body>
</html>