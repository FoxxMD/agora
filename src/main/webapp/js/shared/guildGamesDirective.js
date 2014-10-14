/**
 * Created by Matthew on 8/28/2014.
 */
angular.module('gtfest')
    .directive('guildGames', teamGames);
// @ngInject
function teamGames(Games, $rootScope) {
    return {
        template: '<ul class="list-unstyled"><li ng-repeat="game in gamearray" class="gameItem"><a ui-sref="game({id: game.id})">{{game.name}}</a> <button class="btn btn-sm pull-right" ng-click="tgCtrl.removeGame(game)">Remove</button></li></ul>' +
            '<input type="text" ng-model="tgCtrl.zeGame" typeahead="game as game.name for game in tgCtrl.gamesCollection" typeahead-on-select="tgCtrl.addGame($item)" class="form-control">',
        scope: {
            'gamearray': '='
        },
        restrict: 'E',
        controllerAs: 'tgCtrl',
        controller: /*@ngInject*/ ["$scope", function ($scope) {
            var that = this;
            if($scope.gamearray == undefined)
                $scope.gamearray = [];
            Games.getGames().then(function (games) {
                   that.gamesCollection = $(games.plain()).not($scope.gamearray).get();
            });
            this.addGame = function(aGame) {
                $scope.gamearray.push(aGame);
                that.zeGame = null;
                that.gamesCollection.splice(that.gamesCollection.indexOf(aGame),1);
                $rootScope.$broadcast('adjustMorphHeight');
            };
            this.removeGame = function(g) {
                $scope.gamearray.splice($scope.gamearray.indexOf(g),1);
                that.gameCollection.push(g);
                $rootScope.$broadcast('adjustMorphHeight');
            }
        }]
    }
}
teamGames.$inject = ["Games", "$rootScope"];
