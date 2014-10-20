/**
 * Created by Matthew on 8/27/2014.
 */
angular.module('gtfest')
    .directive('guilds', teams);
// @ngInject
function teams(Guilds, Games, $state, $stateParams, $timeout, Account, Events) {
    return {
        templateUrl:'views/guilds/guilds.html',
        restrict:'E',
        scope:'true',
        controllerAs:'guildsCtrl',
        controller: /*@ngInject*/ ["$scope", function($scope){
            var that = this,
                pageNo = 1;
            this.guildCollection = [];
            this.stateParams = $stateParams;
            this.state = $state;
            this.createGuildData = {
                maxPlayers: 0,
                joinType: 'Public'
            };
            if ($state.$current.includes.globalSkeleton) {
                Guilds.getGuilds().then(function(response){
                   that.guildCollection = response.plain();
                });
            }
            else if ($state.$current.includes.eventSkeleton) {
                that.isEvent = true;
                Events.getTeamsAndGuilds($stateParams.eventId.toString()).then(function(response){
                    that.guildCollection = response.plain();
                });
            }

            this.getMoreGuilds = function(){
                that.busy = true;
                pageNo++;
                if(!that.isEvent){
                    Guilds.getGuilds(pageNo).then(function(response){
                        if(response.length > 0)
                        {
                            that.guildCollection = that.guildCollection.concat(response.plain());
                            that.busy = false;
                        }
                    });
                }
                else{
                    Events.getTeamsAndGuilds($stateParams.eventId.toString(), pageNo).then(function(response){
                        if(response.length > 0)
                        {
                            that.guildCollection = that.guildCollection.concat(response.plain());
                            that.busy = false;
                        }
                    });
                }
            };

            this.loadGames = function(query) {
                var deferred = $q.defer();
                var plats = [{'text':'Halo'},
                    {'text':'LoL'},
                    {'text':'CS:GO'}];
                deferred.resolve(plats);
                return deferred.promise;
            };

            this.filterGuilds = function(guild) {
                var passed = true;
                if(that.guildNameTags.length > 0)
                {
                    passed = that.guildNameTags.filter(function(val, index, arr){
                        return guild.name.toLowerCase().indexOf(val.text.toLowerCase()) != -1;
                    }).length > 0;
                }
                if(that.guildGameTags.length > 0)
                {
                    passed = that.guildGameTags.filter(function(val, index, arr){
                        for(var i = 0; i < user.gameProfiles.length; i++){
                            if(guild.games[i].name == val.text)
                                return true;
                        }
                    }).length > 0;
                }
                return passed;
            };
        }],
        link: function(scope, elem, attrs){
            scope.guildsCtrl.tryCreateGuild = function(form){
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
teams.$inject = ["Guilds", "Games", "$state", "$stateParams", "$timeout", "Account" ,"Events"];
