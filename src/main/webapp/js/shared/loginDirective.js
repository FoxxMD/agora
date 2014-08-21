/**
 * Created by Matthew on 8/11/2014.
 */
angular.module('gtfest')
    .directive('login', login);

// @ngInject
function login(Account) {
    return {
        templateUrl: '/views/shared/login.html',
        restrict: 'E',
        scope:'true',
        controllerAs: 'loginCon',
        controller: function ($scope) {
            $scope.tryLogin = function () {
                $scope.$broadcast('show-errors-check-validity');
                if ($scope.loginForm.$valid) {
                    Account.login($scope.loginformData.email, $scope.loginformData.password).promise.then(
                        function () {
                            Account.initUser();
                            $scope.$broadcast('show-errors-reset');
                            //$scope.error = false;
                            $scope.loginformData = {};
                        }, function (response) {
                            $scope.$broadcast('notify','error',response.data, 4000);
                            //$scope.error = true;
                        });
                }
            }
        }
    }
}