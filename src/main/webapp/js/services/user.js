/**
 * Created by Matthew on 9/5/2014.
 */
angular.module('gtfest')
    .service('Users', ['Restangular', function (Restangular) {
        var users = Restangular.all('users');

        this.isRegisteredForEvent = function (user, eventId) {
            return $.grep(user.events, function (e) {
                return e.id == eventId
            }).length > 0; //or should it be == 1?
        };
        this.hasPaid = function (user, eventId) {
            return $.grep(user.events, function (e) {
                return e.id == eventId && e.hasPaid
            }).length > 0;
        };
        this.getEventIndex = function (user, eventId) {
            for (var i = 0; i < user.events.length; i++) {
                if (user.events[i].id == eventId)
                    return i;
            }
        };
        this.getUsers = function () {
            return users.getList(); //TODO pagination
        };
        this.getUser = function (userId) {
            return users.one(userId).get();
        };
        this.isEventAdmin = function (user, eventId) {
            var validEvents = $.grep(user.events, function (e) {
                return e.id == eventId && (e.isAdmin || e.isModerator)
            });
            return validEvents.length > 0 ? validEvents[0].isAdmin ? 'A' : 'M' : false;
        };
        this.setHandle = function(userId, handle) {
            return users.one(userId).patch({globalHandle: handle});
        }

    }]);