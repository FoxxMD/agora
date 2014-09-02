/**
 * Created by Matthew on 8/22/2014.
 */
angular.module('gtfest')
    .controller('GlobalController', GlobalController);

// @ngInject
function GlobalController($scope, Account){

    this.account = Account;
    $scope.headerName = "GameFest";
}
GlobalController.$inject = ["$scope", "Account"];
