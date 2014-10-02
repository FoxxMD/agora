/**
 * Created by Matthew on 10/1/2014.
 */
// @ngInject
angular.module('gtfest')
    .service('Tournaments', ["Restangular", "$q", "$rootScope","Account", function (Restangular, $q, $rootScope, Account) {
        var currentTournament = undefined;

        function RestTour(eventId) {
            return Restangular.all('events').one(eventId).all('tournaments');
        }

        this.getCurrent = function(){
            return currentTournament;
        };

        this.setCurrent = function(tour) {
            currentTournament = tour;
        };

        this.getTournaments = function(eventId, pageNo) {
           return RestTour(eventId).getList({page: pageNo});
        };
        this.getTournament = function(eventId, tourId) {
            return RestTour(eventId).one(tourId).get();
        };
        this.createTournament = function(eventId, tourData) {
            return RestTour(eventId).post(tourData);
        };

        this.isModerator = function(userId, tour) {
            tour = tour || currentTournament;
            return _.find(tour.users, function(tuser) {
                return tuser.id == userId && (tuser.isModerator || tuser.isAdmin);
            });
        };
        this.isAdmin = function(userId, tour) {
            tour = tour || currentTournament;
            return _.find(tour.users, function(tuser) {
                return tuser.id == userId && tuser.isAdmin;
            });
        };
    }]);