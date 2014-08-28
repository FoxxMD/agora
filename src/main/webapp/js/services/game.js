/**
 * Created by Matthew on 8/28/2014.
 */
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
        }
    }]);