angular.module('gtfest')
    .controller('CNCController', CNCController);

// @ngInject
function CNCController($scope, Restangular, UAccount, $rootScope){
    this.account = UAccount;
}
CNCController.$inject = ["$scope", "Restangular", "Account", "$rootScope"];


