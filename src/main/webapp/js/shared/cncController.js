angular.module('gtfest')
    .controller('CNCController', CNCController);

// @ngInject
function CNCController($scope, Restangular, Account){
    this.account = Account;
}


