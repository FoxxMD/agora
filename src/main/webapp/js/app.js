// Declare app level module which depends on filters, and services
angular.module('gtfest', ['ngResource', 'ui.bootstrap', 'restangular','ui.router', 'ngCookies', 'ngStorage'],
        function ($stateProvider, $urlRouterProvider, $locationProvider, $httpProvider, RestangularProvider) {
            $stateProvider
                .state('index', {
                    template: '<smart-menu></smart-menu><div ui-view></div>',
                    abstract:true,
                    controller: 'CNCController'
                })
                .state('globalSkeleton', {
                    template:'<div class="container"><div ui-view></div></div>',
                    abstract:true,
                    parent:'index'
                })
                .state('portal', {
                    url:'/',
                    templateUrl:'/views/home/home.html',
                    parent:'globalSkeleton'
                })
                .state('eventSkeleton', {
                    templateUrl:'/views/event/skeleton.html',
                    abstract: true,
                    controller:'SampleEventController as eventCtrl',
                    parent:'index'
                })
                .state('sampleEvent',{
                    url:'/sampleEvent',
                    templateUrl:'/views/event/eventHome.html',
                    parent:'eventSkeleton'
                });
            //TODO make 404 page
            $urlRouterProvider.otherwise('/');
            $locationProvider.html5Mode(true);
            RestangularProvider.setBaseUrl('/api');

        });

angular.module('gtfest').run(function($rootScope, Restangular, Account){
    Restangular.setErrorInterceptor(function(response, deferred, responseHandler){
        if(response.status === 401) {
            if(response.headers('ignoreError') == "true")
                return true;
            //TODO redirect user to login page
            $rootScope.$broadcast('notify','notice','You need to be logged in to do that!');
            return false;
        }
        else if(response.status === 403){
            $rootScope.$broadcast('notify','warning',response.body);
            //TODO inform user they don't have correct privileges for that request
            return false;
        }
        else if(response.status === 500){
            $rootScope.$broadcast('notify','error',response.body);
            return false;
        }
    });
    //on startup let's try and get the user from memory
    Account.validateToken().promise.then(function(){
        Account.initUser();
    });
});
