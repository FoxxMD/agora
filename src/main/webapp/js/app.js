// Declare app level module which depends on filters, and services
angular.module('gtfest', ['ngResource', 'ui.bootstrap', 'restangular','ui.router', 'ngCookies'])
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
        }]);
