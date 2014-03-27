angular.module('app.directives', [])
    .directive('scheduleDirective', ['$compile', '$http', function ($compile, $http) {
        var directive = {
            restrict: 'AE',
            template: '<button class="btn btn-info pull-right" ng-click="changeDay(\'2014-03-08\')">Day 1</button><button class="btn btn-info pull-right" ng-click="changeDay(\'2014-03-09\')">Day 2</button><div id="calendar" style="float:left;"></div>',
            link: function (scope, element, attrs) {
                $http.get('/content/schedule.json').success(function (data) {
                    var realSchedule = [];
                    angular.copy(data, realSchedule);
                    for(var i = 0; i < data.length; i++)
                    {
                        realSchedule[i].start = moment(data[i].start, "YYYY-MM-DD HH:mm").valueOf();
                        realSchedule[i].end = moment(data[i].end, "YYYY-MM-DD HH:mm").valueOf();
                    }
                    var calendar = $(element).find('#calendar').calendar({
                        events_source: realSchedule,
                        tmpl_path: 'js/calendar/tmpls/',
                        view: 'day',
                        day: '2014-03-08'
                    });

                    scope.changeDay = function (theDay) {
                        calendar.setOptions({day: theDay});
                        calendar.view();
                    };

                });

            }
        };
        return directive;
    }])
    //stolen from http://rogeralsing.com/2013/08/26/angularjs-directive-to-check-that-passwords-match-followup/
    .directive('passwordMatch', [function () {
        return {
            restrict: 'A',
            scope: true,
            require: 'ngModel',
            link: function (scope, elem, attrs, control) {
                var checker = function () {

                    //get the value of the first password
                    var e1 = scope.$eval(attrs.ngModel);

                    //get the value of the other password
                    var e2 = scope.$eval(attrs.passwordMatch);
                    return e1 == e2;
                };
                scope.$watch(checker, function (n) {

                    //set the form control to valid if both
                    //passwords are the same, else invalid
                    control.$setValidity("unique", n);
                });
            }
        };
    }])
    .directive('stripeDir', ['$rootScope', 'userService', '$state', function ($rootScope, userService, $state) {
        return {
            restrict: 'A',
            templateUrl: '/templates/pay.html',
            link: function (scope, elem, attrs) {
                Stripe.setPublishableKey('pk_test_C5kuVaBMR3FiCbMYfxS9mxpq'); //test key
            },
            controller: function ($scope) {
                if (userService.getProfile().paid == 1) {
                    userService.alreadyPaid(true);
                    $state.go('profile');
                }
                if (userService.getProfile().token == null) {
                    $state.go('home');
                }

                $scope.handleStripe = function (status, response) {
                    if (response.error) {
                        // there was an error. Fix it.
                        $scope.formErrorMessage = '<strong>There was an error submitting your information.</strong> Please ensure that you have input all of your information correctly and try again.';
                        $scope.formErrorTechMessage = response.error;
                    } else {
                        // got stripe token, send
                        var data = { token: response.id};
                        userService.payRegistration(data).promise.then(function (response) {
                            //payment success
                            if (response == undefined || response == null) {
                                userService.justPaid(true);
                                $state.go('profile');
                            } else {
                                $scope.formErrorMessage = '<strong>Your payment was submitted successfully! But there was a problem recording your payment.</strong> Please contact an admin to fix this issue. <strong>Do not resubmit your payment!</strong>';
                                $scope.formErrorTechMessage = response;
                            }

                        }, function (response) {
                            $scope.formErrorMessage = '<strong>There was an error processing your payment, you have not been charged.</strong> Please ensure your card and billing information is correct before trying again.';
                            $scope.formErrorTechMessage = response;
                        });
                    }
                }
            }

        };
    }])
    .directive('gamesectionDir', [function () {
        return {
            restrict: 'A',
            templateUrl: '/templates/games.html',
            link: function (scope, element, attrs) {
                $(element).find('.thumbnail').on('click', function (ev, target) {
                    $(element).find('.thumbnail').removeClass('active orange');
                    $(this).addClass('active orange');
                    var anchor = $(this).attr('href');
                    $(document.body).animate({
                        'scrollTop':   $('#eventRules').offset().top
                    }, 1000, 'swing');
                });
            }
        }
    }])
    .directive('gameDir', ['$http', '$state', function ($http, $state) {
        return {
            restrict: 'A',
            templateUrl: '/templates/gameDirective.html',
            controller: function ($scope, $element) {
                $http.get($state.current.data).success(function (data) {
                    $scope.gameInfo = data;
                });
            },
            link: function (scope, element, attrs) {

            }
        }
    }])
    .directive('aboutDir', ['$http', function ($http) {
        return {
            restrict: 'A',
            templateUrl: '/templates/about.html',
            controller: function ($scope) {
                $http.get('content/credits.json').success(function (data) {
                    $scope.credits = data;
                });
            },
            link: function (scope, element, attrs) {
                $(element).find('#regCredits').on('click', function () {
                    $(element).find('#start').remove();
                    $(element).find('#titles').attr('id', 'boringtitle');
                    $(element).find('#titlecontent').attr('id', 'boringtitlecontent');
                    $(element).closest('html').css('overflow', 'auto');
                });
            }
        }
    }])
    .directive('teamDir', ['userService', 'teamService', '$stateParams', '$filter', '$state', function (userService, teamService, $stateParams, $filter, $state) {
        return {
            restrict: 'A',
            templateUrl: '/templates/team.html',
            controller: function ($scope) {
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
                $scope.clickJoin = function () {
                    $scope.showJoin = true;
                };

                $scope.admin = userService.adminMode() && (userService.getProfile().role == 1 || userService.getProfile().role == 1);

                $scope.tryJoin = function () {
                    teamService.addMember($stateParams.teamId, userService.getProfile().id, $scope.joinPassword).promise.then(function (response) {
                        getTeamInfo();
                        $scope.teamErrorMessage = undefined;
                    }, function (response) {
                        $scope.teamErrorMessage = '<strong>Could not join team! </strong>' + response;
                    })
                };
                $scope.tryLeave = function () {
                    var memberVar = null;
                    for (i = 1; i < 4; i++) {
                        if ($scope.team['member' + i] == userService.getProfile().id) {
                            memberVar = 'member' + i;
                        }
                    }
                    teamService.updateTeam(memberVar, 0, $stateParams.teamId).promise.then(function (response) {
                        getTeamInfo();
                    }, function (response) {
                        $scope.teamErrorMessage = '<strong>Could not update team! </strong>' + response;
                    });
                };

                $scope.updateTeam = function (element, updateVal) {
                    if(element === 'password' && updateVal === undefined)
                    {
                        return 'Password must be 5 characters or less';
                    }

                    teamService.updateTeam(element, updateVal, $stateParams.teamId).promise.then(function (response) {
                        getTeamInfo();
                    }, function (response) {
                        $scope.teamErrorMessage = '<strong>Could not update team! </strong>' + response;
                        return false;
                    });
                };

                function getTeamInfo() {

                    teamService.getTeam($stateParams.teamId).promise.then(function (teamData) {
                        $scope.team = teamData;


                        $scope.showGames = function () {
                            var selected = $filter('filter')($scope.games, {value: $scope.team.game});
                            return ($scope.team.game && selected.length) ? selected[0].text : 'Not set';
                        };

                        $scope.teamMembers = [];
                        for (i = 1; i < 4; i++) {
                            if (teamData["member" + i] != 0) {
                                $scope.teamMembers.push({value: teamData["member" + i], text: teamData["member" + i + "Name"]});
                            }
                        }

                        $scope.showTeamMembers = function () {
                            var selected = $filter('filter')($scope.teamMembers, {value: $scope.team.captain});
                            return ($scope.team.captain && selected.length) ? selected[0].text : $scope.team.captainName;
                        };

                        var yourid = userService.getProfile().id; //Oy this is shoddy
                        if (yourid == teamData.captain || userService.role == 2 || userService.role == 1) {
                            $scope.ownTeam = true;
                            $scope.onTeam = true;
                        }
                        else if (teamData.member1 == yourid || teamData.member2 == yourid || teamData.member3 == yourid || teamData.member4 == yourid) {
                            $scope.onTeam = true;
                        }
                        else {
                            $scope.onTeam = false;
                        }
                    });
                }
            },
            link: function (scope, element, attrs) {

            }
        };
    }]);