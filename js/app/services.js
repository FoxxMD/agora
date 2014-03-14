angular.module('app.services', [])
    .service('userService', ['Restangular', '$http', '$localStorage', '$q', function (Restangular, $http, $localStorage, $q) {
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
            ign: '',
            token:null,
            tokenExpire: null
        };

        this.isLoggedIn = function () {
            var d = new Date();
            return (user.token  !== null && (tokenExpire > d.getDate()))
        };

        this.login = function (data) {

            var deferred = $q.defer();

            $http.post({method:'POST',url:'php/users.php', data:data, params:{mode:'verify'}}).success(function (response) {
                if(response.success != undefined && response.success)
                {
                    if(response.authtoken != undefined)
                    {
                     user.token = response.authtoken;
                     user.tokenExpire = new Date();
                     var today = new Date();
                     user.tokenExpire.setDate(today.getDate()+1);
                    }
                }
                deferred.resolve(response);
            }).error(function (response) {
                    deferred.reject(response);
                });
        };

        this.register = function (data) {

            var deferred = $q.defer();

            user.email = data.email;
            user.alias = data.alias;
            user.password = data.password;

            $http.post('php/register.php', user).success(function (response) {
                if (response.success) {
                    user.token = response.authtoken;
                    user.tokenExpire = new Date();
                    var today = new Date();
                    user.tokenExpire.setDate(today.getDate()+1);
                    deferred.resolve();
                }
                else {
                    deferred.reject(response.message);
                }
            }).error(function (response) {
                    deferred.reject();
                });
            return deferred;
        };
    }]);