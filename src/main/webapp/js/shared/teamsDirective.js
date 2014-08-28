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
              var teamButton = new UIMorphingButton( $(elem).find('.morph-button')[0]);
            scope.$on('adjustMorphHeight', function(){
                $timeout(function(){
                    teamButton.adjustHeight();
                },0); //TODO turn morphing button into it's own directive
            });
            scope.teamsCtrl.tryCreateTeam = function(){
                Teams.createTeam(scope.teamsCtrl.createTeamData).promise.then(function(tid){
                    teamButton.toggle();
                    scope.teamsCtrl.createTeamData = {};
                    scope.teamsCtrl.createTeamData.games = [];
                    scope.$broadcast('show-errors-reset');
                    console.log(tid);
                });
            }
        }
    }
}