<%@ include file="../common/header.jsp"%>

<div class="col-md-6 col-md-offset-3">
    <c:forEach items="${requestScope.alerts}" var="alert">
        <div class="alert alert-danger">
            <strong>Error!</strong>${alert}
        </div>
    </c:forEach>
    <form class="form" action="${pageContext.request.contextPath}/user/new" method="post">
        <p class="form-group">
            <label class="control-label" for="email">Email:</label>
            <input class="form-control" type="email" id="email" name="email"/>
        </p>
        <p class="form-group">
            <label class="control-label" for="first_name">First name:</label>
            <input class="form-control" type="text" id="first_name" name="first_name"/>
        </p>
        <p class="form-group">
            <label class="control-label" for="last_name">Last name:</label>
            <input class="form-control" type="text" id="last_name" name="last_name"/>
        </p>
        <p class="form-group">
            <label class="control-label" for="password">Password:</label>
            <input class="form-control" type="password" id="password" name="password"/>
        </p>
        <p class="form-group">
            <button class="btn btn-success" type="submit">Sign up</button>
        </p>
    </form>
</div>

<%@ include file="../common/footer.jsp"%>