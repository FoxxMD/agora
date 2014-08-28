/**
 * Created by Matthew on 8/28/2014.
 */
angular.module('gtfest')
    .service('Events', function (Restangular, $q, $rootScope) {

        var events = Restangular.all('events');

        this.getEvents = function(pageNo){
            return events.getList({page: pageNo}).$object;
        };

        this.getEvent = function(eventId) {
           return events.one(eventId).get();
        }
    });