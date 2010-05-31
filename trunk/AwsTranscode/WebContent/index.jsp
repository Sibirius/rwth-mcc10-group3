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
<title>AWSTranscode - Gallery</title>
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
	PropertiesCredentials pC = new PropertiesCredentials(new File("AwsCredentials.properties"));
	AmazonSimpleDB sdb = new AmazonSimpleDBClient(pC);
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
	    String fileName ="";
	    String thumb = "";
	    String aniThumb = "";
	    String streamVid = "";
	    String mobileVid = "";
	    String streamReq = "";
	    String mobileReq = "";	   
	    
	    for (Attribute a : item.getAttributes()) {
	    	if(a.getName().equals("Title")) title = a.getValue();
	    	else if(a.getName().equals("Description")) description = a.getValue();
	    	else if(a.getName().equals("Tags")) tags = a.getValue();
	    	else if(a.getName().equals("FileName")) fileName = a.getValue();	    	
	    	else if(a.getName().equals("ThumbnailName")) thumb = a.getValue();
	    	else if(a.getName().equals("aniThumbnailName")) aniThumb = a.getValue();
	    	else if(a.getName().equals("streamFileName")) streamVid = a.getValue();
	    	else if(a.getName().equals("streamFileReq")) streamReq = a.getValue();
	    	else if(a.getName().equals("mobileFileName")) mobileVid = a.getValue();	    	
	    	else if(a.getName().equals("mobileFileReq")) mobileReq = a.getValue();	    	
	    }
	    %>
		<div class="media">
		<a href="#" id="<%= itemName %>"><img src="http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/<%= thumb %>" alt="<%= title %>" /></a>
		<script>$("#<%= itemName %>").mouseover(function(){$(this).find("img").attr("src","http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/<%= aniThumb %>");}).mouseout(function(){$(this).find("img").attr("src","http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/<%= thumb %>");});</script>
		<p class="title"><%= title %></p>
		<p><%= description %></p>
		<p class="tags"><b>Tags:</b> <%= tags %></p>
		<p class="links"><a href="./DeleteFileServlet?fileID=<%= itemName %>">Delete</a><br /><%= streamVid.equals("") ? ( streamReq.equals("1") ? "Stream pending" : "<a href=\"./VideoRequestServlet?type=stream&fileName=" + fileName + "\">Request Stream</a>" ) : "<a href=\"./stream.jsp?fileURL="+ streamVid +"\">Stream</a>"  %>  <br /> <%= mobileVid.equals("") ? ( mobileReq.equals("1") ? "Download pending" : "<a href=\"./VideoRequestServlet?type=mobile&fileName=" + fileName + "\">Request Download</a>" ) : "<a href=\"http://d1gielk21ucles.cloudfront.net/" + mobileVid + "\">Download</a>" %></p>
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
	    String fileName = "";
	    String thumb = "";
	    for (Attribute a : item.getAttributes()) {
	    	if(a.getName().equals("Title")) title = a.getValue();
	    	else if(a.getName().equals("Description")) description = a.getValue();
	    	else if(a.getName().equals("Tags")) tags = a.getValue();
	    	else if(a.getName().equals("FileName")) fileName = a.getValue();	    	
	    	else if(a.getName().equals("ThumbnailName")) thumb = a.getValue();  	
	    }
	    %>
		<div class="media">
		<a class="lb" href="http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/<%= fileName %>"><img src="http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/<%= thumb %>" alt="<%= itemName %>" /></a>
		<p class="title"><%= title %></p>
		<p><%= description %></p>
		<p class="tags"><b>Tags:</b> <%= tags %></p>
		<p class="links"><a href="./DeleteFileServlet?fileID=<%= itemName %>">Delete</a></p>		
		</div>	    
	    <%
	}
	%>
	<hr />	
	<p><a href="./logging.jsp">Log</a></p>
</div>
</body>
</html>