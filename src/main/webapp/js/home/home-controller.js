angular.module('gtfest')
  .controller('HomeController', ['$scope','Restangular', function ($scope,Restangular) {
    Restangular.all('games').getList()
        .then(function(games){
          console.log(games)
        })
  }]);
