// Declare app level module which depends on filters, and services
angular.module('gtfest', ['ngResource', 'ui.bootstrap', 'restangular', 'ui.router', 'ngStorage',
        'ui.bootstrap.showErrors', 'ngAnimate', 'ui.validate', 'smart-table', 'angular-loading-bar', 'ngSanitize','angular-ladda',
        'xeditable','angularPayments'],
    ["$stateProvider", "$urlRouterProvider", "$locationProvider", "$httpProvider", "RestangularProvider", function ($stateProvider, $urlRouterProvider, $locationProvider, $httpProvider, RestangularProvider) {
        $stateProvider
            .state('index', {
                abstract: true,
                controller: 'CNCController as cncCtrl',
                resolve: {
                    UAccount: function (Account) {
                        return Account;
                    }
                },
                template: '<div ui-view></div>'
            })
            .state('globalSkeleton', {
                templateUrl: '/views/shared/skeleton.html',
                abstract: true,
                parent: 'index',
                controller: 'GlobalController as globalCtrl'
            })
            .state('globalSkeleton.portal', {
                url: '/{opt:(?:login|register)}',
                params: {
                    opt: {value: null}
                },
                templateUrl: '/views/global/home.html'
            })
            .state('globalSkeleton.events', {
                url: '/events',
                templateUrl: '/views/global/events.html',
                controller: 'EventsController as eventsCtrl'
            })
            .state('globalSkeleton.teams', {
                url: '/teams',
                template: '<teams></teams>',
                data: {
                    teamType: 'global'
                }
            })
            .state('eventSkeleton', {
                templateUrl: '/views/shared/skeleton.html',
                url: '/event/{eventId:[0-9]+}',
                params: {
                    eventId: {}
                },
                resolve: {
                    eventData: function (Events, $stateParams, $state, $q) {
                        var deferred = $q.defer();
                        Events.getEvent($stateParams.eventId.toString()).then(function (response) {
                            return deferred.resolve(response);
                        }, function (error) {
                            $state.go('portal');
                            return deferred.reject();
                        });
                        return deferred.promise;
                    }
                },
                abstract: true,
                controller: 'EventController as eventCtrl',
                parent: 'index'
            })
            .state('eventSkeleton.event', {
                url: '/{opt:(?:login|register)}',
                params: {
                    opt: {value: null},
                    eventId: {}
                },
                templateUrl: '/views/event/eventHome.html'
            })
            .state('eventSkeleton.teams', {
                url: '/teams',
                template: '<teams></teams>',
                params:{
                    eventId:{}
                },
                data: {
                    teamType: 'event'
                }
            })
            .state('eventSkeleton.pay',{
                url:'/pay',
                templateUrl:'/views/event/pay.html',
                controller:'PaymentController as payCtrl',
                params:{
                    eventId:{}
                }
            })
            .state('eventSkeleton.settings', {
                url:'/settings',
                templateUrl:'/views/event/settings.html',
                params:{
                    eventId:{}
                },
                controller:'EventSettingsController as eventSettings'
            });

        //Account related states
        $stateProvider
            .state('registrationConfirm', {
                url: '/confirmRegistration?token',
                params: {
                    token: {}
                },
                controller: function ($rootScope, Account, $stateParams, $state, $location) {
                    Account.confirmRegistration($stateParams.token).then(function (response) {
                        $rootScope.$broadcast('notify', 'notice', 'Account confirmation is complete! Please login.');
                        if (response !== undefined)
                            $location.url('/event/' + response);
                        else
                            $location.url('/');
                        $rootScope.openLogin();
                    }, function () {
                        $location.url('/');
                    });
                },
                parent: 'globalSkeleton'
            });

        $urlRouterProvider.otherwise('/');
        $locationProvider.html5Mode(true);
        RestangularProvider.setBaseUrl('/api');

    }]);

angular.module('gtfest').run(["$rootScope", "Restangular", "Account", "$urlRouter", "$location", "$state","editableOptions", "editableThemes",
    function ($rootScope, Restangular, Account, $urlRouter, $location, $state, editableOptions, editableThemes) {

        editableThemes.bs3.inputClass = 'form-control input-sm';
        editableThemes.bs3.buttonsClass = 'btn-sm';
        editableOptions.theme = 'bs3';

    Restangular.setErrorInterceptor(function (response, deferred, responseHandler) {
        if (response.status === 400) {
            $rootScope.$broadcast('notify', 'warning', response.data, 6000);
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
    Account.validateToken().then(function () {
        Account.initUser();
    });
}]);
