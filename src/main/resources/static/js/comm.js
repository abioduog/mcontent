/* global mContent */
mContent.ajax.get = function (url, success, error) {
    $.ajax({
        type: "GET",
        url: url,
        success: success,
        error: error
    });
}

mContent.ajax.submit_instant = function (form, success, error) {
    mContent.ajax.submit_prepare(form, success, error);
    form.submit();
}

mContent.ajax.submit_prepare = function (form, success, error) {
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
}