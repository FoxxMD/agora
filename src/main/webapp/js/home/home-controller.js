angular.module('gtfest')
    .controller('HomeController', HomeController);

// @ngInject
function HomeController(Restangular){
    var d = new Date();
    var team = {
        "name":"a team",
        "createdDate": d.getTime()

    };
    Restangular.all('teams').post(team).then(function(zeteam)
    {
        console.log(zeteam);
    })
}


