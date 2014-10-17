/**
 * Created by Matthew on 8/15/2014.
 */
angular.module('gtfest')
    .directive('register', register);

// @ngInject
function register(Account, $stateParams, $rootScope) {
    return {
        templateUrl: '/views/shared/register.html',
        restrict: 'E',
        scope:'true',
        controllerAs: 'registerCtrl',
        controller: /*@ngInject*/ ["$scope", function ($scope) {
            var that = this;
            $scope.tryRegister = function () {
                $scope.registerLoading = true;
                $scope.$broadcast('show-errors-check-validity');
                if ($scope.registerForm.$valid) {
                   Account.register($scope.registerformData.handle, $scope.registerformData.email, $scope.registerformData.password, $stateParams.eventId).promise.then(
                        function (response) {
                            $scope.sidebar.registerVisible = false;
                            $scope.$broadcast('notify', 'notice', "Registration successful! please check your email for a confirmation link to activate your account.", 6000);
                            $scope.$broadcast('show-errors-reset');
                            $scope.registerformData = {};
                            $rootScope.toggleMenu();
                        }).finally(function(){
                           $scope.registerLoading = false;
                       });
                }
            }
        }]
    }
}
register.$inject = ["Account", "$stateParams", "$rootScope"];
