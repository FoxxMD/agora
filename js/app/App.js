//initialize app
var app = angular.module('app', ['ngAnimate', 'ngStorage', 'ngSanitize', 'ui.router', 'ui.bootstrap', 'restangular', 'app.directives', 'app.services', 'angularPayments','xeditable']);

//configure routing
//hydrate all states for application in order to setup site structure
app.config(['$stateProvider', '$urlRouterProvider',
    function ($stateProvider, $urlRouterProvider) {

        $stateProvider
            .state('index', {
                templateUrl: 'templates/shared/skeleton.html',
                abstract: true,
                controller: 'cnc'
            })
            .state('home', {
                templateUrl: 'templates/home.html',
                url: '/',
                parent: 'index'
            })
            .state('schedule', {
                templateUrl: 'templates/schedule.html',
                url: '/schedule',
                parent: 'index'
            })
            .state('servers', {
                templateUrl: 'templates/servers.html',
                url: '/servers',
                parent: 'index'
            })
            .state('about', {
                template: '<div about-dir></div>',
                url: '/about',
                parent: 'index'
            })
            .state('pay', {
                template: '<div stripe-dir></div>',
                url: '/pay',
                parent: 'index'
            })
            .state('games', {
                template: '<div gamesection-dir></div>',
                url: '/games',
                parent: 'index'
            })
            .state('sc2', {
                template: '<div game-dir></div>',
                url: '/sc2',
                data: '/content/games/sc2.json',
                parent: 'games'
            })
            .state('profile', {
                templateUrl:'templates/user.html',
                url:'/profile',
                parent:'index',
                controller:'userctrl'
            })
            .state('user',{
                templateUrl:'templates/user.html',
                url:'/user/:userId',
                controller: 'userctrl'
            })
            .state('404', {
                templateUrl: 'templates/shared/404.html',
                url: '/404',
                parent: 'index'
            });
        //make sure root goes to home state
        $urlRouterProvider.when('', '/');
        //anything else gets 404'd
        $urlRouterProvider.otherwise('404');
    }]);

//configure RESTful provider -- will be using to hydrate domain model
app.config(['RestangularProvider', '$httpProvider', function (RestangularProvider, $httpProvider) {
    RestangularProvider.setBaseUrl('/');
}]);

app.run(function ($rootScope, userService, editableOptions) {
    $rootScope.$on('broadcast', function (e, data) {
        if (undefined == data.with) {
            $rootScope.$broadcast(data.say);
        }
        else {
            $rootScope.$broadcast(data.say, data.with);
        }
    });
    editableOptions.theme = 'bs3';
    userService.initUser();
});


/*

 ============== Controllers ==================

 */

//Site skeleton controller
/*

 Will control site navigation and login/register actions and user notification.
 Basically everything on the menu bar.

 */
app.controller('cnc', ['$scope', '$state', '$modal', '$rootScope', 'userService', function ($scope, $state, $modal, $rootScope, userService) {

        $scope.isLoggedIn = userService.isLoggedIn();
        $scope.alias = userService.getProfile().alias;

        $rootScope.$on('loginChange', function () {
            $scope.isLoggedIn = userService.isLoggedIn();
        });

        $scope.openLogin = function () {

            var modalInstance = $modal.open({
                templateUrl: '/templates/shared/login.html',
                controller: ModalLoginCtrl
            });

            modalInstance.result.then(function (action) {
                if (action == 'register') {
                    $scope.openRegistration();
                }
            })

        }

        var ModalLoginCtrl = function ($scope, $modalInstance) {
            $scope.loginSubmit = function () {
                var that = this;
                userService.login(this.loginData).promise.then(function (response) {
                    $modalInstance.close('logged in');
                }, function (response) {
                    alert('login failed');
                });
            }
            $scope.close = function () {
                $modalInstance.dismiss('canceled');
            }
            $scope.closeToRegister = function () {
                $modalInstance.close('register');
            }
        };

        $scope.logoff = function(){
          userService.logoff();
        };

        $scope.openRegistration = function () {
            var modalInstance = $modal.open({
                templateUrl: '/templates/shared/register.html',
                controller: ModalRegisterCtrl
            });
        }

        $rootScope.$on('openRegistration', function () {
            $scope.openRegistration();
        });

        var ModalRegisterCtrl = function ($scope, $modalInstance) {
            $scope.submitRegistration = function () {
                var that = this;
                userService.register(this.formData).promise.then(function () {
                    if (that.payNow) {
                        $state.go('pay');
                    }
                    $modalInstance.close('registered');
                }, function (response) {
                    alert("Registration failed: " + response.message);
                });
            };
            $scope.close = function () {
                $modalInstance.dismiss('canceled');
            };

        };
    }])
    .controller('userctrl', ['$scope', 'userService', '$stateParams', function ($scope, userService, $stateParams) {

        if($stateParams.userId != null)
        {
            $scope.user = userService.getUser($stateParams.userId);
            $scope.ownProfile = false;
        }
        else{
            $scope.user = userService.getUser(userService.getProfile().id);
            $scope.ownProfile = true;
        }

        $scope.updateUser = function (element, updateVal) {
         return userService.updateUser(element, updateVal);
        }
    }]);