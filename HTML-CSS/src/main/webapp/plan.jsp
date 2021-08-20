<%@ page import="BolBolCodes.BolBolSystem" %><%--
  Created by IntelliJ IDEA.
  User: Ali
  Date: 3/17/2021
  Time: 12:39 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Plan</title>
    <style>
        table{
            width: 100%;
            text-align: center;

        }
        table, th, td{
            border: 1px solid black;
            border-collapse: collapse;
        }
    </style>
</head>
<body>
<a href="/">Home</a>
<li id="code">Student Id: <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentId() %> </li>
<br>
<table>
    <tr>
        <th></th>
        <th>7:30-9:00</th>
        <th>9:00-10:30</th>
        <th>10:30-12:00</th>
        <th>14:00-15:30</th>
        <th>16:00-17:30</th>
    </tr>
    <tr>
        <td>Saturday</td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Saturday" , "7:30-9:00") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Saturday" , "9:00-10:30") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Saturday" , "10:30-12:00") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Saturday" , "14:00-15:30") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Saturday" , "16:00-17:30") %> </td>
    </tr>
    <tr>
        <td>Sunday</td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Sunday" , "7:30-9:00") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Sunday" , "9:00-10:30") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Sunday" , "10:30-12:00") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Sunday" , "14:00-15:30") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Sunday" , "16:00-17:30") %> </td>
    </tr>
    <tr>
        <td>Monday</td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Monday" , "7:30-9:00") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Monday" , "9:00-10:30") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Monday" , "10:30-12:00") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Monday" , "14:00-15:30") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Monday" , "16:00-17:30") %> </td>
    </tr>
    <tr>
        <td>Tuesday</td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Tuesday" , "7:30-9:00") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Tuesday" , "9:00-10:30") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Tuesday" , "10:30-12:00") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Tuesday" , "14:00-15:30") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Tuesday" , "16:00-17:30") %> </td>
    </tr>
    <tr>
        <td>Wednesday</td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Wednesday" , "7:30-9:00") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Wednesday" , "9:00-10:30") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Wednesday" , "10:30-12:00") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Wednesday" , "14:00-15:30") %> </td>
        <td> <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentCourseNameBasedOnDayAndTime("Wednesday" , "16:00-17:30") %> </td>
    </tr>
</table>
</body>
</html>
