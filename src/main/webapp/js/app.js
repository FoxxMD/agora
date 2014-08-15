// Declare app level module which depends on filters, and services
angular.module('gtfest', ['ngResource', 'ui.bootstrap', 'restangular', 'ui.router', 'ngCookies', 'ngStorage', 'ui.bootstrap.showErrors', 'ngAnimate', 'ui.validate'],
    function ($stateProvider, $urlRouterProvider, $locationProvider, $httpProvider, RestangularProvider) {
        $stateProvider
            .state('index', {
                abstract: true,
                controller: 'CNCController',
                template: '<div ui-view></div>'
            })
            .state('globalSkeleton', {
                template: '<div class="container"><div ui-view></div></div>',
                abstract: true,
                parent: 'index'
            })
            .state('portal', {
                url: '/{opt:(?:login|register)}',
                params:{
                    opt: {value:null}
                },
                templateUrl: '/views/home/home.html',
                parent: 'globalSkeleton'
            })
            .state('eventSkeleton', {
                templateUrl: '/views/event/skeleton.html',
                abstract: true,
                controller: 'SampleEventController as eventCtrl',
                parent: 'index'
            })
            .state('event', {
                url: '/event/{eventId:[0-9]}/{opt:(?:login|register)}',
                params:{
                    opt: {value:null},
                    eventId: {}
                },
                templateUrl: '/views/event/eventHome.html',
                parent: 'eventSkeleton'
            });
        //TODO make 404 page
        $urlRouterProvider.otherwise('index');
        $locationProvider.html5Mode(true);
        RestangularProvider.setBaseUrl('/api');

    });

angular.module('gtfest').run(function ($rootScope, Restangular, Account, $urlRouter, $location, $state) {
    Restangular.setErrorInterceptor(function (response, deferred, responseHandler) {
        if(response.status === 400) {
            $rootScope.$broadcast('notify','warning', response.data, 6000);
        }
        if (response.status === 401) {
            if (response.headers('ignoreError') == "true")
                return true;
            //TODO redirect user to login page
            $rootScope.$broadcast('notify', 'notice', 'You need to be logged in to do that!', 4000);
        }
        else if (response.status === 403) {
            $rootScope.$broadcast('notify', 'error', response.data, 4000);
        }
        else if (response.status === 500) {
            $rootScope.$broadcast('notify', 'error', response.data, 5000);
        }
    });
    //on startup let's try and get the user from memory
    Account.validateToken().promise.then(function () {
        Account.initUser();
    });
});
