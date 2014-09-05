/**
 * Created by Matthew on 9/5/2014.
 */
angular.module('gtfest')
.service('Users', ['Restangular', function(Restangular){
    var users = Restangular.all('users');

    this.isRegisteredForEvent = function(user, eventId) {
        return $.grep(user.events, function(e) { return e.id == eventId}).length > 0; //or should it be == 1?
    };
    this.getUser = function(userId) {
        return users.one(userId.toString()).get();
    }

}]);