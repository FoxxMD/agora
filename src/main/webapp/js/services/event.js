/**
 * Created by Matthew on 8/28/2014.
 */
// @ngInject
angular.module('gtfest')
    .service('Events', ["Restangular", "$q", "$rootScope","Account", function (Restangular, $q, $rootScope, Account) {

        var events = Restangular.all('events'),
            currentEvent = undefined;

        var that = this;

        this.getEvents = function(pageNo){
            var deferred = $q.defer();
            events.getList({page: pageNo}).then(function(response){
                deferred.resolve(response.plain());
            });
            return deferred.promise;
        };
        this.setCurrentEvent = function(eventData)
        {
            currentEvent = eventData;
        };

        this.getEvent = function(eventId) {
           return events.one(eventId).get();
        };
        this.getCurrentEvent = function(){
            return currentEvent;
        };
        this.createEvent = function(event) {
            var deferred = $q.defer();
            events.post(event).then(function(response){
                $rootScope.$broadcast('notify', 'notice', 'The event <strong>'+event.name+'</strong> has been created!', 4000);
                deferred.resolve(response);
            });
            return deferred.promise;
        };
        this.updateEvent = function(event) {
          return events.one(event.id.toString()).patch(event);
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
        this.payRegistration = function(eventId, payType, cardToken, userId, paid, receipt) {
            return events.one(eventId).all('users').one(userId).post('payRegistration',{card: cardToken, type: payType, paid: paid, receipt: receipt});
        };
        /*
         * User Functions
         */
        this.joinEvent = function(eventId, userId) {
            if(userId)
                return events.one(eventId).post('users', {userId: userId});
            else
                return events.one(eventId).post('users');
        };
        this.leaveEvent = function(eventId, userId) {
            if(userId)
                return events.one(eventId).all('users').one(userId).remove();
            else
                return events.one(eventId).one('users').remove();
        };
        this.getUsers = function(eventId, pageNo) {
            return events.one(eventId).all('users').getList({page: pageNo}); //TODO pagination
        };
        this.getUser = function(eventId, userId) {
            return events.one(eventId).all('users').one(userId).get();
        };
        this.setPresent = function(eventId, userId, status) {
            return events.one(eventId).all('users').one(userId).patch({isPresent: status });
        };
        this.setModerator = function(eventId, userId, status) {
            return events.one(eventId).all('users').one(userId).patch({isModerator: status });
        };
        this.setAdmin = function(eventId, userId, status) {
            return events.one(eventId).all('users').one(userId).patch({isAdmin: status });
        };
        this.getTeamsAndGuilds = function(eventId, pageNo) {
            var eid = eventId || currentEvent.id.toString();
            return events.one(eid).all('teamsAndGuilds').getList({page: pageNo});
        };
        this.isAdmin = function(user, event){
            var e = event || currentEvent;
            return e != undefined && (_.some(currentEvent.admins, {'id':user.id}));
        };
        this.isModerator = function(user, event){
            var e = event || currentEvent;
            return e != undefined && (that.isAdmin(user, event) || _.some(currentEvent.moderators, {'id':user.id}));
        };

    }]);
