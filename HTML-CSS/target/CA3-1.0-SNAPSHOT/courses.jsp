<%--
  Created by IntelliJ IDEA.
  User: Ali
  Date: 3/16/2021
  Time: 5:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="BolBolCodes.BolBolSystem"  %>
<%@ page import="BolBolCodes.Student"%>
<%@ page import="BolBolCodes.Offering" %>
<%@ page import="java.util.Objects" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Courses</title>
    <style>
        .course_table {
            width: 100%;
            text-align: center;
        }
        .search_form {
            text-align: center;
        }
    </style>
</head>
<body>
<a href="/">Home</a>
<li id="code">Student Id: <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentId() %></li>
<li id="units">Total Selected Units: <%= BolBolSystem.getBolBolSystemInstance().getOnlineStudent().calculateTotalUnitsSelectedNowOffers() %></li>

<div style = "position:absolute; left:950px; top:90px; background-color:red; font-size: 25px;">
    <%
        String addResult = (String) request.getAttribute("AddResult");
        if(Objects.isNull(addResult)){
            addResult = "";
        }
    %>
    <%= addResult %>
</div>

<br>

<table>
    <tr>
        <th>Code</th>
        <th>Class Code</th>
        <th>Name</th>
        <th>Units</th>
        <th></th>
    </tr>

    <%
        for(Offering offer : BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getSelectedNowOffers().keySet()){
    %>
    <tr>
        <td><%= offer.getCode() %></td>
        <td><%= offer.getClassCode() %></td>
        <td><%= offer.getName() %></td>
        <td><%= offer.getUnits() %></td>
        <td>
            <form action="" method="POST" >
                <input id="form_action" type="hidden" name="action" value="remove">
                <input id="form_course_code" type="hidden" name="course_code" value=<%= offer.getCode() %> >
                <input id="form_class_code" type="hidden" name="class_code" value=<%= offer.getClassCode() %> >
                <button type="submit">Remove</button>
            </form>
        </td>
    </tr>

    <%
        }
    %>

</table>

<br>

<form action="" method="POST">
    <button type="submit" name="action" value="submit">Submit Plan</button>
    <button type="submit" name="action" value="reset">Reset</button>
</form>

<br>

<form class="search_form" action="" method="POST">
    <label>Search:</label>
    <input type="text" name="search" value=<%= BolBolSystem.getBolBolSystemInstance().getSearchName() %> >
    <button type="submit" name="action" value="search">Search</button>
    <button type="submit" name="action" value="clear">Clear Search</button>
</form>



<br>

<table class="course_table">
    <tr>
        <th>Code</th>
        <th>Class Code</th>
        <th>Name</th>
        <th>Units</th>
        <th>Signed Up</th>
        <th>Capacity</th>
        <th>Type</th>
        <th>Days</th>
        <th>Time</th>
        <th>Exam Start</th>
        <th>Exam End</th>
        <th>Prerequisites</th>
        <th></th>
    </tr>
    <%
        for(Offering offering : BolBolSystem.getBolBolSystemInstance().getSearchOffers()){
    %>
    <tr>
        <td><%= offering.getCode() %></td>
        <td><%= offering.getClassCode() %></td>
        <td><%= offering.getName() %></td>
        <td><%= offering.getUnits() %></td>
        <td><%= offering.getNumSignedUp() %></td>
        <td><%= offering.getCapacity() %></td>
        <td><%= offering.getType() %></td>
        <td><%= offering.getDays() %></td>
        <td><%= offering.getTime() %></td>
        <td><%= offering.getExamTimeStart() %></td>
        <td><%= offering.getExamTimeEnd() %></td>
        <td><%= offering.getPrerequisites() %></td>
        <td>
            <form action="" method="POST" >
                <input id="form_action" type="hidden" name="action" value="add">
                <input id="form_class_code" type="hidden" name="course_code" value=<%= offering.getCode() %> >
                <input id="form_class_code" type="hidden" name="class_code" value=<%= offering.getClassCode() %> >
                <button type="submit">Add</button>
            </form>
        </td>
    </tr>
    <%
        }
    %>
</table>
</body>
</html>
