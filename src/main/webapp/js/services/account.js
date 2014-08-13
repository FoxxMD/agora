/**
 * Created by Matthew on 8/6/2014.
 */
angular.module('gtfest')
    .service('Account', ['Restangular', '$localStorage', '$q', function (Restangular, $localStorage, $q) {

        var privUser = undefined;
        this.user = function () {
            return privUser;
        };
        this.isLoggedIn = function () {
            return privUser !== undefined
        };
        this.isAdmin = function () {
            return privUser.role == "admin"
        };
        this.logout = function () {
            privUser = undefined;
            delete $localStorage.authToken;
            Restangular.setDefaultHeaders({});
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
            return deferred;
        };

        this.initUser = function () {
            var deferred = $q.defer();
            Restangular.all('users').one('me').get().then(function (returnedUser) {
                privUser = returnedUser.plain();
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
                    deferred.reject();
                });
            return deferred;
        };
    }]);