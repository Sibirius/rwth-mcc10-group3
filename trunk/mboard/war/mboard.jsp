<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<html>
  <head>
  	<style>
  		#messages {
  			border: 1px solid black;
  			padding: 4px;
  		}
  	</style>
  </head>

  <body>

<%
   	UserService userService = UserServiceFactory.getUserService();
   	User user = userService.getCurrentUser();
   	if (user != null) {
%>
	<p>Logged in as: <%= user.getNickname() %>.
	(<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Log Out</a>)</p>	
<%
   	} else {
%>
	<p>
		(<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Log In</a>)	
	</p>
<%
   	}
%>

	<div id="container">
		<form action="/post" method="post">
    	<div><textarea name="content" rows="3" cols="60"></textarea></div>
    	<div><input type="submit" value="Post Message" /></div>
  		</form>
  		
  		<div id="messages">  			
  		</div>
  	</div>

  </body>
</html>