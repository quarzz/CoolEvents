<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${requestScope.user.firstName} profile page</title>
</head>
<body>
    <h2>${requestScope.user.login}</h2>
    <h3>${requestScope.user.firstName} ${requestScope.user.lastName}</h3>
</body>
</html>
