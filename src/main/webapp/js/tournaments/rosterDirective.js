/**
 * Created by Matthew on 10/3/2014.
 */
angular.module('gtfest')
    .directive('roster', rostDirective);
// @ngInject
function rostDirective(Tournaments, Events, Guilds, $state, $stateParams, Account, $q, $timeout) {
    return {
        restrict: 'E',
        templateUrl: '/views/tournaments/roster.html',
        controllerAs: 'rosterCtrl',
        controller: /*@ngInject*/ ["$scope", function ($scope) {
            var that = this;
            this.tour = Tournaments.getCurrent();
            this.user = Account.user();
            this.account = Account;
            this.tourService = Tournaments;
            this.newTeamData = {
                teamPlayers: [Account.isLoggedIn() ? that.user.id : undefined],
                guildOnly: false,
                captainId: Account.isLoggedIn() ? that.user.id : undefined
            };
            this.state = $state;
            this.showTeamForm = Account.isLoggedIn() ? that.user.guilds.length == 0 : false;
            if (that.tour.tournamentType.teamPlay)
                this.isOnTeam = Account.isLoggedIn() ? Tournaments.isOnTeamInRoster(Account.user().id) : false;
            else
                this.isOnRoster = Account.isLoggedIn() ? Tournaments.isUserInRoster(Account.user().id) : false;
            this.toggleGuildMemberSelection = function (player) {
                if (!player.selected) {
                    if (Tournaments.canAddTeamMember(that.newTeamData.teamPlayers)) {
                        that.newTeamData.teamPlayers.push(player.User.id);
                        player.selected = true;
                    }
                    else {
                        $scope.$emit('notify', 'warning', "You've already selected the maximum number of players!", 4000);
                    }
                }
                else {
                    that.newTeamData.teamPlayers.splice(that.newTeamData.teamPlayers.indexOf(player.User.id), 1);
                    player.selected = false;
                }
            };
            this.cachedGuilds = [];
            this.populateGuildMembers = function (guild) {
                that.selectedGuildData = undefined;
                if (that.cachedGuilds[guild.Guild.id] != undefined) {
                    that.selectedGuildData = that.cachedGuilds[guild.Guild.id];
                    that.newTeamData.guildId = guild.Guild.id;
                    $scope.$broadcast('adjustMorphHeight');
                }
                else
                    Guilds.getGuild(guild.Guild.id.toString()).then(function (response) {
                        that.cachedGuilds[response.plain().id] = response.plain();
                        that.cachedGuilds[guild.Guild.id].members = _.map(that.cachedGuilds[guild.Guild.id].members, function (member) {
                            if (member.User.id == that.user.id)
                                member.selected = true;
                            return member;
                        });
                        that.newTeamData.guildId = guild.Guild.id;
                        that.selectedGuildData = that.cachedGuilds[guild.Guild.id];
                        $scope.$broadcast('adjustMorphHeight');
                    });
            };
            this.createNewTeam = function (form) {
                $scope.$broadcast('show-errors-check-validity');
                if (form.$valid) {
                    that.teamLoading = true;
                    Tournaments.createTeam($stateParams.eventId.toString(), that.tour.id.toString(), that.newTeamData).then(function (response) {
                        Tournaments.getTournament($stateParams.eventId.toString(), that.tour.id.toString()).then(function (response) {
                            that.tour = response.plain();
                            that.isOnTeam = Tournaments.isOnTeamInRoster(Account.user().id);
                        });
                        $scope.$emit('notify', 'notice', "Team successfully added.", 4000);
                        $scope.$broadcast('toggleMorph');
                        that.newTeamData = {
                            teamPlayers: [that.user.id],
                            guildOnly: false,
                            captainId: that.user.id
                        };

                        that.selectedGuild = undefined;
                        that.selectedGuildData = undefined;
                        that.showTeamForm = that.user.guilds.length == 0;
                    }).finally(function () {
                        that.teamLoading = false;
                    });
                }
            };
            this.isCaptain = function (team, userId) {
                if(Account.isLoggedIn()){
                    userId = userId || that.user.id;
                    return _.find(team.teamPlayers, function (player) {
                        return player.User.isCaptain && player.User.id == userId;
                    });
                }
                else{
                    return false;
                }

            };
            this.joinTeam = function (team) {
                team.teamJoinLoading = true;
                userId = that.user.id;
                if(that.account.hasPaid($stateParams.eventId)){
                    Tournaments.joinTeam(team.id.toString(), userId, false).then(function (response) {
                        team.teamPlayers = response.teamPlayers;
                        that.isOnTeam = Tournaments.isOnTeamInRoster(Account.user().id, that.tour);
                        $scope.$emit('notify', 'notice', "Succesfully joined team.", 2000);
                    }).finally(function () {
                        team.teamJoinLoading = false;
                    })
                }
                else{
                    team.teamJoinLoading = false;
                    $scope.$emit('notify', 'notice', 'You need to pre-register in order to join a tournament! Visit the <a href="'+$state.href('eventSkeleton.pay',{eventId:$stateParams.eventId})+'">Pay</a> page to do so. ', 5000);
                }

            };
            this.leaveTeam = function (team, userId) {
                if(that.user.id == userId || that.isCaptain(team, that.user.id) || $scope.tourCtrl.isAdmin() || $scope.tourCtrl.isModerator())
                {
                    team.teamLeaveLoading = true;
                    userId = userId || that.user.id;
                    Tournaments.leaveTeam(team.id.toString(), userId).then(function (response) {
                        team.teamPlayers = response.teamPlayers;
                        that.isOnTeam = Tournaments.isOnTeamInRoster(Account.user().id, that.tour);
                        $scope.$emit('notify', 'notice', "Succesfully left team.", 2000);
                    }).finally(function () {
                        team.teamLeaveLoading = false;
                    });
                }
                else{
                    $scope.$emit('notify', 'error', "You do not have permission to kick this player.", 4000);
                }

            };
            this.deleteTeam = function (team) {
                Tournaments.deleteTeam(team.id.toString()).then(function () {
                    $scope.$emit('notify', 'notice', "Team deleted.", 4000);
                })
            };
            this.changeTeamPresent = function(team) {
                team.teamPresentLoading = true;
                Tournaments.changeTeamPresent(team, that.tour.id.toString()).then(function(){
                }, function(){
                    team.isPresent = !team.isPresent;
                }).
                    finally(function () {
                    team.teamPresentLoading = false;
                });
            };
            this.changeUserPresent = function(user) {
                user.presentLoading = true;
                Tournaments.changeUserPresent(user).then(function(){}, function(){
                    user.isPresent = !user.isPresent;
                }).finally(function(){
                    user.presentLoading = false;
                })
            };
            this.changeUserModerator = function(user) {
                user.moderatorLoading = true;
                Tournaments.changeUserModerator(user).then(function(){}, function(){
                    user.isModerator = !user.isModerator;
                }).finally(function(){
                    user.moderatorLoading = false;
                })
            };
            this.changeUserAdmin = function(user) {
                user.adminLoading = true;
                Tournaments.changeUserAdmin(user).then(function(){}, function(){
                    user.isAdmin = !user.isAdmin;
                }).finally(function(){
                    user.adminLoading = false;
                })
            };
            this.changeRosterStatus = function(){
                that.rosterStatusLoading = true;
                if(that.isOnRoster) {
                    Tournaments.removePlayer().then(function(response){
                        that.tour.users = _.filter(that.tour.users, function(user){
                           return user.id != that.user.id;
                        });
                        //that.tour.users.splice TODO user removal after leaving tournament
                        that.isOnRoster = false;
                        $scope.$emit('notify', 'notice', "Succesfully left tournament.", 2000);
                    }).finally(function(){
                        that.rosterStatusLoading = false;
                    });
                }
                else{
                    if(that.account.hasPaid($stateParams.eventId)){
                        Tournaments.addPlayer().then(function(response){
                            that.tour.users.push(response);
                            that.isOnRoster = true;
                            $scope.$emit('notify', 'notice', "Succesfully joined tournament.", 2000);
                        }).finally(function(){
                            that.rosterStatusLoading = false;
                        });
                    }
                    else{
                        that.rosterStatusLoading = false;
                        $scope.$emit('notify', 'notice', 'You need to pre-register in order to join a tournament! Visit the <a href="'+$state.href('eventSkeleton.pay',{eventId:$stateParams.eventId})+'">Pay</a> page to do so. ', 5000);
                    }

                }
            };
            this.bootPlayer = function(userId) {
              Tournaments.removePlayer(userId.toString()).then(function(){
                  that.tour.users = _.filter(that.tour.users, function(user){
                      return user.id != userId;
                  });
                  $scope.$emit('notify', 'notice', "Player booted.", 2000);
              })
            };
            this.hasGuild = function(guildName) {
                if(Account.isLoggedIn()){
                    return _.find(that.user.guilds, function(guild){
                        return guild.name == guildName;
                    })
                }
                else
                    return false;

            };
            this.changeTeamSize = function() {
              Tournaments.update({teamMaxSize: that.tour.details.teamMaxSize, teamMinSize: that.tour.details.teamMinSize}).then(function(){
               return true;
            });
            };
            this.guildTip = function(){
                $scope.$emit('notify','notice',"You can't join this team because you are not part of the Guild! Visit the guild's page to join.",5000);
            }
        }],
        link: function (scope, elem, attrs) {
        }
    }
}
rostDirective.$inject = ["Tournaments", "Events", "Guilds", "$state", "$stateParams", "Account", "$q", "$timeout"];
