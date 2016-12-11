<%--
  Created by IntelliJ IDEA.
  User: oli
  Date: 04.12.2016
  Time: 21:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
</head>
<body>
    <form action="/login" method="post">
        <p>
            <label for="login">Email:</label>
            <input type="email" id="login" name="login">
        </p>

        <p>
            <label for="password">Password:</label>
            <input type="password" id="password" name="password">
        </p>
        <p>
            <input type="hidden" name="stage" value="1">
            <input type="submit" value="log in">
        </p>
    </form>
</body>
</html>
