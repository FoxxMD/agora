var currUser = "";
var currData = null;
var currRole = 0;
var users = "";

$(document).ready(function () {

    //$('#main-menu-right-login').hide();
    //$('#main-menu-right').hide();

    // checkIfLogged();

    $('#logoff').on('click',function(e){
        currUser = "";

        $.ajax({
            url:"php/users.php",
            data:{mode:"logoff"},
            type:"POST"
        }).done(function(){
                location.reload();
            })

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
        if(currRole == 0) {
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
        } else {
            $('.contentContainer').fadeTo(400, 0, function () {
                $('.contentPane').spin();
                $.ajax({
                    url: "admin.html"
                }).done(function (data) {
                        $('.contentContainer').html(data);
                        $('.contentPane').spin(false);
                        $('.contentContainer').fadeTo(400, 1, function () {
                        });
                        initAdmin();
                    });
            });
        }
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
                                    debugger;
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
                                    } else if(data == 2) {
                                        currUser = $("#login #param1").val();
                                        currRole = 1;
                                        $('#formPostModal .modal-body').text('You have successfully logged into your account! Redirecting page in 3 seconds...');
                                        setTimeout(function(){
                                            $('#formPostModal').modal("hide");
                                            $('#main-menu-right-login').show();
                                            $('#main-menu-right').hide();
                                            $.ajax({
                                                url: "./admin.html"
                                            }).done(function (data) {
                                                    $('.contentContainer').html(data);

                                                    initAdmin();

                                                });



                                        },3000);
                                    }

                                    else if (data == 0)
                                        $('#formPostModal .modal-body').text('Your account/password combination is incorrect, please try again');
                                    else if (data == "-1")
                                        $('#formPostModal .modal-body').text('You have tried too many times, the account is locked until 15 minutes later');

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
            if(datax["paid"]==1) {
                $("#userinfo #paid").html("Paid");

            } else {
                $("#userinfo #paid").html("Not Paid");
                $("#teamformation").prop('disabled',true);
            }
            $("#userinfo #steam").html(datax["steam"]);
            $("#userinfo #bn").html(datax["bn"]);
            $("#userinfo #lol").html(datax["lol"]);
            $("#userinfo #xbox").html(datax["xbox"]);
            $("#userinfo #ign").html(datax["ign"]);
    });

    $("#changeForm #cancel").on('click',function(){
       $("#changeForm").modal('hide');
    });

    /*
    $("#userinfo #password").on('dblclick',function(e){
        $("#changeForm").modal('show');
        $("#changeForm input").attr("placeholder",'New Password');
        $("#confirmPassword").hide();
        changeUserPassword();
    })
    */

    $("#userinfo #steam").on('dblclick',function(e){
        $("#changeForm").modal('show');
        $("#changeForm input").attr("placeholder",'New Steam Account');
        $("#confirmPassword").hide();
        changeUserValue("steam");
    })

    $("#userinfo #bn").on('dblclick',function(e){
        $("#changeForm").modal('show');
        $("#changeForm input").attr("placeholder",'New Battle.net Account');
        $("#confirmPassword").hide();
        changeUserValue("bn");
    })

    $("#userinfo #lol").on('dblclick',function(e){
        $("#changeForm").modal('show');
        $("#changeForm input").attr("placeholder",'New League of Legends Account');
        $("#confirmPassword").hide();
        changeUserValue("lol");
    })

    $("#userinfo #xbox").on('dblclick',function(e){
        $("#changeForm").modal('show');
        $("#changeForm input").attr("placeholder",'New Xbox Live Account');
        $("#confirmPassword").hide();
        changeUserValue("xbox");
    })

    $("#userinfo #ign").on('dblclick',function(e){
        $("#changeForm").modal('show');
        $("#changeForm input").attr("placeholder",'New Ign Account');
        $("#confirmPassword").hide();
        changeUserValue("ign");
    })
}

function changeUserValue(key) {

    $("#changeForm input").val("");
    $("#changeForm input").show();
    $("#changeForm #confirm").off('click');
    $("#changeForm #confirm").on('click',function(){
        var value = $("#changeForm #value").val();
        $.ajax({
            url : "php/users.php",
            data: {mode: "set", param1: currUser, param2: key, param3: value},
            type : "POST"
        }).done(function(){
                $("#changeForm").modal('hide');
                initUser();
            })
    })
}

function changeUserPassword() {
    $("#changeForm input").val("");
    $("#changeForm input").show();
    $("#changeForm #confirm").off('click');
    $("#changeForm #confirm").on('click', function(){
        $("#confirmPassword").show();
        $("#changeForm input").hide();
        $("#changeForm #confirm").off('click');
        $("#changeForm #confirm").on('click',function(){
            var value = $("#changeForm #value").val();
            $.ajax({
                url : "php/users.php",
                data: {mode: "set", param1: currUser, param2: "password", param3: value},
                type : "POST"
            }).done(function(){
                    $("#changeForm").modal('hide');
                    $("#changeForm input").show();
                    initUser();
                })
        });
    });
}


function checkIfLogged() {

    $.ajax({
        url: "php/users.php",
        data: {mode :"check"},
        type: "POST",
        dataType:"json"
    }).done(function(data) {
        if(data["code"] != "0") {
            currUser = data["user"];
            $('#main-menu-right-login').show();
            if(data["code"] == "2")
                currRole = 1;
        } else {
            $('#main-menu-right').show();
        }
    });
}


function initAdmin() {

    $('#adminCancel').on('click',function(){
        $('#adminForm').modal('hide');
    })

    $.ajax({
        url: "php/users.php",
        data: {mode: "get"},
        type:"POST",
        dataType:"json"
    }).done(function(data){
            users = data;
            var html = $("#users").html();
            html = html + "<tbody>";
            for(var i=0;i<data.length;i++) {
                var c = data[i];
                var pay = "";
                var enter = "";
                if(c["paid"] == 1)
                    pay = "Paid";
                else
                    pay = "Not Paid";
                if(c["entered"] == 1)
                    enter = "Entered";
                else
                    enter = "Not Entered";
                html = html + "<tr><td>" + c["email"] + "</td>" +
                    "<td class='change' id='alias_" + i + "'>" + c["alias"] + "</td>" +
                    "<td class='change' id='password_" + i + "'>" + "&#42;&#42;&#42;&#42;&#42;&#42;&#42;&#42;" + "</td>";

                if(c["paid"] == 1)
                    html = html + "<td style='color: #00ff00;'>" + pay + "</td>";
                else
                    html = html + "<td style='color: #ff0000;'>" + pay + "</td>";

                if(c["entered"] == 1)
                    html = html +  "<td style='color: #00ff00;'>" + enter+ "</td>";
                else
                    html = html +  "<td style='color: #ff0000;'>" + enter+ "</td>";

                html = html +
                    "<td class='change' id='steam_" + i + "'>" + c["steam"] + "</td>" +
                    "<td class='change' id='bn_" + i + "'>" + c["bn"] + "</td>" +
                    "<td class='change' id='lol_" + i + "'>" + c["lol"] + "</td>" +
                    "<td class='change' id='xbox_" + i + "'>" + c["xbox"] + "</td>" +
                    "<td class='change' id='ign_" + i + "'>" + c["ign"] + "</td>" +
                    "<td><input type='button' class='pay btn-primary' id='pay" + i + "' value='toggle payment'/></td>" +
                    "<td><input type='button' class='enter btn-primary' id='enter" + i + "' value='toggle entrance'/></td>" +
                    "<td><input type='button' class='delete btn-primary' id='delete" + i + "' value='delete account'/></td></tr>";
            }
            html = html + "</tbody>";
            $("#users").html(html);

            $('.change').on('dblclick', function(e){
                var id = $(e.currentTarget).attr('id');
                var idx = id.indexOf("_");
                var key = id.substring(0, idx);
                var index = id.substring(idx + 1);

                $('#confirmDelete').hide();
                $('#adminForm input').show();
                $("#adminConfirm").html("Change");
                $('#adminForm').modal('show');
                $('#adminConfirm').off('click');
                $('#adminConfirm').on('click',function(e) {
                    var value = $('#adminForm input').val();
                    $.ajax({
                        url: "php/users.php",
                        data : {mode:'set', param1:users[index]["email"],param2:key, param3:value},
                        type: "POST"
                    }).done(function(){
                            $('#adminForm').modal('hide');
                            $.ajax({
                                url: "./admin.html"
                            }).done(function (data) {
                                    $('.contentContainer').html(data);

                                    initAdmin();

                                });
                        })
                });
            });

            $('.pay').on('click',function(e){
                var id = $(e.currentTarget).attr('id');
                var index = parseInt(id.substr(3));
                var pay = 0;
                if(users[index]["paid"] == 0)
                    pay = 1;
                $.ajax({
                    url: "php/users.php",
                    data : {mode:'set', param1:users[index]["email"],param2:'paid', param3:pay},
                    type: "POST"
                }).done(function(){
                        $.ajax({
                            url: "./admin.html"
                        }).done(function (data) {
                                $('.contentContainer').html(data);

                                initAdmin();

                            });
                    })
            });

            $('.enter').on('click',function(e){
                var id = $(e.currentTarget).attr('id');
                var index = parseInt(id.substr(5));
                var enter = 0;
                if(users[index]["entered"] == 0)
                    enter = 1;
                $.ajax({
                    url: "php/users.php",
                    data : {mode:'set', param1:users[index]["email"],param2:'entered', param3:enter},
                    type: "POST"
                }).done(function(){
                        $.ajax({
                            url: "./admin.html"
                        }).done(function (data) {
                                $('.contentContainer').html(data);

                                initAdmin();

                            });
                    })
            });

            $('.delete').on('click',function(e){
                var id = $(e.currentTarget).attr('id');
                var index = parseInt(id.substr(6));

                $("#adminConfirm").html("Delete");
                $("#adminForm").modal('show');
                $("#adminForm input").hide();
                $("#confirmDelete").show();
                $("#adminConfirm").off('click');
                $("#adminConfirm").on('click', function(){
                    $.ajax({
                        url: "php/users.php",
                        data : {mode:'delete', param1:users[index]["email"]},
                        type: "POST"
                    }).done(function(){
                            $("#adminForm").modal('hide');

                            $.ajax({
                                url: "./admin.html"
                            }).done(function (data) {
                                    $('.contentContainer').html(data);

                                    initAdmin();

                                });
                        })
                });
            });
        });
}