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
                    Account.login($scope.formData.email, $scope.formData.password).promise.then(
                        function () {
                            Account.initUser();
                        }, function (response) {
                            $scope.error = true;
                        });
                }
            }
        }
    }
}