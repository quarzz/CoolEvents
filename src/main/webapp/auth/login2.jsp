<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
</head>
<body>
<form action="/login" method="post">
    <p>
        We have sent 6-digits pin code on your email. Please, provide it below.
    </p>
    <p>
        <label for="pin">pin code:</label>
        <input type="number" min="100000" max="999999" id="pin" name="pin">
    </p>
    <p>
        <input type="hidden" name="stage" value="2">
        <input type="submit" value="confirm">
    </p>
</form>
</body>
</html>
