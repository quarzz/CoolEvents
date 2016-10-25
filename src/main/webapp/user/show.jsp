<%@include file="../common/header.jsp"%>

<%--user/show.jsp--%>

<h2>${requestScope.user.login}</h2>
<h3>${requestScope.user.firstName} ${requestScope.user.lastName}</h3>

<div class="panel-group">
    <c:forEach items="${requestScope.user.myEvents}" var="event">
        <div class="panel panel-primary">
            <a href="/event/?id=${event.id}">
                <div class="panel-heading">
                    ${event.title}
                </div>
            </a>
            <div class="panel-body">
                ${event.description}
                <br>
                ${event.date}
                <br>
                <a href="/user/?id=${event.owner.id}">${event.owner.login}</a>
                <br>
                <a class="btn btn-warning" href="/event/?id=${event.id}&action=edit">Edit</a>
                <a class="btn btn-danger" href="/event/?id=${event.id}&action=delete">delete</a>
            </div>
        </div>
    </c:forEach>
    <c:forEach items="${requestScope.user.readingEvents}" var="event">
        <div class="panel panel-primary">
            <a href="/event/?id=${event.id}">
                <div class="panel-heading">
                        ${event.title}
                </div>
            </a>
            <div class="panel-body">
                    ${event.description}
                <br>
                    ${event.date}
                <br>
                <a href="/user/?id=${event.owner.id}">${event.owner.login}</a>
                <br>
                <a class="btn btn-warning" href="/event/?id=${event.id}&action=edit">Edit</a>
                <a class="btn btn-danger" href="/event/?id=${event.id}&action=delete">delete</a>
            </div>
        </div>
    </c:forEach>
    <br>
    <a class="btn btn-success pull-right" href="/event/new">Add new</a>
</div>

<%@include file="../common/footer.jsp"%>