//initialize app
var app = angular.module('app', ['ngAnimate', 'ngStorage', 'ngSanitize', 'ui.router', 'ui.bootstrap', 'app.directives', 'app.services', 'angularPayments', 'xeditable','ngTable', 'angulartics', 'angulartics.google.analytics']);

//configure routing
//hydrate all states for application in order to setup site structure
app.config(['$stateProvider', '$urlRouterProvider','$locationProvider', '$httpProvider',
    function ($stateProvider, $urlRouterProvider, $locationProvider, $httpProvider) {

        $locationProvider.html5Mode(true);

        $stateProvider
            .state('index', {
                templateUrl: '/templates/shared/skeleton.html',
                abstract: true,
                controller: 'cnc'
            })
            .state('home', {
                templateUrl: '/templates/home.html',
                url: '/',
                parent: 'index'
            })
            .state('schedule', {
                templateUrl: '/templates/schedule.html',
                url: '/schedule',
                parent: 'index'
            })
            .state('servers', {
                templateUrl: '/templates/servers.html',
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
                parent: 'index',
                authenticated: true
            })
            .state('games', {
                template: '<div gamesection-dir></div>',
                url: '/games',
                parent: 'index'
            })
            /*
                Game Sections

             */
            .state('sc2', {
                template: '<div game-dir></div>',
                url: '/sc2',
                data: '/content/games/sc2.json',
                parent: 'games'
            })
            .state('csgo', {
                template: '<div game-dir></div>',
                url: '/csgo',
                data: '/content/games/csgo.json',
                parent: 'games'
            })
            .state('dota', {
                template: '<div game-dir></div>',
                url: '/dota',
                data: '/content/games/dota.json',
                parent: 'games'
            })
            .state('halo', {
                template: '<div game-dir></div>',
                url: '/halo',
                data: '/content/games/halo.json',
                parent: 'games'
            })
            .state('lol', {
                template: '<div game-dir></div>',
                url: '/lol',
                data: '/content/games/lol.json',
                parent: 'games'
            })
            .state('mtg', {
                template: '<div game-dir></div>',
                url: '/mtg',
                data: '/content/games/mtg.json',
                parent: 'games'
            })
            .state('mvc', {
                template: '<div game-dir></div>',
                url: '/mvc',
                data: '/content/games/mvc.json',
                parent: 'games'
            })
            .state('poke', {
                template: '<div game-dir></div>',
                url: '/pokemon',
                data: '/content/games/poke.json',
                parent: 'games'
            })
            .state('ptcg', {
                template: '<div game-dir></div>',
                url: '/ptcg',
                data: '/content/games/ptcg.json',
                parent: 'games'
            })
            .state('ssb', {
                template: '<div game-dir></div>',
                url: '/ssb',
                data: '/content/games/ssb.json',
                parent: 'games'
            })
            .state('hs', {
                template: '<div game-dir></div>',
                url: '/hearthstone',
                data: '/content/games/hs.json',
                parent: 'games'
            })
            /*
                End of Game Sections

             */
            .state('profile', {
                templateUrl: '/templates/user.html',
                url: '/profile',
                parent: 'index',
                controller: 'userctrl',
                authenticated: true
            })
            .state('user', {
                templateUrl: '/templates/user.html',
                url: '/user/:userId',
                parent: 'index',
                controller: 'userctrl',
                authenticated: true
            })
            .state('users', {
                templateUrl: '/templates/users.html',
                url: '/users',
                parent: 'index',
                controller: 'usersctrl',
                authenticated: true
            })
            .state('team', {
                template: '<div team-dir></div>',
                url: '/team/:teamId',
                parent: 'index',
                authenticated: true
            })
            .state('teams', {
                templateUrl: '/templates/teams.html',
                url: '/teams',
                parent: 'index',
                controller: 'teamsctrl',
                authenticated: true
            })
            .state('resetPW', {
                templateUrl:'/templates/resetpw.html',
                url:'/resetpw',
                parent:'index',
                controller:'resetctrl'
            })
            .state('forgotPW', {
                templateUrl:'/templates/forgotpw.html',
                url:'/forgotpw/:resetToken',
                parent: 'index',
                controller:'forgotctrl'
            })
            .state('404', {
                templateUrl: '/templates/shared/404.html',
                url: '/404',
                parent: 'index'
            });
        //make sure root goes to home state
        $urlRouterProvider.when('', '/');
        //anything else gets 404'd
        $urlRouterProvider.otherwise('404');
        $httpProvider.interceptors.push('authResponseInterceptor');

    }]);

app.run(function ($rootScope, userService, editableOptions, $state) {

    userService.initUser();

    $rootScope.$on('broadcast', function (e, data) {
        if (undefined == data.with) {
            $rootScope.$broadcast(data.say);
        }
        else {
            $rootScope.$broadcast(data.say, data.with);
        }
    });
    editableOptions.theme = 'bs3';

    $rootScope.$on('$stateChangeStart',
        function (event, toState, toParams, fromState, fromParams) {
            if (toState.authenticated) {
                if (!userService.isLoggedIn()) {
                    event.preventDefault();
                    $state.go('home');
                }
            }
        })

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
        $scope.userService = userService;

        $rootScope.$on('loginChange', function () {
            $scope.alias = userService.getProfile().alias;
            $scope.isLoggedIn = userService.isLoggedIn();
            $scope.paid = userService.getProfile().paid;
        });
        $rootScope.$on('authExpired', function(event, data){
            userService.logoff();
            $rootScope.siteError = "Your login has expired! Please log back in.";
            if($state.current.authenticated)
            {
                $state.go('home');
            }
            //$scope.openLogin();
        });

        $scope.openPay = function () {
            $state.go('pay');
        };

        $scope.stopReminder = function() {
            userService.stopReminder();
        };

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

        };

        var ModalLoginCtrl = function ($scope, $modalInstance) {
            $scope.loginSubmit = function () {
                var that = this;

                userService.login(this.loginData).promise.then(function (response) {
                    $rootScope.siteError = null;
                    $modalInstance.close('logged in');
                }, function (response) {
                    $scope.formErrorMessage = response;
                });
            };
            $scope.goToForgot = function(){
                $state.go('resetPW');
                $modalInstance.dismiss('canceled');
            };
            $scope.close = function () {
                $modalInstance.dismiss('canceled');
            };
            $scope.closeToRegister = function () {
                $modalInstance.close('register');
            }
        };

        $scope.logoff = function () {
            userService.logoff();
            $state.go('home');
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
                    $scope.formErrorMessage = "There was a problem with registration: " + response;
                });
            };
            $scope.close = function () {
                $modalInstance.dismiss('canceled');
            };

        };
    }])
    .controller('userctrl', ['$scope', 'userService', '$stateParams', '$state','$filter', function ($scope, userService, $stateParams, $state, $filter) {

        if(userService.getProfile().justPaid)
        {
            $scope.justPaid = true;
            userService.justPaid(false);
        }
        if(userService.getProfile().alreadyPaid)
        {
            $scope.alreadyPaid = true;
            userService.alreadyPaid(false);
        }

        $scope.paid = [{value: 1, text:"Yes"},
            {value: 0, text:"No"}];
        $scope.entered = [{value: 1, text:"Yes"},
            {value: 0, text:"No"}];

        userService.getUser($stateParams.userId).promise.then(function (userData) {
            $scope.user = userData;

            $scope.showPaid = function() {
                var selected = $filter('filter')($scope.paid, {value: $scope.user.paid});
                return ($scope.user.paid && selected.length) ? selected[0].text : 'Not set';
            };
            $scope.showEntered = function() {
                var selected = $filter('filter')($scope.entered, {value: $scope.user.entered});
                return ($scope.user.entered && selected.length) ? selected[0].text : 'Not set';
            };

        });
        $scope.admin = userService.adminMode() && userService.getProfile().role == 1;
        $scope.ownProfile = ($stateParams.userId == userService.getProfile().id) || $state.current.name == "profile";
        $scope.showPassword = false;

        $scope.openPay = function () {
            $state.go('pay');
        };

        $scope.updateUser = function (element, updateVal) {
            if(updateVal == undefined)
            {
                return "Max length is 20 characters!";
            }
            if($stateParams.userId != undefined)
            {
                return userService.updateUser($stateParams.userId, element, updateVal);
            }
            else{
                return userService.updateUser(userService.getProfile().id , element, updateVal);
            }

        };

        $scope.deleteUser = function(userId) {
            userService.deleteUser(userId).promise.then(function(){
                if(userService.getProfile().id == userId) {
                    userService.logoff();
                    $state.go('home');
                }else{
                    $state.go('users');
                }
            },function(response){
                $scope.userErrorMessage = response;
            });
        };

        $scope.submitPasswordChange = function(){
            var that = this;
            if($scope.admin)
            {
                this.formData.email = $scope.user.email;
            }
            userService.changePassword(this.formData).promise.then(function(response){
                $scope.passwordSuccess = true;
                $scope.userErrorMessage = null;
                that.showPassword = false;
                that.formData.oldPassword = null;
                that.formData.newPassword = null;
                that.formData.passwordConfirm = null;
                that.passwordChangeForm.$setPristine();
            }, function(response){
                $scope.userErrorMessage = "Password change failed: " + response;
            });
        }
    }])
    .controller('usersctrl', ['$scope', 'userService', '$state','ngTableParams','$filter', function ($scope, userService, $state, ngTableParams, $filter) {
        $scope.admin = userService.adminMode() && userService.getProfile().role == 1;

        $scope.paid = [{value: 1, text:"Yes"},
            {value: 0, text:"No"}];
        $scope.entered = [{value: 1, text:"Yes"},
            {value: 0, text:"No"}];

        userService.getUsers().promise.then(function (data) {
            //$scope.users = userArray;

            $scope.tableParams = new ngTableParams({
                page: 1,            // show first page
                count: 10          // count per page
                /*                filter: {
                 alias:''       // initial filter
                 }*/
            }, {
                total: data.length, // length of data
                getData: function($defer, params) {
                    // use build-in angular filter
                    var orderedData = params.filter() ?
                        $filter('filter')(data, params.filter()) :
                        data;

                    $scope.users = orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count());

                    params.total(orderedData.length); // set total for recalc pagination
                    $defer.resolve($scope.users);
                }
            });

        });

        $scope.updateUser = function (userId, element, updateVal) {
            return userService.updateUser(userId, element, updateVal);
        };

        $scope.goToUser = function (id) {
            $state.go('user', {userId: id});
        }
    }])
    .controller('teamsctrl', ['$scope', 'teamService', '$state', '$modal','ngTableParams','$filter', function ($scope, teamService, $state, $modal, ngTableParams, $filter) {

        $scope.goToTeam = function (id) {
            $state.go('team', {teamId: id});
        };

        teamService.getTeams().promise.then(function (data) {
            //$scope.teams = response;

            $scope.tableParams = new ngTableParams({
                page: 1,            // show first page
                count: 10          // count per page
                /*                filter: {
                 alias:''       // initial filter
                 }*/
            }, {
                total: data.length, // length of data
                getData: function($defer, params) {
                    // use build-in angular filter
                    var orderedData = params.filter() ?
                        $filter('filter')(data, params.filter()) :
                        data;

                    $scope.teams = orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count());

                    params.total(orderedData.length); // set total for recalc pagination
                    $defer.resolve($scope.teams);
                }
            });

        }, function (response) {
            alert(response);
        });

        $scope.openCreateTeam = function () {
            var modalInstance = $modal.open({
                templateUrl: '/templates/shared/createTeam.html',
                controller: modalCreateTeamCtrl
            });
        };


        var modalCreateTeamCtrl = function ($scope, $modalInstance) {

            $scope.games = ['CS:GO', 'DOTA 2', 'Starcraft 2', 'Halo 3 2v2', 'Halo 3 3v3', 'League of Legends SR', 'League of Legends ARAM', 'SSB:Brawl', 'SSB:Melee' ];

            $scope.submitTeam = function () {
                var that = this;
                teamService.createTeam(this.formData).promise.then(function () {
                    $modalInstance.close('created');
                }, function (response) {
                    alert("Team creation failed: " + response.message);
                });
            };
            $scope.close = function () {
                $modalInstance.dismiss('canceled');
            };

        };
    }])
    .controller('resetctrl', ['$scope','userService', function($scope,userService){
        $scope.tryResetPassword = function(){
            userService.resetPassword(this.formData).promise.then(function(response){
                $scope.passwordSuccess = true;
            }, function(response){
                alert(response);
            });
        }
    }])
    .controller('forgotctrl', ['$scope','$state','userService','$stateParams','$timeout', function($scope, $state, userService, $stateParams, $timeout){
        $scope.formData = {
            resetToken: $stateParams.resetToken
        };
        $scope.tryForgotPassword = function(){
            userService.changePassword(this.formData).promise.then(function(response){
                $scope.passwordSuccess = true;
                $timeout(function(){
                    $state.go('home');
                },1500);
            }, function(response){
                alert(response);
            });
        }
    }]);

app.factory('authResponseInterceptor', ['$q','$rootScope', function($q, $rootScope){
    return {
        response: function(response) {
            if(response.data != undefined && response.data.success == false)
            {
                if(response.data.message == "authExpire")
                {
                    $rootScope.$broadcast('authExpired');
                }
            }
            return response;
        }
        //can't get this to work...
        /*responseError: function(rejection){
         if(rejection.data.message == "authExpire")
         {
         //userService.logoff();
         alert('Auth has expired, please log back in.');
         //$state.go('home');
         }
         return $q.resolve();
         }*/
    }
}]);