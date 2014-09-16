/**
 * Created by Matthew on 8/27/2014.
 */
angular.module('gtfest')
    .directive('guilds', teams);
// @ngInject
function teams(Guilds, Games, $state, $stateParams, $timeout, Account) {
    return {
        templateUrl:'views/guilds/guilds.html',
        restrict:'E',
        scope:'true',
        controllerAs:'guildsCtrl',
        controller: function(){
            var that = this;
            this.guildCollection = [];
            this.createGuildData = {
                maxPlayers: 0,
                joinType: 'Public'
            };
            if ($state.$current.includes.globalSkeleton) {
                this.guildCollection = Guilds.getGuilds();
            }
            else if ($state.$current.includes.eventSkeleton) {
                that.isEvent = true;
                this.guildCollection = Guilds.getGuilds(undefined, $stateParams.eventId);
            }
            this.tableGoTo = function($event, id) {
                if($($event.target).is('td'))
                {
                    var thestate = '';
                    if($state.$current.includes.globalSkeleton)
                        $state.go('globalSkeleton.guild',{guildId:id});
                    else
                        $state.go('eventSkeleton.guild',{guildId:id, eventId:$stateParams.eventId});
                }

            };
        },
        link: function(scope, elem, attrs){
            scope.guildsCtrl.tryCreateGuild = function(){
                Guilds.createGuild(scope.guildsCtrl.createGuildData).then(function(tid){
                    scope.$broadcast('toggleMorph');
                    scope.guildsCtrl.createGuildData = {
                        maxPlayers: 0,
                        joinType: 'Public'
                    };
                    scope.guildsCtrl.createGuildData.games = [];
                    scope.$broadcast('show-errors-reset');
                    console.log(tid);
                });
            }
        }
    }
}
teams.$inject = ["Guilds", "Games", "$state", "$stateParams", "$timeout", "Account"];