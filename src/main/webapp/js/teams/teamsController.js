/**
 * Created by Matthew on 8/22/2014.
 */
angular.module('gtfest')
    .controller('TeamsController',TeamsController);

// @ngInject
function TeamsController($scope, Account){

    this.account = Account;
    $scope.headerName = "GameFest";
}
