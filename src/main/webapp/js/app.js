// Declare app level module which depends on filters, and services
angular.module('gtfest', ['ngResource', 'ui.bootstrap', 'restangular','ui.router', 'ngCookies', 'ngStorage'],
        function ($stateProvider, $urlRouterProvider, $locationProvider, $httpProvider, RestangularProvider) {
            $stateProvider
                .state('index', {
                    template: '<smart-menu></smart-menu><div ui-view></div>',
                    abstract:true,
                    controller: 'HomeController'
                })
                .state('portal', {
                    url:'/',
                    templateUrl:'/views/home/home.html',
                    parent:'index'
                })
                .state('sampleEvent',{
                    url:'/sampleEvent',
                    controller:'SampleEventController as eventCtrl',
                    templateUrl:'/views/event/skeleton.html',
                    parent:'index'
                });
            //$urlRouterProvider.otherwise('/');
            /*$locationProvider.html5Mode(true);*/
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
        });
