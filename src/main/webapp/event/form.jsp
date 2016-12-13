<%@ page import="by.bsu.fpmi.entity.User" %>
<%@ page import="java.util.List" %>
<%@ page import="by.bsu.fpmi.entity.Event" %>

<link rel="stylesheet" type="text/css" href="../css/autoexpand.css">
<script src="../js/autoexpand.js"></script>

<c:choose>
    <c:when test="${requestScope.action == 'new'}">
        <c:set var="actionUrl" value="/event/new"/>
    </c:when>
    <c:when test="${requestScope.action == 'edit'}">
        <c:set var="actionUrl" value="/event/?id=${requestScope.event.id}"/>
    </c:when>
</c:choose>
<div class="row">
    <div class="col-md-offset-3 col-md-6">
        <form action="${actionUrl}" method="post">
            <div class="form-group">
                <label for="title">Title:</label>
                <input type="text" id="title" name="title" value="${requestScope.event.title}" class="form-control"/>
            </div>
            <div class="form-group">
                <label for="description">Description:</label>
                <br>
                <textarea rows="1" data-min-rows="1" id="description" name="description" class="form-control auto-expand">${requestScope.event.description}</textarea>
            </div>
            <div class="form-group">
                <label for="date">Date:</label>
                <br>
                <input type="date" id="date" name="date" value="${requestScope.event.date}" class="form-control"/>
            </div>
            <div class="form-group">
                <c:forEach items="${requestScope.allUsers}" var="user">
                    <div class="checkbox">
                        <label>
                            <input type="checkbox" name="allUsers" ${user.selected? 'checked' : ''} value="${user.id}">${user.login}
                        </label>
                    </div>
                </c:forEach>
            </div>
            <div class="form-group">
                <input type="submit" value="${requestScope.action}" class="form-control">
                <input type="hidden" name="action" value="${requestScope.action}">
            </div>
        </form>
    </div>
</div>
