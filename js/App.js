var currUser = "";
var currData = null;

$(document).ready(function () {


    $('#main-menu-right-login').hide();

    $('#logoff').on('click',function(e){
        currUser = "";
        $('.contentContainer').fadeTo(400, 0, function () {
            $('.contentPane').spin();
            $.ajax({
                url: "login.html"
            }).done(function (data) {
                    $('#main-menu-right-login').hide();
                    $('#main-menu-right').show();
                    $('.contentContainer').html(data);
                    $('.contentPane').spin(false);
                    $('.contentContainer').fadeTo(400, 1, function () {
                    });
                });
        });
    })

    $('#main-menu-left.nav a ').on('click', function (e) {
        $('.contentContainer').fadeTo(400, 0, function () {
            $('.contentPane').spin();
            $.ajax({
                url: $(e.currentTarget).attr('data-link')
            }).done(function (data) {
                $('.contentContainer').html(data);
                $('.contentPane').spin(false);
                $('.contentContainer').fadeTo(400, 1, function () {
                });
            });
        });
    });

    $('#userManagement').on('click', function (e) {
        $('.contentContainer').fadeTo(400, 0, function () {
            $('.contentPane').spin();
            $.ajax({
                url: $(e.currentTarget).attr('data-link')
            }).done(function (data) {
                    $('.contentContainer').html(data);
                    $('.contentPane').spin(false);
                    $('.contentContainer').fadeTo(400, 1, function () {
                    });
                    initUser();
                });
        });
    });

    $('#main-menu-right.nav a ').on('click', function (e) {
        $('.contentContainer').fadeTo(400, 0, function () {
            $('.contentPane').spin();
            $.ajax({
                url: $(e.currentTarget).attr('data-link'),
            }).done(function (data) {
                    $('.contentContainer').html(data);
                    $('.contentPane').spin(false);
                    $('.contentContainer').fadeTo(400, 1, function () {
                    });

                    $('#formPostModal').modal({
                        show: false
                    });

                    $('#signup').validate({
                        rules: {
                            password: {
                                minlength: 5
                            },
                            conpassword: {
                                minlength: 5,
                                equalTo: "#password"
                            }
                        }
                    });
                    $('#signup').on('submit', function (e) {
                        e.preventDefault();
                        if ($('#signup').valid()) {
                            $.ajax({
                                url: 'php/register.php',
                                data: $(e.currentTarget).serialize(),
                                type: 'POST'
                            }).done(function (data) {
                                    if (data == 1)
                                    {
                                        $('#formPostModal .modal-body').text('You are now registered for GT Gamefest! You can now log into your account.')

                                    }

                                    else if (data == 0)
                                        $('#formPostModal .modal-body').text('Registration was not successful! Either your Alias or Email may be duplicated, please try again.');
                                    $('#formPostModal').modal('show');
                                    $('#formPostButton').on('click', function(e){
                                        $('#formPostModal').modal('hide');
                                        e.stopPropagation();
                                    });
                                });
                        }
                    });

                    $('#login').on('submit', function (e) {
                        e.preventDefault();

                        if ($('#login').valid()) {
                            $.ajax({
                                url: 'php/users.php',
                                data: "mode=verify&"+$(e.currentTarget).serialize(),
                                type: 'POST'
                            }).done(function (data) {
                                    if (data == 1)
                                    {

                                        currUser = $("#login #param1").val();
                                        $('#formPostModal .modal-body').text('You have successfully logged into your account! Redirecting page in 3 seconds...');
                                        setTimeout(function(){
                                            $('#formPostModal').modal("hide");
                                            $('#main-menu-right-login').show();
                                            $('#main-menu-right').hide();
                                                $.ajax({
                                                    url: "./users.html"
                                                }).done(function (data) {
                                                        $('.contentContainer').html(data);


                                                        initUser();
                                                    });



                                        },3000);
                                    }

                                    else if (data == 0)
                                        $('#formPostModal .modal-body').text('Your account/password combination is incorrect, please try again');
                                    $('#formPostModal').modal('show');
                                    $('#formPostButton').on('click', function(e){
                                        $('#formPostModal').modal('hide');
                                        e.stopPropagation();
                                    });
                                });
                        }
                    });
                });
        });
    });


});


function initUser() {

    $.ajax({
        url : "php/users.php",
        data : {mode : "get", param1 : currUser},
        dataType : "json",
        type : "post"
    }).done(function(datax){
            currData = datax;

            $("#userinfo #email").html(datax["email"]);
            $("#userinfo #alias").html(datax["alias"]);
            $("#userinfo #password").html(datax["password"]);
            if(datax["paid"]==1) {
                $("#userinfo #paid").html("Paid");

            } else {
                $("#userinfo #paid").html("Not Paid");
                $("#teamformation").prop('disabled',true);
            }
            $("#userinfo #steam").html(datax["steam"]);
            $("#userinfo #bn").html(datax["bn"]);
            $("#userinfo #lol").html(datax["lol"]);
    });

    $("#changeForm #cancel").on('click',function(){
       $("#changeForm").modal('hide');
    });

    $("#userinfo #alias").on('dblclick',function(e){
        $("#changeForm").modal('show');
        $("#changeForm input").attr("placeholder",'New Alias');
        changeUserValue("alias");
    })

    $("#userinfo #password").on('dblclick',function(e){
        $("#changeForm").modal('show');
        $("#changeForm input").attr("placeholder",'New Password');
        changeUserValue("password");
    })

    $("#userinfo #steam").on('dblclick',function(e){
        $("#changeForm").modal('show');
        $("#changeForm input").attr("placeholder",'New Steam Account');
        changeUserValue("steam");
    })

    $("#userinfo #bn").on('dblclick',function(e){
        $("#changeForm").modal('show');
        $("#changeForm input").attr("placeholder",'New Battle.net Account');
        changeUserValue("bn");
    })

    $("#userinfo #lol").on('dblclick',function(e){
        $("#changeForm").modal('show');
        $("#changeForm input").attr("placeholder",'New League of Legends Account');
        changeUserValue("lol");
    })

    $("#userinfo #xbox").on('dblclick',function(e){
        $("#changeForm").modal('show');
        $("#changeForm input").attr("placeholder",'New Xbox Live Account');
        changeUserValue("xbox");
    })

    $("#userinfo #ign").on('dblclick',function(e){
        $("#changeForm").modal('show');
        $("#changeForm input").attr("placeholder",'New Ign Account');
        changeUserValue("ign");
    })
}

function changeUserValue(key) {

    $("#changeForm #confirm").off('click');
    $("#changeForm #confirm").on('click',function(){
        var value = $("#changeForm #value").val();
        $.ajax({
            url : "php/users.php",
            data: {mode: "set", param1: currUser, param2: key, param3: value},
            type : "POST"
        }).done(function(){
                if(key == "alias")
                    currUser = value;
                $("#changeForm").modal('hide');
                initUser();
            })
    })
}
