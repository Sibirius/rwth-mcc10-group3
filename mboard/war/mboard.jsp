<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.jdo.PersistenceManager" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.rwthmcc103.mboard.Message" %>
<%@ page import="com.rwthmcc103.mboard.Profile" %>
<%@ page import="com.rwthmcc103.mboard.PMF" %>

<html>
  <head>
  	<style>
  		#messages {
  			border: 1px solid black;
  			padding: 5px;
  		}
  		
  		.message {
  			border: 1px dotted grey;
  			padding-left:  10px;
  			padding-right: 10px;
  		}
  	</style>
  </head>

  <body>

<%
   	UserService userService = UserServiceFactory.getUserService();
   	User user = userService.getCurrentUser();
   	if (user != null) {
   		//TODO: retrieve and display image for logged in user if one was uploaded, else remind to upload
   		// maybe retrive image by user id in ServeImage
%>
	<p>Logged in as: <%= user.getNickname() %>.
	<a href="/profile.jsp">Edit Profile</a> <br>
	(<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Log Out</a>)
	</p>	
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
<%
    PersistenceManager pm = PMF.get().getPersistenceManager();
    String query = "select from " + Message.class.getName() + " order by date asc";
    List<Message> messages = (List<Message>) pm.newQuery(query).execute();
    if (messages.isEmpty()) {
%>
			<p>The board has no messages.</p>
<%
    } else {
        for (Message m : messages) {
%>
    	<div class="message">
<%        	
            if (m.getAuthor() == null) {
%>
			<p>An anonymous person wrote:</p>
<%
            } else {
%>
			<p><b><%= m.getAuthor().getNickname() %></b> wrote:</p>
<%
            }
%>
			<blockquote><%= m.getContent() %></blockquote>
  		</div>			
<%
        }
    }
    pm.close();
%>
  	</div>

  </body>
</html>