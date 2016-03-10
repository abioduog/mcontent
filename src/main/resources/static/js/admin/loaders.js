/* global mContent */
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

function loadToWorkAreaVieaUrl(url) {
    $.get(url, function (data) {
        $('#workarea').html(data);
        $('#menu .pure-menu-selected').removeClass('pure-menu-selected');
        $(src).parent('li').addClass('pure-menu-selected');
    });
    return false;
}

function loadToWorkArea(src, event) {
    event.preventDefault();
    $.get($(src).attr('href'), function (data) {
        $('#workarea').html(data);
        $('#menu .pure-menu-selected').removeClass('pure-menu-selected');
        $(src).parent('li').addClass('pure-menu-selected');
    });
    return false;
}

function loadToContentEditorViaUrl(url) {
    $.get( url, function( data ) {
        $('#content-editor').html( data );
    });
    return false;
}

function loadToContentEditor(src, event) {
    event.preventDefault();
    $.get($(src).attr('href'), function (data) {
        $('#content-editor').html(data);
    });
    return false;
}

function loadToPopup(title, url, event) {
    event.preventDefault();
    $.get(url, function (data) {
        var popup = $('<div>').attr('title', title).html(data);
        popup.dialog();
    });
    return false;
}

function closePopup(src, event) {
    event.preventDefault();
    $(src).parents('.ui-dialog-content').dialog('close');
    return false;
}

function submitFormOnPopup(src, event, success, error) {
    event.preventDefault();
    var form = $(src).parents('form');
    form.submit(function (event) {
        event.preventDefault();
        $.ajax({
            type: form.attr('method'),
            url: form.attr('action'),
            data: form.serialize(),
            success: function (data)
            {
                closePopup(src, event);
                if(success) {
                    success(data)
                }
            },
            error: error
        });
        return false;
    });
    form.submit();
    return false;
}