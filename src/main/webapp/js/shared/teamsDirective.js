/**
 * Created by Matthew on 8/27/2014.
 */
angular.module('gtfest')
    .directive('teams', teams);

function teams(Teams, Games, $state, $stateParams, $timeout) {
    return {
        templateUrl:'views/shared/teams.html',
        restrict:'E',
        scope:'true',
        controllerAs:'teamsCtrl',
        controller: function(){
            var that = this;
            this.teamCollection = [];
            if ($state.$current.includes.globalSkeleton) {
                this.teamCollection = Teams.getTeams();
            }
            else if ($state.$current.includes.eventSkeleton) {
                this.teamCollection = Teams.getTeams(undefined, $stateParams.eventId);
            }
        },
        link: function(scope, elem, attrs){
            scope.teamsCtrl.tryCreateTeam = function(){
                Teams.createTeam(scope.teamsCtrl.createTeamData).then(function(tid){
                    scope.$broadcast('toggleMorph');
                    scope.teamsCtrl.createTeamData = {};
                    scope.teamsCtrl.createTeamData.games = [];
                    scope.$broadcast('show-errors-reset');
                    console.log(tid);
                });
            }
        }
    }
}