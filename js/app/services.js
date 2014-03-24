angular.module('app.services', [])
    .service('userService', ['Restangular', '$http', '$localStorage', '$q', '$rootScope', function (Restangular, $http, $localStorage, $q, $rootScope) {
        var user = {
                id: 0,
                alias: '',
                email: '',
                paid: false,
                entered: false,
                steam: null,
                bn: null,
                lol: null,
                xbox: null,
                ign: null,
                token: null,
                tokenExpire: null,
                justPaid: false,
                alreadyPaid: false
            },
            isInit = false;

        this.getProfile = function () {
            return user;
        };

        this.getUser = function (id) {
            var deferred = $q.defer();
            $http({method: 'GET', url: 'php/users.php', params: {mode: 'get', id: id}}).success(function (response) {
                deferred.resolve(response);
            }).error(function (response) {
                    deferred.reject();
                });
            return deferred;
        };

        this.getUsers = function () {
            var deferred = $q.defer();
            $http({method: 'GET', url: 'php/users.php', params: {mode: 'getAll'}}).success(function (response) {
                deferred.resolve(response);
            }).error(function (response) {
                    deferred.reject(response);
                });
            return deferred;
        };

        this.initUser = function () {
            if ($localStorage.token != undefined || $localStorage.token != null) {
                user.alias = $localStorage.alias;
                user.email = $localStorage.email;
                user.token = $localStorage.token;
                user.id = $localStorage.id;
                user.tokenExpire = $localStorage.tokenExpire;
                $http.defaults.headers.common.Authentication = $localStorage.token;

                this.getUser(user.id).promise.then(function(response){
                    user = response;
                    user.paid = (response.paid == 1);
                });
            }
            isInit = true;
        };

        this.alreadyPaid = function(action)
        {
            user.alreadyPaid = action;
        };
        this.justPaid = function(action)
        {
            user.justPaid = action;
        };


        this.isLoggedIn = function () {
            if (!isInit) {
                this.initUser();
            }
            var d = new Date();
            return (user.token !== null) //(user.tokenExpire > d.getDate()) need to fix expiration compare
        };

        this.login = function (data) {

            var deferred = $q.defer();
            $http({method: 'POST', url: 'php/users.php', data: data, params: {mode: 'verify'}}).success(function (response) {
                if (response.success != undefined && response.success) {
                    if (response.authtoken != undefined) {
                        user.token = response.authtoken;
                        user.id = response.id;
                        user.alias = response.alias;
                        user.email = data.email;
                        $http.defaults.headers.common.Authentication = response.authtoken;
                        $localStorage.token = response.authtoken;
                        $localStorage.alias = response.alias;
                        $localStorage.id = response.id;
                        $localStorage.email = data.email;
                        user.tokenExpire = new Date();
                        var today = new Date();
                        user.tokenExpire.setDate(today.getDate() + 1);
                        $localStorage.tokenExpire = user.tokenExpire;
                    }
                    $rootScope.$broadcast('loginChange');
                    deferred.resolve();
                }
                else{
                    deferred.reject(response.message);
                }
            }).error(function (response) {
                    deferred.reject(response);
                });
            return deferred;
        };

        this.register = function (data) {
            var that = this;
            var deferred = $q.defer();

            user.email = data.email;
            user.alias = data.alias;

            $http.post('php/register.php', data).success(function (response) {
                if (response.success) {
                    var loginData = {email: user.email, password: data.password};
                    that.login(loginData);
                    deferred.resolve();
                }
                else {
                    deferred.reject(response.message);
                }
            }).error(function (response) {
                    deferred.reject(response.message);
                });
            return deferred;
        };

        this.payRegistration = function (data) {
            var deferred = $q.defer();
            $http({method: 'POST', url: 'php/users.php', data: data, params: {mode: 'pay'}}).success(function (response) {
                if (response.success != undefined && response.success) {
                    user.paid = true;
                    deferred.resolve(response.message);
                }
                else {
                    deferred.reject(response.message);
                }
            }).error(function (response) {
                    deferred.reject(response.message);
                });
            return deferred;
        };

        this.resetPassword = function(data){
            var deferred = $q.defer();
            $http({method: 'POST', url: 'php/users.php', data: data, params: {mode: 'resetPassword'}}).success(function (response) {
                if (response.success != undefined && response.success) {
                    deferred.resolve();
                }
                else {
                    deferred.reject(response.message);
                }
            }).error(function (response) {
                    deferred.reject(response.message);
                });
            return deferred;
        };

        this.changePassword = function(data) {
            var deferred = $q.defer();
            $http({method: 'POST', url: 'php/users.php', data: data, params: {mode: 'changePassword'}}).success(function (response) {
                if (response.success != undefined && response.success) {
                    deferred.resolve();
                }
                else {
                    deferred.reject(response);
                }
            }).error(function (response) {
                    deferred.reject(response);
                });
            return deferred;
        };

        this.updateUser = function (param, paramValue) {

            var deferred = $q.defer();

            var postData = {
                id: user.id,
                param: param,
                updatevalue: paramValue
            };
            $http({method: 'POST', url: 'php/users.php', data: postData, params: {mode: 'set'}}).success(function (response) {
                if (response.success != undefined && response.success) {
                    user[param] = paramValue;
                    if (param == 'alias')
                        $localStorage.alias = paramValue;
                    deferred.resolve();
                }
                else {
                    deferred.reject("Error updating");
                }
            }).error(function (response) {
                    deferred.reject("Error updating");
                });
            return deferred;
        };

        this.logoff = function () {
            $localStorage.$reset();
            user = {
                id: 0,
                alias: '',
                email: '',
                paid: false,
                entered: false,
                steam: null,
                bn: null,
                lol: null,
                xbox: null,
                ign: null,
                token: null,
                tokenExpire: null
            };
            $rootScope.$broadcast('loginChange');
        };

    }])
    .service('teamService', ['$http', '$q', function ($http, $q) {

        this.getTeams = function () {
            var deferred = $q.defer();

            $http({method: 'GET', url: 'php/teams.php', params: {mode: 'getAll'}}).success(function (response) {
                deferred.resolve(response);
            }).error(function (response) {
                    deferred.reject('Error getting teams');
                });
            return deferred;
        };

        this.getTeam = function (id) {
            var deferred = $q.defer();

            $http({method: 'GET', url: 'php/teams.php', params: {mode: 'get', id: id}}).success(function (response) {
                deferred.resolve(response);
            }).error(function (response) {
                    deferred.reject('Error getting team');
                });
            return deferred;
        }

        this.createTeam = function (postData) {
            var deferred = $q.defer();

            $http({method: 'POST', url: 'php/teams.php', data: postData, params: {mode: 'create'}}).success(function (response) {
                if (response.success)
                    deferred.resolve();
                else
                    deferred.reject(response);
            }).error(function (response) {
                    deferred.reject('Error creating team');
                });
            return deferred;
        }

        this.updateTeam = function (param, paramValue, teamId) {

            var deferred = $q.defer();

            var postData = {
                teamId: teamId,
                param: param,
                updatevalue: paramValue
            };
            $http({method: 'POST', url: 'php/teams.php', data: postData, params: {mode: 'set'}}).success(function (response) {
                if (response.success != undefined && response.success) {
                    deferred.resolve();
                }
                else {
                    deferred.reject(response.message);
                }
            }).error(function (response) {
                    deferred.reject(response);
                });
            return deferred;
        };

        this.addMember = function (teamId, id, password) {
            var data = {teamId: teamId, id: id, password: password},
                deferred = $q.defer();
            $http({method: 'POST', url: 'php/teams.php', data: data, params: {mode: 'add'}}).success(function (response) {
                if (response.success != undefined && response.success) {
                    deferred.resolve();
                }
                else {
                    deferred.reject(response.message);
                }
            });
            return deferred;
        };

    }]);