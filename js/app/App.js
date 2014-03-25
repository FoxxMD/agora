//initialize app
var app = angular.module('app', ['ngAnimate', 'ngStorage', 'ngSanitize', 'ui.router', 'ui.bootstrap', 'restangular', 'app.directives', 'app.services', 'angularPayments', 'xeditable']);

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
                parent: 'index',
                authenticated: true
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
                templateUrl: 'templates/user.html',
                url: '/profile',
                parent: 'index',
                controller: 'userctrl',
                authenticated: true
            })
            .state('user', {
                templateUrl: 'templates/user.html',
                url: '/user/:userId',
                parent: 'index',
                controller: 'userctrl',
                authenticated: true
            })
            .state('users', {
                templateUrl: 'templates/users.html',
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

app.run(function ($rootScope, userService, editableOptions, $state) {
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
        $scope.paid = userService.getProfile().paid;

        $rootScope.$on('loginChange', function () {
            $scope.alias = userService.getProfile().alias;
            $scope.isLoggedIn = userService.isLoggedIn();
            $scope.paid = userService.getProfile().paid;
        });

        $scope.openPay = function () {
            $state.go('pay');
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

        }

        var ModalLoginCtrl = function ($scope, $modalInstance) {
            $scope.loginSubmit = function () {
                var that = this;

                userService.login(this.loginData).promise.then(function (response) {
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
    .controller('userctrl', ['$scope', 'userService', '$stateParams', '$state', function ($scope, userService, $stateParams, $state) {

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

        userService.getUser($stateParams.userId).promise.then(function (userData) {
            $scope.user = userData;
        });
        $scope.ownProfile = ($stateParams.userId == userService.getProfile().id) || $state.current.name == "profile";
        $scope.showPassword = false;

        $scope.openPay = function () {
            $state.go('pay');
        };

        $scope.updateUser = function (element, updateVal) {
            return userService.updateUser(element, updateVal);
        }
        $scope.submitPasswordChange = function(){
            this.formData.resetToken = null;
            userService.changePassword(this.formData).promise.then(function(response){
                $scope.passwordSuccess = true;
            }, function(response){
                $scope.userErrorMessage = "Password change failed: " + response;
            });
        }
    }])
    .controller('usersctrl', ['$scope', 'userService', '$state', function ($scope, userService, $state) {
        userService.getUsers().promise.then(function (userArray) {
            $scope.users = userArray;
        });
        $scope.goToUser = function (id) {
            $state.go('user', {userId: id});
        }
    }])
    .controller('teamsctrl', ['$scope', 'teamService', '$state', '$modal', function ($scope, teamService, $state, $modal) {

        $scope.goToTeam = function (id) {
            $state.go('team', {teamId: id});
        }

        teamService.getTeams().promise.then(function (response) {
            $scope.teams = response;
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

            $scope.games = ['Starcraft II', 'League of Legends', 'CS:GO', 'Halo 3', 'SSB:Brawl'];

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
    }]).controller('teamctrl', ['$scope', 'userService', 'teamService', '$stateParams', '$filter', '$state', function ($scope, userService, teamService, $stateParams, $filter, $state) {

        //TODO Behavior for leaving a team
        //TODO Allow captains to add/remove members at will

        if ($stateParams.teamId != null) {
            getTeamInfo();
        }
        $scope.games = [
            {value: 'Starcraft II', text: 'Starcraft II'},
            {value: 'League of Legends', text: 'League of Legends'},
            {value: 'CS:GO', text: 'CS:GO'},
            {value: 'Halo 3', text: 'Halo 3'},
            {value: 'SSB:Brawl', text: 'SSB:Brawl'}
        ];

        $scope.showJoin = false;
        $scope.clickJoin = function(){
            $scope.showJoin = true;
        }

        $scope.tryJoin = function () {
            teamService.addMember($stateParams.teamId, userService.getProfile().id, $scope.joinPassword).promise.then(function (response) {
                getTeamInfo();
                $scope.teamErrorMessage = undefined;
            }, function (response) {
                $scope.teamErrorMessage = '<strong>Could not join team! </strong>' + response;
            })
        };

        $scope.showGames = function () {
            var selected = $filter('filter')($scope.games, {value: $scope.team.game});
            return ($scope.team.game && selected.length) ? selected[0].text : 'Not set';
        };

        $scope.updateTeam = function (element, updateVal) {
            teamService.updateTeam(element, updateVal, $stateParams.teamId).promise.then(function(response){

            },function(response){
                $scope.teamErrorMessage = '<strong>Could not update team! </strong>' + response;
            });
        };

        function getTeamInfo() {

            teamService.getTeam($stateParams.teamId).promise.then(function (teamData) {
                $scope.team = teamData;

                $scope.teamMembers = [];
                for(i = 1; i < 4; i++)
                {
                    if(teamData["member"+i] != 0)
                    {
                        $scope.teamMembers.push({value:teamData["member"+i],text:teamData["member"+i+"Name"]});
                    }
                }
                /*                    {value:teamData.member1, text:teamData.member1Name},
                 {value:teamData.member2, text:teamData.member2Name},
                 {value:teamData.member3, text:teamData.member3Name},
                 {value:teamData.member4, text:teamData.member4Name}];*/

                $scope.showTeamMembers = function () {
                    var selected = $filter('filter')($scope.teamMembers, {value: $scope.team.captain});
                    return ($scope.team.captain && selected.length) ? selected[0].text : $scope.team.captainName;
                };

                var yourid = userService.getProfile().id; //Oy this is shoddy
                if (yourid == teamData.captain)
                {
                    $scope.ownTeam = true;
                    $scope.onTeam = true;
                }
                if(teamData.member1 == yourid || teamData.member2 == yourid || teamData.member3 == yourid || teamData.member4 == yourid)
                {
                    $scope.onTeam = true;
                }
            });
        }
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