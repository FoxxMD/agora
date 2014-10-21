/**
 * Created by Matthew on 8/6/2014.
 */
// @ngInject
angular.module('gtfest')
    .service('Account', ['Restangular', '$localStorage', '$q','$rootScope','Users', function (Restangular, $localStorage, $q, $rootScope, Users) {
        $localStorage.reminders = $localStorage.reminders || {};

        var privUser = undefined;

        this.user = function () {
            return privUser;
        };
        this.isLoggedIn = function () {
            return privUser !== undefined
        };
        this.isAdmin = function () {
            if(!privUser)
                return false;
            return privUser.role == "admin";
        };
        this.isEventAdmin = function(eventId) {
            if(!privUser)
                return false;
            return Users.isEventAdmin(privUser, eventId);
        };
        this.adminEnabled = function() {
            return $localStorage.adminEnabled;
        };
        this.toggleAdmin = function(truthy) {
            $localStorage.adminEnabled = truthy;
        };
        this.isRegisteredForEvent = function(eventId) {
            if(!privUser)
                return false;
            return Users.isRegisteredForEvent(privUser, eventId);
        };
        this.hasPaid = function(eventId) {
            if(!privUser)
                return false;
            return Users.hasPaid(privUser, eventId);
        };
        this.logout = function () {
            privUser = undefined;
            delete $localStorage.authToken;
            Restangular.setDefaultHeaders({});
            $rootScope.$broadcast('accountStatusChange');
        };

        this.validateToken = function () {
            /* Use this method to determine whether the client has a stored authtoken and validate it.
             */
            var deferred = $q.defer();
            if ($localStorage.authToken !== undefined) {
                Restangular.one('login').get({}, {Authorization: $localStorage.authToken})
                    .then(function (something) {
                        Restangular.setDefaultHeaders({Authorization: $localStorage.authToken});
                        deferred.resolve();
                    }, function (response) {
                        deferred.reject();
                    });
            }
            else {
                deferred.reject();
            }
            return deferred.promise;
        };

        this.initUser = function () {
            var deferred = $q.defer();
            Restangular.all('users').one('me').get().then(function (returnedUser) {
                privUser = returnedUser.plain();
                $localStorage.reminders[privUser.id] = $localStorage.reminders[privUser.id] || {};
                $rootScope.$broadcast('accountStatusChange');
                deferred.resolve();
            });
            return deferred;
        };
        this.login = function (email, password) {
            var deferred = $q.defer();
            Restangular.one('login').get({email: email, password: password})
                .then(function (response) {
                    //correctly logged in
                    Restangular.setDefaultHeaders(response);
                    $localStorage.authToken = response;
                    deferred.resolve();
                }, function (response) {
                    //failed to log in
                    deferred.reject(response);
                });
            return deferred;
        };
        this.forgotPassword = function(email) {
            return Restangular.all('forgotPassword').post({email: email});
        };
        this.validateForgottenPasswordToken = function(token) {
            return Restangular.one('passwordReset').get({token:token});
        };
        this.resetForgottenPassword = function(data) {
            return Restangular.all('passwordReset').post(data);
        };
        //eventId is OPTIONAL
        this.register = function(handle, email, password, eventId, noconfirm) {
            var deferred = $q.defer();
            Restangular.all('register').post({handle: handle, email: email, password: password, eventId: eventId, noconfirm: noconfirm}).then(function(response)
            {
                deferred.resolve(response);
            },
            function(response){
               return deferred.reject(response);
            });
            return deferred;
        };
        this.confirmRegistration = function(token) {
           return Restangular.one('confirmRegistration').get({token:token});
        }
    }]);
