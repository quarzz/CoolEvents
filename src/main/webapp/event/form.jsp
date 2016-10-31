<%@ page import="by.bsu.fpmi.entity.User" %>
<%@ page import="java.util.List" %>
<%@ page import="by.bsu.fpmi.entity.Event" %>
<c:choose>
    <c:when test="${requestScope.action == 'new'}">
        <c:set var="actionUrl" value="/event/new"/>
    </c:when>
    <c:when test="${requestScope.action == 'edit'}">
        <c:set var="actionUrl" value="/event/?id=${requestScope.event.id}"/>
    </c:when>
</c:choose>
<form action="${actionUrl}" method="post">
    <p>
        <label for="title">Title:</label>
        <br>
        <input type="text" id="title" name="title" value="${requestScope.event.title}"/>
    </p>
    <p>
        <label for="description">Description:</label>
        <br>
        <textarea rows="20" cols="80" id="description" name="description">${requestScope.event.description}</textarea>
    </p>
    <p>
        <label for="date">Date:</label>
        <br>
        <input type="date" id="date" name="date" value="${requestScope.event.date}"/>
    </p>
    <p>
        <c:forEach items="${requestScope.allUsers}" var="user">
            <input type="checkbox" name="allUsers" ${user.selected? 'checked' : ''} value="${user.id}">${user.login}<br>
        </c:forEach>
    </p>
    <p>
        <input type="submit">
        <input type="hidden" name="action" value="${requestScope.action}">
    </p>
</form>
