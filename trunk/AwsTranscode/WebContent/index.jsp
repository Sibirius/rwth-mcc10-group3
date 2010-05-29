<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.amazonaws.auth.PropertiesCredentials" %>
<%@ page import="com.amazonaws.services.simpledb.AmazonSimpleDB" %>
<%@ page import="com.amazonaws.services.simpledb.AmazonSimpleDBClient" %>
<%@ page import="java.io.*" %>
<%@ page import="com.amazonaws.services.simpledb.model.SelectRequest" %>
<%@ page import="com.amazonaws.services.simpledb.model.Item" %>
<%@ page import="com.amazonaws.services.simpledb.model.Attribute;" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="css/jquery.lightbox-0.5.css" media="screen" />
<link rel="stylesheet" type="text/css" href="css/style.css" media="screen" />
 <script src='js/jquery-1.4.2.min.js' type="text/javascript"></script>
 <script type="text/javascript" src="js/jquery.lightbox-0.5.js"></script>
 <script type="text/javascript">
$(function() {
	$('a.lb').lightBox();
});
</script>
 <style> </style>
</head>
<body>
<div id="page">

	<h1><a href="./index.jsp">AWS based picture and video uploading</a></h1>

	<p class="uploadlink"><a href="./upload.jsp">Upload new files</a></p>
	
	<%
	AmazonSimpleDB sdb = new AmazonSimpleDBClient(new PropertiesCredentials(new File("/home/stephan/Desktop/AwsCredentials.properties")));
	String myDomain = "mcc10group3media";	 
	%>
	
	<h2>Videos</h2>
	
	<%
	String selectExpression = "select * from `" + myDomain + "` where Type = 'Video'";
	SelectRequest selectRequest = new SelectRequest(selectExpression);
	for (Item item : sdb.select(selectRequest).getItems()) {
	    String itemName = item.getName();
	    String title = "";
	    String description = "";
	    String tags = "";
	    String thumb = "";
	    String aniThumb = "";
	    String streamVid = "";
	    String mobileVid = "";
	    for (Attribute a : item.getAttributes()) {
	    	if(a.getName().equals("Title")) title = a.getValue();
	    	else if(a.getName().equals("Description")) description = a.getValue();
	    	else if(a.getName().equals("Tags")) tags = a.getValue();
	    	else if(a.getName().equals("ThumbnailName")) thumb = a.getValue();
	    	else if(a.getName().equals("aniThumbnailName")) aniThumb = a.getValue();
	    	else if(a.getName().equals("streamFileName")) streamVid = a.getValue();
	    	else if(a.getName().equals("mobileFileName")) mobileVid = a.getValue();	    	
	    }
	    %>
		<div class="media">
		<a href="#"><img src="http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/<%= aniThumb %>" alt="<%= itemName %>" /></a>
		<p class="title"><%= title %></p>
		<p><%= description %></p>
		<p class="tags"><b>Tags:</b> <%= tags %></p>
		<p class="links"><%= (streamVid.equals("") ? "<a href=\"#streamReq\">Stream req</a>" : "<a href=\"./stream.jsp?fileURL="+ streamVid +"\">Stream</a>"  ) %>  | <%= (mobileVid.equals("") ? "<a href=\"#downreq\">Download req</a>" : "<a href=\"d1465bq2op7ksa.cloudfront.net/" + mobileVid + "\">Download req</a>") %></p>
		</div>	    
	    <%
	}
	%>
	
	<hr />
	
	<h2>Bilder</h2>
	
	<%
	selectExpression = "select * from `" + myDomain + "` where Type = 'Picture'";
	selectRequest = new SelectRequest(selectExpression);
	for (Item item : sdb.select(selectRequest).getItems()) {
	    String itemName = item.getName();
	    String title = "";
	    String description = "";
	    String tags = "";
	    String thumb = "";
	    for (Attribute a : item.getAttributes()) {
	    	if(a.getName().equals("Title")) title = a.getValue();
	    	else if(a.getName().equals("Description")) description = a.getValue();
	    	else if(a.getName().equals("Tags")) tags = a.getValue();
	    	else if(a.getName().equals("ThumbnailName")) thumb = a.getValue();  	
	    }
	    %>
		<div class="media">
		<a class="lb" href="200x150.gif"><img src="http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/<%= thumb %>" alt="<%= itemName %>" /></a>
		<p class="title"><%= title %></p>
		<p><%= description %></p>
		<p class="tags"><b>Tags:</b> <%= tags %></p>
		</div>	    
	    <%
	}
	%>	
</div>
</body>
</html>