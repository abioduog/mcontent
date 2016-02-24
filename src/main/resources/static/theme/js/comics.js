$( document ).ready(function() {
    $("#workarea").append("<div class='navigator'><a class='previous-page'> << </a><span class='current-position'></span><a class='next-page'> >> </a></div>");
    $("img").protectImage();

    $("#workarea .navigator .previous-page").click(function(event) {
        if(!$(this).attr("disabled"))
           moveTo( parseInt($("#workarea .navigator").attr("current-page")) - 1);
    });
    $("#workarea .navigator .next-page").click(function(event) {
        if(!$(this).attr("disabled"))
           moveTo( parseInt($("#workarea .navigator").attr("current-page")) + 1);
    });
    moveTo(0);
});

function moveTo(index) {
    var imgs = $("#workarea img");
    imgs.hide();
    $(imgs[index]).show();

    $("#workarea .navigator .previous-page").removeAttr("disabled");
    $("#workarea .navigator .next-page").removeAttr("disabled");

    if(index <= 0) {
        $("#workarea .navigator .previous-page").attr("disabled", "disabled");
    }
    if(index >= imgs.length - 1) {        
        $("#workarea .navigator .next-page").attr("disabled", "disabled");
    }
    
    $("#workarea .navigator .current-position").text("Page " + (index+1) + " of " + imgs.length)
    $("#workarea .navigator").attr("current-page", index);
}