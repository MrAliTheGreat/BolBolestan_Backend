<%--
  Created by IntelliJ IDEA.
  User: Ali
  Date: 3/16/2021
  Time: 10:44 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="BolBolCodes.BolBolSystem"  %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Courses</title>
</head>
<body>
<ul>
    <li id="std_id">Student Id: <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentId() %></li>
    <li>
        <a href="/courses">Select Courses</a>
    </li>
    <li>
        <a href="/plan">Submitted plan</a>
    </li>
    <li>
        <a href="/profile">Profile</a>
    </li>
    <li>
        <a href="/logout">Log Out</a>
    </li>
</ul>
</body>
</html>