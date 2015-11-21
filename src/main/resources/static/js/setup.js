window.mContent = {
    ajax: {},
    loaders: {},
    popup: {},
    notify: null
};
var mContent = window.mContent;

mContent.ajax.get = function(url, success, error) {
    $.ajax({
        type: "GET",
        url: url,
        success: success,
        error: error
    });
}


mContent.ajax.submit = function(form, success, error) {
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


mContent.loaders.workArea = function(url) {
    $.get(url, function (data) {
        $('#workarea').html(data);
    });
    return false;
}

mContent.loaders.contentEditor  = function(url) {
    $.get( url, function( data ) {
        $('#content-editor').html( data );
    });
    return false;
}


mContent.popup.load = function(title, url) {
    $.get(url, function (data) {
        var popup = $('<div>').attr('title', title).html(data);
        popup.dialog();
    });
}

mContent.popup.close = function(src, event) {
    if(event)
        event.preventDefault();
    $(src).parents('.ui-dialog-content').dialog('close');
    return false;
}

mContent.popup.submit = function(form, success, error) {
    mContent.ajax.submit(form,
        function (data) {
            mContent.popup.close(form);
            if(success) {
                success(data);
            }
        },
        error
    );
}

mContent.notify = function(message, type) {
    switch(type) {
        case "error":
            window.alert("ERROR: " + message);
            break;
        case "warning":
            window.alert("Warning: " + message);
            break;
        default:
            window.alert(message);
            break;
    }
}