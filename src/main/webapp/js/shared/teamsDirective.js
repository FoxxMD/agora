/**
 * Created by Matthew on 8/27/2014.
 */
angular.module('gtfest')
    .directive('teams', teams);
// @ngInject
function teams(Teams, Games, $state, $stateParams, $timeout, Account) {
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
                that.isEvent = true;
                this.teamCollection = Teams.getTeams(undefined, $stateParams.eventId);
            }
            this.tableGoTo = function($event, id) {
                if($($event.target).is('td'))
                {
                    var thestate = '';
                    if($state.$current.includes.globalSkeleton)
                        $state.go('globalSkeleton.team',{teamId:id});
                    else
                        $state.go('eventSkeleton.team',{teamId:id, eventId:$stateParams.eventId});
                }

            };
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
teams.$inject = ["Teams", "Games", "$state", "$stateParams", "$timeout", "Account"];