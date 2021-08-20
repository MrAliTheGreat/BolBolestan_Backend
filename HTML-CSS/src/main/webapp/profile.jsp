<%@ page import="BolBolCodes.BolBolSystem" %>
<%@ page import="BolBolCodes.Offering" %>
<%@ page import="BolBolCodes.PassedCourse" %><%--
  Created by IntelliJ IDEA.
  User: Ali
  Date: 3/17/2021
  Time: 1:57 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Profile</title>
    <style>
        li {
            padding: 5px
        }
        table{
            width: 10%;
            text-align: center;
        }
    </style>
</head>
<body>
<a href="/">Home</a>
<ul>
    <li id="std_id">Student Id: <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentId() %> </li>
    <li id="first_name">First Name: <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getName() %> </li>
    <li id="last_name">Last Name: <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getSecondName() %> </li>
    <li id="birthdate">Birthdate: <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getBirthDate() %> </li>
    <li id="gpa">GPA: <%= String.format("%.2f" , BolBolSystem.getBolBolSystemInstance().getOnlineStudent().calculateGPA()) %> </li>
    <li id="tpu">Total Passed Units: <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().calculateTotalNumPassedUnits() %> </li>
</ul>
<table>
    <tr>
        <th>Code</th>
        <th>Grade</th>
    </tr>
    <%
        for(PassedCourse passedCourse : BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentPassedCourses()){
    %>
    <tr>
        <td> <%= passedCourse.getCode() %> </td>
        <td> <%= passedCourse.getGrade() %> </td>
        <td> <%= passedCourse.getTerm() %></td>
    </tr>
    <%
        }
    %>
</table>
</body>
</html>
