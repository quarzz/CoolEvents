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
