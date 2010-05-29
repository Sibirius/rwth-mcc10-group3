<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
 <link rel="stylesheet" type="text/css" href="css/style.css" media="screen" />
 <script src='js/jquery-1.4.2.min.js' type="text/javascript"></script>
 <script src='js/jquery.MultiFile.js' type="text/javascript" language="javascript"></script>
  <!--<script type="text/javascript">
 	$(document).ready(function(){
	   $("input").removeAttr('disabled');
	});
 </script>-->
 <style>
 	
 </style>
</head>
<body>

<div id="page">

<h1><a href="./index.jsp">AWS based picture and video uploading</a></h1>

	<p class="uploadlink"><a href="./index.jsp">Back to Gallery</a></p>

<fieldset>
<legend>Upload-Form</legend>

<form class="uploadform" action="./UploadServlet" method="post" enctype="multipart/form-data">
<p>Title:<br /><input type="text" name="title" value="" /></p>
<p>Description:<br /><textarea name="description"></textarea></p>
<p>Tags:<br /><input type="text" name="tags" value="" /> </p>
<p><input type="file" size="15" name="file" class="fileinput multi" accept="gif|jpg|png|jpeg/mp4" maxlength="3" /></p>
<p><input type="submit" name="button" value="Upload" /></p>
</form>

</fieldset>



</div>
</body>
</html>