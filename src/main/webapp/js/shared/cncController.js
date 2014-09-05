angular.module('gtfest')
    .controller('CNCController', CNCController);

// @ngInject
function CNCController($scope, Restangular, UAccount, $rootScope){
    this.account = UAccount;
    $rootScope.$on('$stateChangeError',
        function(event, toState, toParams, fromState, fromParams, error){
            console.log('state change error: ' + error);
        })
}
CNCController.$inject = ["$scope", "Restangular", "Account", "$rootScope"];


