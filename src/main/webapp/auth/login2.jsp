<%@include file="../common/header.jsp"%>

<div class="row">
    <div class="col-md-offset-3 col-md-6">
        <form action="/login" method="post">
            <p>
                We have sent 6-digits pin code on your email. Please, provide it below.
            </p>
            <div class="form-group">
                <label for="pin">pin code:</label>
                <input type="number" min="100000" max="999999" id="pin" name="pin" class="form-control">
            </div>
            <div class="form-group">
                <input type="hidden" name="stage" value="2">
                <input type="submit" value="confirm" class="form-control">
            </div>
        </form>
    </div>
</div>

<%@include file="../common/footer.jsp"%>