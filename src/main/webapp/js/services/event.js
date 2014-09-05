/**
 * Created by Matthew on 8/28/2014.
 */
// @ngInject
angular.module('gtfest')
    .service('Events', ["Restangular", "$q", "$rootScope", function (Restangular, $q, $rootScope) {

        var events = Restangular.all('events');

        this.getEvents = function(pageNo){
            return events.getList({page: pageNo}).$object;
        };

        this.getEvent = function(eventId) {
           return events.one(eventId).get();
        };
        this.createEvent = function(event) {
            var deferred = $q.defer();
            events.post(event).then(function(response){
                $rootScope.$broadcast('notify', 'notice', 'The event <strong>'+event.name+'</strong> has been created!', 4000);
                deferred.resolve(response);
            });
            return deferred.promise;
        };
        this.setDescription = function(eventId, desc) {
            return events.one(eventId).post('description',{description:desc});
        }
    }]);