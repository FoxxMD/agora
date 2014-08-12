angular.module('gtfest')
    .controller('CNCController', CNCController);

// @ngInject
function CNCController(Restangular, Account){

    var d = new Date();
    var team = {
        "name":"a team",
        "createdDate": d.getTime()

    };
/*    Restangular.all('teams').post(team).then(function(zeteam)
    {
        console.log(zeteam);
    })*/
/*    Restangular.all('games').getList().then(function(games){
       console.log(games);
    });*/
}


