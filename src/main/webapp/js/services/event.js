/**
 * Created by Matthew on 8/28/2014.
 */
// @ngInject
angular.module('gtfest')
    .service('Events', ["Restangular", "$q", "$rootScope","Account", function (Restangular, $q, $rootScope, Account) {

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

        /*
         * Event Info Settings
         */
        this.setDescription = function(eventId, desc) {
            return events.one(eventId).post('description',{description:desc});
        };
        this.setPrivacy = function(eventId, privacy) {
          return events.one(eventId).post('privacy',{privacy:privacy});
        };

        /*
         * Payment Settings
        */
        this.createPayment = function(eventId, paymentInfo) {
            return events.one(eventId).post('payments',paymentInfo);
        };
        this.changePayment = function(eventId, optionId, paymentInfo) {
            return events.one(eventId).one('payments').post(optionId,paymentInfo);
        };
        this.deletePayment = function(eventId, optionId) {
            return events.one(eventId).one('payments').one(optionId).remove();
        };
        /*
         * User Functions
         */
        this.joinEvent = function(eventId, userId) {
            if(userId)
                return events.one(eventId).post('users', {userId: userId});
            else
                return events.one(eventId).post('users');
        }
        this.leaveEvent = function(eventId, userId) {
            if(userId)
                return events.one(eventId).one('users').remove({userId: userId});
            else
                return events.one(eventId).one('users').remove();
        }

    }]);