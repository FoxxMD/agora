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
        scope:{
            onsite:'='
        },
        controllerAs: 'registerCtrl',
        controller: /*@ngInject*/ ["$scope", function ($scope) {
            var that = this;
            $scope.tryRegister = function () {
                $scope.registerLoading = true;
                $scope.$broadcast('show-errors-check-validity');
                if ($scope.registerForm.$valid) {
                    if($scope.onsite){
                        $scope.registerformData.noconfirm = true;
                    }
                   Account.register($scope.registerformData.handle, $scope.registerformData.email, $scope.registerformData.password, $stateParams.eventId, $scope.registerformData.noconfirm).promise.then(
                        function (response) {
                            if($scope.onsite){
                                $scope.registerformData = {};
                                $scope.$broadcast('show-errors-reset');
                                $scope.$emit('notify', 'notice', "Registration successful!", 6000);
                            }
                            else{
                                $scope.$parent.sidebar.registerVisible = false;
                                $scope.$emit('notify', 'notice', "Registration successful! please check your email for a confirmation link to activate your account.", 6000);
                                $scope.registerformData = {};
                                $scope.$broadcast('show-errors-reset');
                                $rootScope.toggleMenu();
                            }
                        }).finally(function(){
                           $scope.registerLoading = false;
                       });
                }
            }
        }]
    }
}
register.$inject = ["Account", "$stateParams", "$rootScope"];
