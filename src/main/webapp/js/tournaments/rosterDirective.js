/**
 * Created by Matthew on 10/3/2014.
 */
angular.module('gtfest')
    .directive('roster', rostDirective);
// @ngInject
function rostDirective(Tournaments, Events, Guilds, $state, $stateParams, Account, $q) {
    return {
        restrict: 'E',
        templateUrl: '/views/tournaments/roster.html',
        controllerAs: 'rosterCtrl',
        controller: function ($scope) {
            var that = this;
            this.tour = Tournaments.getCurrent();
            this.user = Account.user();
            this.newTeamData = {
                teamPlayers: [that.user.id],
                guildOnly: false,
                captainId: that.user.id
            };
            this.showTeamForm = that.user.guilds.length == 0;
            if (that.tour.tournamentType.teamPlay)
                this.isOnTeam = Tournaments.isOnTeamInRoster(Account.user().id);
            else
                this.isOnRoster = Tournaments.isUserInRoster(Account.user().id);
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
                if (that.cachedGuilds[guild.Guild.id] != undefined)
                {
                    that.selectedGuildData = that.cachedGuilds[guild.Guild.id];
                    that.newTeamData.guildId = guild.Guild.id;
                    $scope.$broadcast('adjustMorphHeight');
                }
                else
                    Guilds.getGuild(guild.Guild.id.toString()).then(function (response) {
                        that.cachedGuilds[response.plain().id] = response.plain();
                        that.cachedGuilds[guild.Guild.id].members = _.map(that.cachedGuilds[guild.Guild.id].members, function(member){
                           if(member.User.id == that.user.id)
                                member.selected = true;
                            return member;
                        });
                        that.newTeamData.guildId = guild.Guild.id;
                        that.selectedGuildData = that.cachedGuilds[guild.Guild.id];
                        $scope.$broadcast('adjustMorphHeight');
                    });
            };
            this.createNewTeam = function(){
                $scope.$broadcast('show-errors-check-validity');
                if ($scope.$$childHead.$$childHead.$$nextSibling.newTeamForm.$valid) {
                    that.teamLoading = true;
                    Tournaments.createTeam($stateParams.eventId.toString(), that.tour.id.toString(), that.newTeamData).then(function(){
                        Tournaments.getTournament($stateParams.eventId.toString(), that.tour.id.toString()).then(function(response){
                            that.tour = response.plain();
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
                    }).finally(function(){
                        that.teamLoading = false;
                    });
                }
            };
        },
        link: function (scope, elem, attrs) {
        }
    }
}
rostDirective.$inject = ["Tournaments", "Events", "Guilds", "$state", "$stateParams", "Account", "$q"];
tourDirective.$inject = ["Tournaments", "Events", "Guilds", "$state", "$stateParams", "Account", "$q"];
