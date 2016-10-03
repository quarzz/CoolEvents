<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>New event</title>
</head>
<body>
    <form action="${pageContext.request.contextPath}/event/new" method="post">
        <p>
            <label for="title">Title:</label>
            <br>
            <input type="text" id="title" name="title"/>
        </p>
        <p>
            <label for="description">Description:</label>
            <br>
            <textarea rows="20" cols="80" id="description" name="description"></textarea>
        </p>
        <p>
            <label for="date">Date:</label>
            <br>
            <input type="date" id="date" name="date"/>
        </p>
        <p>
            <input type="submit">
        </p>
    </form>
</body>
</html>
