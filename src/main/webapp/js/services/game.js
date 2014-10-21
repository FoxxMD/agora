/**
 * Created by Matthew on 8/28/2014.
 */
// @ngInject
angular.module('gtfest')
    .service('Games', ['Restangular', '$q', function (Restangular, $q) {

        var games = Restangular.all('games');

        this.getGames = function(searchTerm){
            if(searchTerm !== undefined){
                return games.getList({search: searchTerm});
            }
            else{
                return games.getList();
            }
        };
        this.getImageUrl = function(game) {
            if(game.filename)
                return 'images/'+game.filename+'.png';
            return 'images/' + game.name.replace(/[^A-Z0-9]+/ig, "_").toLowerCase() + '.png';

        }
    }]);
