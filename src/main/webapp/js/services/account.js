/**
 * Created by Matthew on 8/6/2014.
 */
angular.module('gtfest')
    .service('Account', ['Restangular', '$localStorage', '$q', function (Restangular, $localStorage, $q) {

        var privUser = {};
        this.user = function() { return privUser; };

        this.validateToken = function() {
        /* Use this method to determine whether the client has a stored authtoken and validate it.
         */
            var deferred = $q.defer();
            if($localStorage.authToken !== null)
            {
                Restangular.one('login').post(null,[{"Authorization":$localStorage.authToken},{'ignoreError':true}])
                    .then(function(){
                        console.log('Auth token valid.');
                        Restangular.setDefaultHeaders({Authorization:$localStorage.authToken});
                        deferred.resolve();
                    },function(response){
                        console.log('Auth token invalid.');
                        deferred.reject();
                    })
            }
            else{
                deferred.reject();
            }
            return deferred;
        };

        this.initUser = function() {
            var deferred = $q.defer();
            Restangular.one('me').get().then(function(returnedUser){
                privUser = returnedUser.$object;
                deferred.resolve();
            });
            return deferred;
        };
        this.login = function(email, password) {
            var deferred = $q.defer();
            Restangular.one('login').get([{'email':email},{'password':password}],{'ignoreError':true})
                .then(function(response){
                //correctly logged in
                    // Restangular.setDefaultHeaders({Authorization:request.getHeader('Authorization')}); ???
                    deferred.resolve();
            }, function(response){
                //failed to log in
                    deferred.reject();
            });
            return deferred;
        };
    }]);