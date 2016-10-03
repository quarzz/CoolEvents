<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Sign up</title>
</head>
<body>
    <form action="${pageContext.request.contextPath}/user/new" method="post">
        <label for="email">Email:</label>
        <input type="email" id="email" name="email"/>
        <br>
        <label for="first_name">First name:</label>
        <input type="text" id="first_name" name="first_name"/>
        <br>
        <label for="last_name">Last name:</label>
        <input type="text" id="last_name" name="last_name"/>
        <br>
        <label for="password">Password:</label>
        <input type="password" id="password" name="password"/>
        <br>
        <input type="submit">
    </form>
</body>
</html>
