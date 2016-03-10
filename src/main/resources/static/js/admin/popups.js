/* global mContent */
mContent.popup.open = function (title, content) {
    var popup = $('<div>').attr('title', title).html(content);
    var width40 = $(window).width() * 0.4; // Default size
    popup.dialog({
        modal: true,
        width: width40,
        close: function(event, ui) {
            $(this).dialog('destroy').remove();
        }
    });
}

mContent.popup.load = function (title, url) {
    $.get(url, function (data) {
        var popup = $('<div>').attr('title', title).html(data);
        var width40 = $(window).width() * 0.4; // Default size
        popup.dialog({
            modal: true,
            width: width40,
            close: function(event, ui) {
                $(this).dialog('destroy').remove();
            }
        });
    });
}

mContent.popup.loadWide = function (title, url) {
    $.get(url, function (data) {
        var popup = $('<div>').attr('title', title).html(data);
        var width75 = $(window).width() * 0.75; // Default size
        popup.dialog({
            modal: true,
            width: width75,
            close: function(event, ui) {
                $(this).dialog('destroy').remove();
            }
        });
    });
}

mContent.popup.close = function (src, event) {
    if(event)
        event.preventDefault();
    $(src).parents('.ui-dialog-content').dialog('destroy').remove();
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