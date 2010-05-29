<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AWSTranscode - Upload Success</title>
 <link rel="stylesheet" type="text/css" href="css/style.css" media="screen" />
</head>
<body>
<div id="page">

<h1><a href="./index.jsp">AWS based picture and video uploading</a></h1>

<%
    String message = "";
	String argument = "" + request.getParameter("what");
	if(argument.equals("upload")) message = "Upload successfull";
	if(argument.equals("request")) message = "File Request successfull";
	if(argument.equals("reqExists")) message = "File Request still pending";
	if(argument.equals("delete")) message = "File successful deleted";	
%>

<h2><%= message %></h2>

<p class="uploadlink"><a href="./index.jsp">Back to Gallery</a></p>

</div>
</body>
</html>