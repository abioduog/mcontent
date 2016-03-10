/* global mContent */
mContent.alert.default = function(type, title, message, timeout) {
    var alertDiv = $("<div>");

    alertDiv.addClass("alert");
    alertDiv.addClass("alert-" + type);
    alertDiv.addClass("fade");

    alertDiv.append($("<strong>").text(title));
    alertDiv.append($("<p>").text(message));

    $("body").append(alertDiv);

    if(timeout > 0) {
        setTimeout(function() {alertDiv.remove();}, timeout);
    } else {
        var alertModal = $("<div>");
        alertModal.addClass("alert-modal");
        alertModal.addClass("fade");

        $("body").append(alertModal);

        alertModal.click(function() {
            alertDiv.removeClass("in");
            alertModal.removeClass("in");
            alertDiv.remove();
            alertModal.remove();
        });
        alertModal.addClass("in");
    }
    alertDiv.addClass("in");
}

mContent.alert.success = function (message) {
    mContent.alert.default("success", "", message, 1500);
}

mContent.alert.info = function (message) {
    mContent.alert.default("info", "", message, 0);
}

mContent.alert.warning = function (title, message) {
    mContent.alert.default("warning", title, message, 3000);
}

mContent.alert.error = function (title, message) {
    mContent.alert.default("danger", title, message, 0);
}
