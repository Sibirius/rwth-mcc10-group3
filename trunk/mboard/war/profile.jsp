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

<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	//TODO: only let registered users in here
%>

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
   				
   				// max one element gotten here
   			    String query = "select from " + Profile.class.getName() + " where user == " + user.getUserId();
   			    List<Profile> messages = (List<Profile>) pm.newQuery(query).execute();
   				
   			 	if (messages.isEmpty()) {
		%>
					<p>No Image uploaded.</p>
		<% 			 		
   			 	} else {
		%>   				 
   			 		<p>Image uploaded.</p>
		<%   			 		
   			 	}
		%>
			<form action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data"> 
				<input type="file" name="myFile"> <br>
				<input type="submit" value="Submit">
			</form>
		<%
   			} else {
   				//TODO: error? should not happen with restrictions.
   			}
		%>
	

	</body>
</html>