angular.module('gtfest')
    .controller('CNCController', CNCController);

// @ngInject
function CNCController($scope, Restangular, Account, $rootScope){
    this.account = Account;
}


