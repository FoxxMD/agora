// Declare app level module which depends on filters, and services
angular.module('gtfest', ['ngResource', 'ui.bootstrap', 'restangular','ui.router', 'ngCookies', 'ngStorage'])
    .config(['$stateProvider', '$urlRouterProvider', '$locationProvider', '$httpProvider', 'RestangularProvider',
        function ($stateProvider, $urlRouterProvider, $locationProvider, $httpProvider, RestangularProvider) {
            $stateProvider
                .state('index', {
                    url: '/',
                    controller: 'HomeController'
                });
            $urlRouterProvider.otherwise('index');
            $locationProvider.html5Mode(true);
            RestangularProvider.setBaseUrl('/api');

            RestangularProvider.setErrorInterceptor(function(response, deferred, responseHandler){
                if(response.status === 401) {
                    if(response.config.ignoreError == false)
                        return true;
                    //TODO redirect user to login page
                    return false;
                }
                else if(response.status === 403){
                    //TODO inform user they don't have correct privileges for that request
                    return false;
                }
            })

        }]);
