<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
 <link rel="stylesheet" type="text/css" href="css/style.css" media="screen" />
<script type="text/javascript" src="js/swfobject.js"></script>
</head>
<body>
<div id="page">

<h1><a href="./index.jsp">AWS based picture and video uploading</a></h1>

<p class="uploadlink"><a href="./index.jsp">Back to Gallery</a></p>

<p>URL: <%= request.getParameter("fileURL") %></p>

<div id='mediaspace' style="text-align: center;">This text will be replaced</div>

<script type='text/javascript'>
  var so = new SWFObject('player.swf','ply','720','576','9','#ffffff');
  so.addParam('allowfullscreen','true');
  so.addParam('allowscriptaccess','always');
  so.addParam('wmode','opaque');
  so.addVariable('file','bunny.flv');
  so.addVariable('streamer','rtmp://edge01.fms.dutchview.nl/botr');
  so.write('mediaspace');
</script>


</div>
</body>
</html>