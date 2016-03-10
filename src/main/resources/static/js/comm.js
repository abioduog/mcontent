/* global mContent */
mContent.ajax.get = function (url, success, error) {
    $.ajax({
        type: "GET",
        url: url,
        success: success,
        error: error
    });
}

mContent.ajax.submit = function (form, success, error) {
    form.submit(function (event) {
        event.preventDefault();
        $.ajax({
            type: form.attr('method'),
            url: form.attr('action'),
            data: form.serialize(),
            success: success,
            error: error
        });
        return false;
    });
    form.submit();
}