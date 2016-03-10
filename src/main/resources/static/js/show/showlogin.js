$("form[name='show-login-form']").submit(function (event) {
    console.log("HEI!");
    $("input[name='username']").val($("input[name='username']").val().replace(/ /g, '').replace(/^0/i, '234'));
});
