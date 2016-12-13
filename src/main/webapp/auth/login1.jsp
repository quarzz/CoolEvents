<%@include file="../common/header.jsp"%>

<div class="row">
    <div class="col-md-offset-3 col-md-6">
        <form action="/login" method="post">
            <div class="form-group">
                <label for="login">Email:</label>
                <input type="email" id="login" name="login" class="form-control">
            </div>

            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" class="form-control">
            </div>
            <div class="form-group">
                <input type="hidden" name="stage" value="1">
                <input type="submit" value="Log in" class="btn btn-default">
            </div>
        </form>
    </div>
</div>

<%@include file="../common/footer.jsp"%>