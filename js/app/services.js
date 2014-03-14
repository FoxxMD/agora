angular.module('app.services', [])
    .service('userService', ['Restangular', '$http', '$localStorage', function (Restangular, $http, $localStorage) {
        var user = {
            id: 0,
            alias: '',
            email: '',
            salt: '',
            paid: false,
            entered: false,
            steam: '',
            bn: '',
            lol: '',
            xbox: '',
            ign: ''
        };

        this.isLoggedIn = function () {

        };

        this.login = function (data) {

        };

        this.register = function (data) {

            var deferred = $q.defer();

            user.email = data.email;
            user.alias = data.alias;
            user.password = data.password;

            $http.post('php/register.php',user).success(function(response){
                if (response == 1) {
                    deferred.resolve();
                }
                else{
                    deferred.reject();
                }
            }).error(function(response){
                    deferred.reject();
                });
            return deferred;
        };
    }]);