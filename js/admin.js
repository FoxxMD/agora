var users;

$(document).ready(function(){

    $.ajax({
        url: "php/users.php",
        data: {mode: "get"},
        type:"POST",
        dataType:"json"
    }).done(function(data){
            users = data;
            var html = $("#users").html();
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
                html = html + "<tr><td>" + c["email"] + "</td><td>" + c["alias"] + "</td><td>" + c["password"] + "</td><td>" + pay
                    + "</td><td>" + enter+ "</td><td>" + c["steam"] + "</td><td>" + c["bn"] + "</td><td>" + c["lol"]
                    + "</td><td><input type='button' class='pay' id='pay" + i + "' value='toggle payment'/></td><td><input type='button' class='enter' id='enter" + i + "' value='toggle entrance'/></td>" +
                    "<td><input type='button' class='delete' id='delete" + i + "' value='delete account'/></td></tr>";
            }
            $("#users").html(html);

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
                        location.reload();
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
                        location.reload();
                    })
            });
        });

});