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
                templateUrl: '/templates/team.html',
                url: '/team/:teamId',
                parent: 'index',
                controller: 'teamctrl',
                authenticated: true
            })
            .state('teams', {
                templateUrl: '/templates/teams.html',
                url: '/teams',
                parent: 'index',
                controller: 'teamsctrl',
                authenticated: true
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

    $rootScope.$on('$stateChangeStart',
        function (event, toState, toParams, fromState, fromParams) {
            if (toState.authenticated) {
                if (!userService.isLoggedIn()) {
                    event.preventDefault();
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
                    alert("Registration failed: " + response);
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

        $scope.openPay = function () {
            $state.go('pay');
        };

        $scope.updateUser = function (element, updateVal) {
            return userService.updateUser(element, updateVal);
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
            teamService.getTeam($stateParams.teamId).promise.then(function (teamData) {
                $scope.team = teamData;
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
        $scope.games = [
            {value: 'Starcraft II', text: 'Starcraft II'},
            {value: 'League of Legends', text: 'League of Legends'},
            {value: 'CS:GO', text: 'CS:GO'},
            {value: 'Halo 3', text: 'Halo 3'},
            {value: 'SSB:Brawl', text: 'SSB:Brawl'}
        ];

        $scope.showJoin = false;

        $scope.tryJoin = function () {
            teamService.addMember($stateParams.teamId, userService.getProfile().id, $scope.joinPassword).promise.then(function (response) {
                $state.reload();
            }, function (response) {
                alert("Couldn't join team: " + response);
            })
        };

        $scope.showGames = function () {
            var selected = $filter('filter')($scope.games, {value: $scope.team.game});
            return ($scope.team.game && selected.length) ? selected[0].text : 'Not set';
        };

        $scope.updateTeam = function (element, updateVal) {
            return teamService.updateTeam(element, updateVal, $stateParams.teamId);
        }
    }]);