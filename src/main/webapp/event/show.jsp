<%@ page isELIgnored="false" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${requestScope.event.title}</title>
</head>
<body>
    <h2>${requestScope.event.title}</h2>
    <p>
        ${requestScope.event.description}
    </p>
    <p>
        <fmt:formatDate value="${requestScope.event.date}" type="date"/>
    </p>
</body>
</html>
