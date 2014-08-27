/**
 * Created by Matthew on 8/27/2014.
 */
angular.module('gtfest')
    .directive('teams', teams);

function teams(Teams, $state, $stateParams) {
    return {
        templateUrl:'views/shared/teams.html',
        restrict:'E',
        scope:'true',
        controllerAs:'teamsCtrl',
        controller: function(){
            this.teamCollection = [];
            if ($state.$current.includes.globalSkeleton) {
                this.teamCollection = Teams.getTeams();
            }
            else if ($state.$current.includes.eventSkeleton) {
                this.teamCollection = Teams.getTeams(undefined, $stateParams.eventId);
            }
        },
        link: function(scope, elem, attrs){
                new UIMorphingButton( $(elem).find('.morph-button')[0]);
        }
    }
}