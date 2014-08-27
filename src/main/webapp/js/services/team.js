/**
 * Created by Matthew on 8/22/2014.
 */
angular.module('gtfest')
    .service('Teams', ['Restangular', '$q', function (Restangular, $q) {

        var teams = Restangular.all('teams');

        this.getTeams = function(pageNo, eventId){
            pageNo == undefined ? pageNo = 1 : pageNo;
            if(eventId !== undefined){
                var eventTeams = Restangular.service('teams', Restangular.one('event',eventId));
                return eventTeams.getList({pageNo: pageNo}).$object;
            }
            else{
               return teams.getList({pageNo: pageNo}).$object;
            }
        }
    }]);