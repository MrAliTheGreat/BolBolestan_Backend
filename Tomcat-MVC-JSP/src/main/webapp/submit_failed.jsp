<%--
  Created by IntelliJ IDEA.
  User: Ali
  Date: 3/17/2021
  Time: 12:32 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Submit Failed</title>
    <style>
        h1 {
            color: rgb(207, 3, 3);
        }
    </style>
</head>
<body>
<a href="/">Home</a>
<h1>
    Error:
</h1>
<br>
<h3>
    <%= request.getAttribute("FinalizeResult") %>
</h3>
</body>
</html>
