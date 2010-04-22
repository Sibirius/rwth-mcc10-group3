<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<body>
    <form action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">
        <input type="file" name="myFile">
        <input type="submit" value="Submit">
    </form>
</body>