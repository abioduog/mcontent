$( document ).ready(function() {
    $(".themed-content .workarea").append("<div class='navigator'><a class='previous-page'></a><span class='current-position'></span><a class='next-page'></a></div>");
    $(".themed-content img").protectImage();

    $(".workarea .navigator .previous-page").click(function(event) {
        if(!$(this).attr("disabled"))
           moveTo( parseInt($(".themed-content .workarea .navigator").attr("current-page")) - 1);
    });
    $(".themed-content .workarea .navigator .next-page").click(function(event) {
        if(!$(this).attr("disabled"))
           moveTo( parseInt($(".themed-content .workarea .navigator").attr("current-page")) + 1);
    });
    moveTo(0);
});

function moveTo(index) {
    var imgs = $(".themed-content .workarea img");
    imgs.hide();
    $(imgs[index]).show();

    $(".themed-content .workarea .navigator .previous-page").removeAttr("disabled");
    $(".themed-content .workarea .navigator .next-page").removeAttr("disabled");

    if(index <= 0) {
        $(".themed-content .workarea .navigator .previous-page").attr("disabled", "disabled");
    }
    if(index >= imgs.length - 1) {        
        $(".themed-content .workarea .navigator .next-page").attr("disabled", "disabled");
    }
    
    $(".themed-content .workarea .navigator .current-position").text("Page " + (index+1) + " of " + imgs.length)
    $(".themed-content .workarea .navigator").attr("current-page", index);
}