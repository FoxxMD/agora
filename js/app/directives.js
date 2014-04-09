angular.module('app.directives', [])
    //no longer using this but may be useful later!
    /*.directive('scheduleDirective', ['$compile', '$http', function ($compile, $http) {
     var directive = {
     restrict: 'AE',
     template: '<button class="btn btn-info pull-right" ng-click="changeDay(\'2014-04-20\')">Day 2</button><button class="btn btn-info pull-right" ng-click="changeDay(\'2014-04-19\')">Day 1</button><div id="calendar" style="float:left;"></div>',
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
     day: '2014-04-19'
     });

     scope.changeDay = function (theDay) {
     calendar.setOptions({day: theDay});
     calendar.view();
     };

     });

     }
     };
     return directive;
     }])*/
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
                Stripe.setPublishableKey('pk_live_YwddUwRH90xpNcHRNewOjZhG'); //live key
            },
            controller: function ($scope) {

                var payed = false;

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
                        if (!payed) {
                            payed = true;
                            userService.payRegistration(data).promise.then(function (response) {
                                //payment success
                                if (response == undefined || response == null) {
                                    userService.justPaid(true);
                                    $state.go('profile');
                                } else {
                                    $scope.formErrorMessage = '<strong>Your payment was submitted successfully! But there was a problem recording your payment.</strong> Please contact an admin to fix this issue. <strong>Do not resubmit your payment!</strong>';
                                    $scope.formErrorTechMessage = response;
                                }
                                payed = false;
                            }, function (response) {
                                $scope.formErrorMessage = '<strong>There was an error processing your payment, you have not been charged.</strong> Please ensure your card and billing information is correct before trying again.';
                                $scope.formErrorTechMessage = response;
                                payed = false;
                            });
                        }
                    }
                }
            }

        };
    }])
    .directive('gamesectionDir', ['$stateParams', function ($stateParams) {
        return {
            restrict: 'A',
            templateUrl: '/templates/games.html',
            link: function (scope, element, attrs) {

                if ($stateParams.gameId !== undefined) {
                    $('#gameSelect' + $stateParams.gameId).addClass('active orange');
                }

                $(element).find('.thumbnail').on('click', function (ev, target) {
                    $(element).find('.thumbnail').removeClass('active orange');
                    $(this).addClass('active orange');

                    //don't think animation is necessary here anymore
                    /*                    var anchor = $(this).attr('href');
                     $(document.body).animate({
                     'scrollTop': $('.gamesDirSection').offset().top
                     }, 1000, 'swing');*/
                });
            }
        }
    }])
    .directive('gameDir', ['$http', '$state', '$timeout', '$stateParams', '$rootScope', function ($http, $state, $timeout, $stateParams, $rootScope) {
        return {
            restrict: 'A',
            templateUrl: '/templates/gameDirective.html',
            controller: function ($scope, $element, $stateParams) {
                $http.get('/content/games/' + $stateParams.gameId + '.json').success(function (data) {
                    $scope.gameInfo = data;
                    $scope.tabs = data.tourney.map(function (item) {
                        return { "title": item.title,
                            "id": item.id,
                            "active": $stateParams.tourId === item.id};
                    });
                    $scope.gameImg = '/img/game_logos/' + data.img;
                });

                $scope.openTourney = function (id) {
                    $state.go('tourDetail', {tourId: id});
                };
                $scope.hideTour = function (which) {
                    $scope.hideTourPane = which;
                };

                if ($stateParams.tourId !== undefined) {
                    $scope.openTourney($stateParams.tourId);
                    $scope.hideTourPane = false;
                }
                else {
                    $scope.hideTourPane = true;
                }

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
                $(element).closest('html').css('overflow', 'auto');
                $(element).find('#starCredits').on('click', function () {
                    credits = $(element).find('#creditsSection').clone();
                    $(element).find('#creditsSection').remove();
                    $(credits).find('#boringtitle').before('<p class="text-center" id="start">Thanks for those that made Gamefest possible...</p>');
                    $(credits).find('#boringtitle').attr('id', 'titles');
                    $(credits).find('#boringtitlecontent').attr('id', 'titlecontent');
                    $(element).closest('html').css('overflow', 'hidden');
                    $(element).find('#creditsWrapper').html(credits);
                });
            }
        }
    }])
    .directive('teamDir', ['userService', 'teamService', '$stateParams', '$filter', '$state', '$rootScope', function (userService, teamService, $stateParams, $filter, $state, $rootScope) {
        return {
            restrict: 'A',
            templateUrl: '/templates/team.html',
            controller: function ($scope) {
                if ($stateParams.teamId != null) {
                    getTeamInfo();
                }
                $scope.games = [
                    {value: 'Starcraft 2', text: 'Starcraft 2'},
                    {value: 'League of Legends SR', text: 'League of Legends SR'},
                    {value: 'League of Legends ARAM', text: 'League of Legends ARAM'},
                    {value: 'CS:GO', text: 'CS:GO'},
                    {value: 'Halo 3 2v2', text: 'Halo 3 2v2'},
                    {value: 'Halo 3 3v3', text: 'Halo 3 3v3'},
                    {value: 'SSB:Brawl', text: 'SSB:Brawl'},
                    {value: 'SSB:Melee', text: 'SSB:Melee'},
                    {value: 'DOTA 2', text: 'DOTA 2'}
                ];

                $scope.showJoin = false;
                $scope.clickJoin = function () {
                    $scope.showJoin = true;
                };

                $scope.admin = userService.adminMode() && (userService.getProfile().role == 1 || userService.getProfile().role == 1);

                $scope.tryJoin = function () {
                    teamService.addMember($stateParams.teamId, userService.getProfile().id, $scope.joinPassword).promise.then(function (response) {
                        getTeamInfo();
                        $rootScope.siteError = null;
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
                    });
                };

                $scope.updateTeam = function (element, updateVal) {
                    if (element === 'password' && updateVal === undefined) {
                        return 'Password must be 5 characters or less';
                    }

                    teamService.updateTeam(element, updateVal, $stateParams.teamId).promise.then(function (response) {
                        getTeamInfo();
                    });
                };

                $scope.deleteTeam = function (id) {
                    teamService.deleteTeam(id).promise.then(function () {
                        $state.go('teams');
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

                        if(teamData.tournaments.length > 0)
                        {
                            $scope.registeredForTour = true;
                            if($scope.ownTeam)
                            {
                                $rootScope.siteInfo = "While your team is registered for a tournament you cannot change team details or add/remove members.";
                            }
                        }

                    });
                }
            },
            link: function (scope, element, attrs) {

            }
        };
    }])
    .directive('tourdetailDir', ['tourService', '$stateParams', '$http', '$state', 'userService', '$modal', '$filter','teamService','$rootScope', function (tourService, $stateParams, $http, $state, userService, $modal, $filter, teamService, $rootScope) {
        return {
            restrict: 'AE',
            templateUrl: '/templates/tourDetail.html',
            controller: function ($scope) {

                tourService.getTournamentInfo($stateParams.tourId).promise.then(function (response) {
                    $scope.tourInfo = response.info;
                    $scope.tourUsers = response.users;
                    $scope.tourTeams = response.teams;
                    $http.get('/content/games/' + response.info.jsonName + '.json').success(function (data) {
                        $scope.jsonInfo = data;
                        for (var i = 0; i < data.tourney.length; i++) {
                            if (data.tourney[i].title == response.info.Name) {
                                $scope.tourney = data.tourney[i];
                            }
                        }
                    });

                    $scope.admin = userService.adminMode() && (userService.getProfile().role == 1 || userService.getProfile().role == 2);

                    userService.getProfile().captainOf.map(function (item) {
                        var found = false;
                        for (var i = 0; i < response.teams.length; i++) {
                            if (response.teams[i].ID == item.ID) {
                                $scope.foundTeam = item.ID;
                                break;
                            }
                        }
                    });
                    userService.getProfile().tournaments.map(function (item) {
                        if (item.Id == $stateParams.tourId) {
                            $scope.foundPlayer = userService.getProfile().id
                        }
                    });
                    $scope.showPlayers = function () {
                        var selected = $filter('filter')($scope.numPlayers, {value: $scope.tourInfo.minTeamMembers});
                        return ($scope.tourInfo.minTeamMembers != undefined && selected.length > 0) ? selected[0].text : 'Not set';
                    };
                    $scope.showEntrants = function () {
                        var selected = $filter('filter')($scope.entrants, {value: $scope.tourInfo.isTeamOnly});
                        return ($scope.tourInfo.isTeamOnly != undefined && selected.length > 0) ? selected[0].text : 'Not set';
                    };
                    $scope.showStatus = function () {
                        var selected = $filter('filter')($scope.tourStatus, {value: $scope.tourInfo.isPlaying});
                        return ($scope.tourInfo.isPlaying != undefined && selected.length > 0) ? selected[0].text : 'Not set';
                    };
                    $scope.openRosters = $scope.tourInfo.isPlaying == 0;
                });

                //$scope.selectedTeam = null;

                $scope.userService = userService;

                $scope.numPlayers = [
                    {value: 0, text: 'Not Set'},
                    {value: 1, text: '1'},
                    {value: 2, text: '2'},
                    {value: 3, text: '3'},
                    {value: 4, text: '4'},
                    {value: 5, text: '5'}
                ];
                $scope.entrants = [
                    {value: 0, text: 'Players'},
                    {value: 1, text: 'Teams'}
                ];
                $scope.tourStatus = [
                    {value: 0, text: 'Open'},
                    {value: 1, text: 'Rosters Closed'},
                    {value: 2, text: 'In-Progress'},
                    {value: 3, text: 'Complete'}];

                $scope.setPlayers = function (num) {
                    tourService.setPlayers(num, $stateParams.tourId).promise.then(function () {
                    }, function () {
                        return "Couldn't set players..";
                    });
                };
                $scope.setEntrantType = function (num) {
                    tourService.setEntrantType(num, $stateParams.tourId).promise.then(function () {
                    }, function () {
                        return "Couldn't set entrant type";
                    });
                };
                $scope.setTourStatus = function (num) {
                    tourService.setTourStatus(num, $stateParams.tourId).promise.then(function () {
                    }, function () {
                        return "Couldn't set status";
                    });
                };

                $scope.tryRegisterPlayer = function () {
                    tourService.registerUser(userService.getProfile().id, $stateParams.tourId).promise.then(function () {
                        $scope.tourUsers.push(userService.getProfile());
                        $scope.foundPlayer = userService.getProfile().id;
                    });
                };
                $scope.tryLeavePlayer = function (userId) {

                    tourService.removeUser(userId, $stateParams.tourId).promise.then(function () {
                        $scope.tourUsers = $scope.tourUsers.map(function (item) {
                            if (item.id != userId) {
                                return item;
                            }
                        });
                        $scope.foundPlayer = undefined;
                    });
                };
                $scope.tryLeaveTeam = function (teamId) {

                    tourService.removeTeam(teamId, $stateParams.tourId).promise.then(function () {
                        $scope.tourTeams = $scope.tourTeams.map(function (item) {
                            if (item.ID != teamId) {
                                return item;
                            }
                        });
                        $scope.foundTeam = undefined;
                    });
                };

                var $tourScope = $scope;

                /*
                 Team Join Modal

                 */
                $scope.open = function () {

                    var modalInstance = $modal.open({
                        templateUrl: 'teamRegister.html',
                        controller: ModalTeamRegisterInstanceCtrl,
                        resolve: {
                            yourTeams: function () {
                                return userService.getProfile().captainOf;
                            },
                            tourneyTeams: function () {
                                return $scope.tourTeams;
                            },
                            minimumMembers: function () {
                                return $scope.tourInfo.minTeamMembers;
                            }
                        }
                    });

                    /*                        modalInstance.result.then(function (selectedItem) {
                     $scope.selected = selectedItem;
                     }, function () {
                     //$log.info('Modal dismissed at: ' + new Date());
                     });*/
                };


                var ModalTeamRegisterInstanceCtrl = function ($scope, $modalInstance, yourTeams, tourneyTeams, minimumMembers) {
                    var allPlayers = [];
                    $scope.loading = true;
                    $scope.minimum = minimumMembers;
                    tourneyTeams.map(function (item) {
                        for (var i = 0; i < item.members.length; i++) {
                            if (item.members[i] != 0) {
                                allPlayers[item.members[i]] = true;
                            }
                        }
                        allPlayers[item.captain] = true;
                    });
                    $scope.modifiedTeams = yourTeams.map(function (item) {
                        item.valid = true;
                        item.numberValid = true;
                        for (var i = 0; i < item.members.length; i++) {
                            if (allPlayers[item.members[0]] != undefined) {
                                item.valid = false;
                            }
                        }
                        if (allPlayers[userService.getProfile().id] != undefined) {
                            item.valid = false;
                        }
                        if (minimumMembers != null) {
                            var realmems = [];
                            for (var y = 0; y < item.members.length; y++) {
                                if (item.members[y] != undefined && item.members[y] != 0) {
                                    realmems.push(item.members[y]);
                                }
                            }
                            item.members = realmems;
                            item.members.push(item.captain);
                            if (item.members.length != minimumMembers) {
                                item.numberValid = false;
                            }
                        }
                        return item;
                    });
                    $scope.loading = false;

                    $scope.selectedTeam = null;
                    $scope.selectTeam = function (team) {
                        if (team.valid && team.numberValid) {
                            $scope.selectedTeam = team;
                        }
                    };

                    $scope.register = function () {
                        tourService.registerTeam($scope.selectedTeam.ID, $stateParams.tourId).promise.then(function () {
                            $tourScope.foundTeam = $scope.selectedTeam.ID;
                            $tourScope.tourTeams.push($scope.selectedTeam);
                            $modalInstance.close();
                        });
                    };

                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    };
                };


                $scope.addPlayerToTeam = function (id) {
                    var modalInstance = $modal.open({
                        templateUrl: 'teamJoin.html',
                        controller: ModalTeamJoinInstanceCtrl,
                        resolve: {
                            yourTeams: function () {
                                return userService.getProfile().captainOf;
                            },
                            id: function () {
                                return id;
                            }
                        }
                    });
                    modalInstance.result.then(function (success) {
                        if(success)
                        {
                            $rootScope.siteSuccess = "User added to your team.";
                        }
                     }, function () {

                     });
                };

                var ModalTeamJoinInstanceCtrl = function ($scope, $modalInstance, yourTeams, id) {

                    $scope.loading = true;

                    $scope.modifiedTeams = yourTeams.map(function (item) {
                        item.valid = true,
                        item.duplicateUser = false;
                            var realmems = [];
                            for (var y = 0; y < item.members.length; y++) {
                                if (item.members[y] != undefined && item.members[y] != 0) {
                                    realmems.push(item.members[y]);
                                    if(id == item.members[y])
                                    {
                                        item.duplicateUser = true;
                                    }
                                }
                            }
                            item.members = realmems;
                            item.members.push(item.captain);
                            if (item.members.length == 5) {
                                item.valid = false;
                            }
                        return item;
                    });
                    $scope.loading = false;

                    $scope.selectedTeam = null;
                    $scope.selectTeam = function (team) {
                        if (team.valid && !team.duplicateUser) {
                            $scope.selectedTeam = team;
                        }
                    };

                    $scope.addToTeam = function () {
                        teamService.addMember($scope.selectedTeam.ID, id).promise.then(function (response) {
                            tourService.removeUser(id, $stateParams.tourId).promise.then(function () {
                                var userindex = 0;
                                $tourScope.tourUsers.map(function(item,index){
                                    if(item.id == id)
                                    {
                                        userindex = index;
                                    }
                                });
                                $tourScope.tourUsers.splice(userindex,1);
                            });
                            $modalInstance.close(true);
                        });
                    };

                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    };
                };

            },
            link: function (scope, element, attrs) {

                scope.makePlayerPresent = function (userId) {
                    tourService.makePlayerPresent(userId, $stateParams.tourId).promise.then(function () {
                        $(element).find('#presentPlayer' + userId).remove();
                    });
                };

                scope.makeTeamPresent = function (teamId) {
                    tourService.makeTeamPresent(teamId, $stateParams.tourId).promise.then(function () {
                        $(element).find('#presentTeam' + teamId).remove();
                    });
                };
            }
        }
    }]);