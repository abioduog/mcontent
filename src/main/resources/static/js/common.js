function loadToWorkArea(src, event) {
    event.preventDefault();
    $.get($(src).attr('href'), function (data) {
        $('#workarea').html(data);
        $('#menu .pure-menu-selected').removeClass('pure-menu-selected');
        $(src).parent('li').addClass('pure-menu-selected');
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

function loadToPopup(title, src, event) {
    event.preventDefault();
    $.get($(src).attr('href'), function (data) {
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

function submitFormOnPopup(src, event) {
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
            }
        });
        return false;
    });
    form.submit();
    return false;
}

function callSimpleAction(src, event) {
    event.preventDefault();
    $.ajax({
        type: "GET",
        url: $(src).attr("href"),
        success: function (data)
        {
            window.alert("Success");
        }
    });
    return false;
}
