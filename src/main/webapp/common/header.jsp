<%@ page import="by.bsu.fpmi.controller.AccessController" %>
<%@ page import="by.bsu.fpmi.util.Constants" %>
<%@ page import="by.bsu.fpmi.dao.UserDao" %>
<%@ page import="by.bsu.fpmi.dao.UserDaoImpl" %>
<%@ page import="by.bsu.fpmi.entity.User" %>
<%@ page isELIgnored="false" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    int user_id = AccessController.getCurrentUserID(request);
    UserDao userDao = new UserDaoImpl();
    User user = userDao.getUserById(user_id);
    pageContext.setAttribute("logged_in", user != null, PageContext.PAGE_SCOPE);
    pageContext.setAttribute("user", user, PageContext.PAGE_SCOPE);
%>

<!DOCTYPE HTML>
<html>
<head>
    <title>Title</title>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <!-- Latest compiled JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
<%--nav goes here--%>
    <header class="navbar navbar-inverse">
        <div class="container">
            <div class="navbar-header">
                <a class="navbar-brand" href="/">Cool Events</a>
            </div>
            <ul class="nav navbar-nav navbar-right">
                <c:choose>
                    <c:when test="${logged_in}">
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                    ${user.login} <span class="caret"></span>
                            </a>
                            <ul class="dropdown-menu">
                                <li><a href="/user">Profile</a></li>
                                <li><a href="/logout">Log out</a></li>
                            </ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li>
                            <a href="/user/new">
                                <span class="glyphicon glyphicon-user" Sign Up></span> Sign up
                            </a>
                        </li>
                        <li>
                            <a href="/login">
                                <span class="glyphicon glyphicon-log-in"></span> Log in
                            </a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </header>
    <div class="container">