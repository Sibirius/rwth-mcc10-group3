<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.io.*" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AWSTranscode - Logging</title>
 <link rel="stylesheet" type="text/css" href="css/style.css" media="screen" />
</head>
<body>
<div id="page">

<h1><a href="./index.jsp">AWS based picture and video uploading</a></h1>

<%
	String output = "";
	try {
	FileInputStream fstream = new FileInputStream("server.log");
	DataInputStream in = new DataInputStream(fstream);
	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	
	String strLine;

	while ((strLine = br.readLine()) != null)   {
	  // Print the content on the console
	  output = strLine + "\n" + output;
	}

	in.close();
	}catch (Exception e){//Catch exception if any
	  System.err.println("Error: " + e.getMessage());
	}	
%>

<p class="uploadlink"><a href="./index.jsp">Back to Gallery</a></p>

<div style="display: block; text-align: center"><textarea rows="30" cols="120" WRAP="OFF"><%= output %></textarea></div>
</div>
</body>
</html>

