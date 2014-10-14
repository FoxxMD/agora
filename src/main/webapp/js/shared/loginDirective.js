/**
 * Created by Matthew on 8/11/2014.
 */
angular.module('gtfest')
    .directive('login', login);

// @ngInject
function login(Account, $rootScope) {
    return {
        templateUrl: '/views/shared/login.html',
        restrict: 'E',
        scope:'true',
        controllerAs: 'loginCon',
        controller: /*@ngInject*/ ["$scope", function ($scope) {
            $scope.tryLogin = function () {
                $scope.$broadcast('show-errors-check-validity');
                if ($scope.loginForm.$valid) {
                    $scope.loginLoading = true;
                    Account.login($scope.loginformData.email, $scope.loginformData.password).promise.then(
                        function () {
                            Account.initUser();
                            $scope.$broadcast('show-errors-reset');
                            $scope.loginformData = {};
                            $rootScope.toggleMenu();
                        }, function (response) {
                            $scope.$broadcast('notify','error',response.data, 4000);
                        }).finally(function(){
                            $scope.loginLoading = false;
                        });
                }
            };
            $scope.tryForgotPassword = function(form) {
                $scope.$broadcast('show-errors-check-validity');
                if(form.$valid) {
                    $scope.forgotLoading = true;
                    Account.forgotPassword($scope.forgotPasswordEmail).then(function(){
                        $scope.$broadcast('notify','notice', "If there is an email associated with this account an email has been sent to it. Please check your inbox for a link.", 10000);
                        $scope.showForgotForm = false;
                        $rootScope.toggleMenu();
                        $scope.$broadcast('show-errors-reset');
                    }).finally(function(){
                        $scope.forgotLoading = false;
                    });
                }
            };
        }]
    }
}
login.$inject = ["Account", "$rootScope"];
