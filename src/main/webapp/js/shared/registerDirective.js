/**
 * Created by Matthew on 8/15/2014.
 */
angular.module('gtfest')
    .directive('register', register);

// @ngInject
function register(Account, $stateParams) {
    return {
        templateUrl: '/views/shared/register.html',
        restrict: 'E',
        scope:'true',
        controllerAs: 'registerCtrl',
        controller: function ($scope) {
            $scope.tryRegister = function () {
                $scope.$broadcast('show-errors-check-validity');
                if ($scope.registerForm.$valid) {
                   Account.register($scope.registerformData.email, $scope.registerformData.password, $stateParams.eventId).promise.then(
                        function (response) {
                            $scope.sidebar.registerVisible = false;
                            $scope.$broadcast('notify', 'notice', response, 5000);
                            $scope.$broadcast('show-errors-reset');
                            $scope.error = false;
                            $scope.registerformData = {};
                        });
                }
            }
        }
    }
}