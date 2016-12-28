<%@include file="../common/header.jsp"%>

<%--user/show.jsp--%>

<h2>${requestScope.user.login}</h2>
<h3>${requestScope.user.firstName} ${requestScope.user.lastName}</h3>



<div class="panel-group">
    <c:forEach items="${requestScope.user.events}" var="event" varStatus="status">
        <div class="row">
            <div class="panel panel-default col-md-6 col-md-offset-3">
                <style type="text/css">
                    a.login:link {color: black;}      /* unvisited link */
                    a.login:visited {color: black;}   /* visited link */
                    a.login:hover {color: black;}     /* mouse over link */
                    a.login:active {color: black;}    /* selected link */
                </style>
                    <a class="login" href="/event/?id=${event.id}">
                        <div class="panel-heading">
                            <strong>${event.title}</strong>
                        </div>
                    </a>

                <div class="panel-body">
                        ${event.description}
                    <br>
                        ${event.date}
                    <br>
                    <a href="/user/?id=${event.owner.id}">${event.owner.login}</a>
                    <br>
                    <c:if test="${event.access == 2}">
                        <a class="btn btn-default" href="/event/?id=${event.id}&action=edit">Edit</a>
                        <a class="btn btn-default" href="/event/?id=${event.id}&action=delete">delete</a>
                    </c:if>
                </div>
            </div>
        </div>
        <br>
    </c:forEach>
    <br>
    <div class="row">
        <div class="col-md-6 col-md-offset-3">
            <a class="btn btn-success pull-right" href="/event/new">Add new</a>
        </div>
    </div>
</div>

<%@include file="../common/footer.jsp"%>