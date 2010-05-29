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
	String arg = request.getParameter("what");
	String message = "";
	if(arg.equals("upload")) message = "Upload successfull";
	if(arg.equals("request")) message = "File Request successfull";
	if(arg.equals("reqExists")) message = "File Request still pending";
	
%>

<h2><%= message %></h2>

<p class="uploadlink"><a href="./index.jsp">Back to Gallery</a></p>

</div>
</body>
</html>