<%@ include file="../common/header.jsp" %>

    <h2>${requestScope.event.title}</h2>
    <p>
        ${requestScope.event.description}
    </p>
    <p>
        <fmt:formatDate value="${requestScope.event.date}" type="date"/>
    </p>

    <c:if test="${requestScope.currentUserEvent}">
        <a class="btn btn-warning" href="/event/?id=${requestScope.event.id}&action=edit">Edit</a>
        <a class="btn btn-danger" href="/event/?id=${requestScope.event.id}&action=delete">delete</a>
    </c:if>

<%@ include file="../common/footer.jsp" %>