angular.module('gtfest')
  .controller('HomeController', ['$scope','Restangular', function ($scope,Restangular) {
/*    Restangular.all('games').getList()
         .then(function(games){
         console.log(games)
         })*/
        var d = new Date();
        var team = {
            "name":"a team",
            "createdDate": d.getTime()

        };
        Restangular.all('teams').post(team).then(function(zeteam)
        {
            console.log(zeteam);
        })
  }]);
