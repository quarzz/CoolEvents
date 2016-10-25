<form action="${pageContext.request.contextPath}/event/new" method="post">
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
        <input type="submit">
        <input type="hidden" name="action" value="${requestScope.action}">
    </p>
</form>
