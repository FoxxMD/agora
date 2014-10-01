/**
 * Created by Matthew on 10/1/2014.
 */
// @ngInject
angular.module('gtfest')
    .service('Tournaments', ["Restangular", "$q", "$rootScope","Account", function (Restangular, $q, $rootScope, Account) {

        function RestTour(eventId) {
            return Restangular.all('events').one(eventId).all('tournaments');
        }

        this.getTournaments = function(eventId, pageNo) {
           return RestTour(eventId).getList({page: pageNo});
        };
        this.getTournament = function(eventId, tourId) {
            return RestTour(eventId).one(tourId);
        };
        this.createTournament = function(eventId, tourData) {
            return RestTour(eventId).post(tourData);
        };
    }]);