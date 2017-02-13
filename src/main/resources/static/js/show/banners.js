function setBanner(location) {
    var url = "adds/random?location=";
    url += location == 1 ? "top" : "bottom";
    var elem = location == 1 ? $('.ad-panel-top') : $('.ad-panel-bottom');
    $.ajax({
        url: url,
        success: function (resp) {
            elem.css('background-image', 'url(data:image/' + resp.image.type + ';base64,' + resp.image.data + ')');
            elem.click(function () {
                window.open(resp.link, '_blank');
            });
        }
    });
}