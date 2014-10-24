// Declare app level module which depends on filters, and services
// @ngInject
angular.module('gtfest', ['ui.bootstrap', 'restangular', 'ui.router', 'ngStorage', 'ngTouch',
        'ui.bootstrap.showErrors', 'ngAnimate', 'ui.validate', 'angular-loading-bar', 'ngSanitize','angular-ladda',
        'xeditable','angularPayments', 'ui.calendar','infinite-scroll','wu.masonry','ngTagsInput','ui.tree',
        'angulartics', 'angulartics.google.analytics', 'toaster', 'ngFlowtype', 'toggle-switch'],
    ["$stateProvider", "$urlRouterProvider", "$locationProvider", "$httpProvider", "RestangularProvider", "$analyticsProvider",
        function ($stateProvider, $urlRouterProvider, $locationProvider, $httpProvider, RestangularProvider, $analyticsProvider) {
        $stateProvider
            .state('index', {
                abstract: true,
                controller: 'CNCController as cncCtrl',
                resolve: {
                    UAccount: /*@ngInject*/ ["Account", function (Account) {
                        return Account;
                    }]
                },
                template: '<div ui-view></div>'
            })
            .state('globalSkeleton', {
                templateUrl: '/views/shared/skeleton.html',
                abstract: true,
                parent: 'index',
                controller: 'GlobalController as globalCtrl',
                resolve: {
                    nothing: /*@ngInject*/ ["Events", function(Events){
                        Events.setCurrentEvent(undefined);
                    }]
                }
            })
            /*.state('globalSkeleton.portal', {
                url: '/{opt:(?:login|register)}',
                params: {
                    opt: {value: null}
                },
                templateUrl: '/views/global/home.html'
            })
            .state('globalSkeleton.events', {
                url: '/events',
                template: '<events></events>'
            })
            .state('globalSkeleton.guilds', {
                url: '/guilds',
                template: '<guilds></guilds>'
            })
            .state('globalSkeleton.users',{
                url:'/users',
                template:'<users></users>'
            })
            .state('globalSkeleton.profile', {
                url:'/users/:userId',
                params:{
                    userId:{}
                },
                resolve: {
                    userData: function(Account, Users, $stateParams, $state, $q){
                        if(Account.isLoggedIn() && $stateParams.userId == Account.user().id)
                            return Account.user();
                        else {
                            var deferred = $q.defer();
                            Users.getUser($stateParams.userId.toString()).then(function(response){
                                return deferred.resolve(response);
                            }, function(error){
                                $state.go('globalSkeleton.portal');
                                return deferred.reject();
                            });
                            return deferred.promise;
                        }
                    }
                },
                templateUrl:'/views/users/profile.html',
                controller:'ProfileController as profileCtrl'
            })
            .state('globalSkeleton.guild', {
                url:'/guilds/:guildId',
                params:{
                    guildId:{}
                },
                templateUrl:'/views/guilds/guild.html',
                controller:'GuildController as guildCtrl',
                resolve:{
                    guildData: function(Guilds, $stateParams, $state, $q) {
                        var deferred = $q.defer();
                        Guilds.getGuild($stateParams.guildId.toString()).then(function(response){
                            return deferred.resolve(response);
                        }, function(error){
                            console.log(error);
                            $state.go('globalSkeleton.guilds');
                            return deferred.reject();
                        });
                        return deferred.promise;
                    }
                }
            })*/
            .state('eventSkeleton', {
                templateUrl: '/views/shared/skeleton.html',
                url: '/event/{eventId:[0-9]+}',
                params: {
                    eventId: {}
                },
                resolve: {
                    eventData: /*@ngInject*/ ["Events", "$stateParams", "$state", "$q", function (Events, $stateParams, $state, $q) {
                        var deferred = $q.defer();
                        Events.getEvent($stateParams.eventId.toString()).then(function (response) {
                            Events.setCurrentEvent(response.plain());
                            return deferred.resolve(response);
                        }, function (error) {
                            $state.go('portal');
                            return deferred.reject();
                        });
                        return deferred.promise;
                    }]
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
            .state('eventSkeleton.schedule', {
                url: '/schedule',
                controller:'ScheduleController as scheduleCtrl',
                resolve: {
                    tourData: /*@ngInject*/ ["Tournaments", "$stateParams", function(Tournaments, $stateParams) {
                      return Tournaments.getTournaments($stateParams.eventId.toString());
                    }]
                },
                params: {
                    eventId: {}
                },
                templateUrl: '/views/event/schedule.html'
            })
            .state('eventSkeleton.tournaments', {
                url:'/tournaments',
                controller:'TournamentsController as toursCtrl',
                params: {
                    eventId:{},
                    gameFilter:{value: null}
                },
                templateUrl:'/views/tournaments/tournaments.html'
            })
            .state('eventSkeleton.tournament',{
                abstract:true,
                url:'/tournaments/{tournamentId:[0-9]+}',
                template:'<tournament></tournament>',
                resolve:{
                    tournamentData: /*@ngInject*/ ["Tournaments", "$stateParams", "$q", "$state", function(Tournaments, $stateParams, $q, $state) {
                        var deferred = $q.defer();
                        Tournaments.getTournament($stateParams.eventId.toString(), $stateParams.tournamentId.toString()).then(function(response)
                        {
                            Tournaments.setCurrent(response);
                            return deferred.resolve(response);
                        }, function(error){
                            $state.go('eventSkeleton.tournaments',{eventId: $stateParams.eventId});
                            console.log(error);
                            return deferred.reject();
                        });
                        return deferred.promise;
                    }]
                },
                params:{
                    eventId:{},
                    tournamentId:{}
                }
            })
            .state('eventSkeleton.tournament.roster', {
                url:'',
                template:'<roster></roster>',
                params:{
                    eventId:{},
                    tournamentId:{}
                }
            })
            .state('eventSkeleton.tournament.rules',{
                url:'/rules',
                templateUrl:'views/tournaments/rules.html',
                params:{
                    eventId:{},
                    tournamentId:{}
                },
                controller:'RulesController as rulesCtrl'
            })
            .state('eventSkeleton.tournament.prizes',{
                url:'/prizes',
                templateUrl:'views/tournaments/prizes.html',
                params:{
                    eventId:{},
                    tournamentId:{}
                },
                controller:'PrizesController as prizesCtrl'
            })
            .state('eventSkeleton.tournament.admin',{
                url:'/admin',
                templateUrl:'views/tournaments/admin.html',
                params:{
                    eventId:{},
                    tournamentId:{}
                },
                controller:'TourAdminController as adminCtrl'
            })
            .state('eventSkeleton.tournament.streamsandservers',{
                url:'/streamsandservers',
                templateUrl:'views/tournaments/streamsandservers.html',
                params:{
                    eventId:{},
                    tournamentId:{}
                },
                controller:'SSController as ssCtrl'
            })
            .state('eventSkeleton.guilds', {
                url: '/teams',
                template: '<guilds></guilds>',
                params:{
                    eventId:{}
                }
            })
            .state('eventSkeleton.guild', {
                url:'/guilds/:guildId',
                params:{
                    guildId:{},
                    eventId:{}
                },
                templateUrl:'/views/guilds/guild.html',
                controller:'GuildController as guildCtrl',
                resolve:{
                    guildData: /*@ngInject*/ ["Guilds", "$stateParams", "$state", "$q", function(Guilds, $stateParams, $state, $q) {
                        var deferred = $q.defer();
                        Guilds.getGuild($stateParams.guildId.toString()).then(function(response){
                            return deferred.resolve(response);
                        }, function(error){
                            console.log(error);
                            $state.go('eventSkeleton.event',{eventId:$stateParams.eventId});
                            return deferred.reject();
                        });
                        return deferred.promise;
                    }]
                }
            })
            .state('eventSkeleton.users',{
                url:'/users',
                template:'<users></users>',
                params:{
                    eventId:{}
                }
            })
            .state('eventSkeleton.onSiteRegister', {
                url:'',
                templateUrl:'/views/event/adminRegister.html',
                resolve:{
                    nothing:/*@ngInject*/ ["Account","$stateParams","$state", function(Account, $stateParams, $state){
                        if(Account.isAdmin() || Account.isEventAdmin($stateParams.eventId.toString())){
                            return true;
                        }
                        else{
                            $state.go('eventSkeleton.event');
                            return false;
                        }
                    }]
                }
            })
            .state('eventSkeleton.profile', {
                url:'/users/:userId',
                params:{
                    eventId:{},
                    userId:{}
                },
                resolve: {
                    userData: /*@ngInject*/ ["Account", "Events", "$stateParams", "$state", "$q", function(Account, Events, $stateParams, $state, $q){
                        if(Account.isLoggedIn() && $stateParams.userId == Account.user().id)
                           return Account.user();
                        else {
                            var deferred = $q.defer();
                            Events.getUser($stateParams.eventId.toString(),$stateParams.userId.toString()).then(function(response){
                                return deferred.resolve(response);
                            }, function(error){
                                $state.go('eventSkeleton.event',{eventId:$stateParams.eventId});
                                return deferred.reject();
                            });
                            return deferred.promise;
                        }
                    }]
                },
                templateUrl:'/views/users/profile.html',
                controller:'ProfileController as profileCtrl'
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
            })
            .state('eventSkeleton.about', {
                url:'/about',
                templateUrl:'/views/event/about.html',
                params:{
                    eventId:{}
                },
                controller:'EventAboutController as aboutCtrl'
            })
            .state('eventSkeleton.faq', {
                url:'/faq',
                templateUrl:'/views/event/faq.html',
                params:{
                    eventId:{}
                },
                controller: 'EventFaqController as faqCtrl'
            })
            .state('eventSkeleton.aboutPlatform',{
                url:'',
                templateUrl:'/views/global/about.html',
                params:{
                    eventId:{}
                }
            });

        //Account related states
        $stateProvider
            .state('registrationConfirm', {
                url: '/confirmRegistration?token',
                params: {
                    token: {}
                },
                controller: /*@ngInject*/ ["$rootScope", "Account", "$stateParams", "$state", "$location", function ($rootScope, Account, $stateParams, $state, $location) {
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
                }],
                parent: 'globalSkeleton'
            })
            .state('globalSkeleton.passwordreset', {
                url:'/passwordReset?token',
                params:{
                    token:{}
                },
                templateUrl:'/views/shared/passwordReset.html',
                controller: /*@ngInject*/ ["$scope", "$stateParams", "Account", "$location", "$rootScope", function($scope, $stateParams, Account, $location, $rootScope) {
                    Account.validateForgottenPasswordToken($stateParams.token).then(function(){
                        $scope.passwordData = {
                            token: $stateParams.token
                        }
                    }, function(){
                        $location.url('/');
                    });
                    $scope.passwordResetSubmit = function(form) {
                        $scope.$broadcast('show-errors-check-validity');
                        if(form.$valid){
                            $scope.resetLoading = true;
                            Account.resetForgottenPassword($scope.passwordData).then(function(){
                                $rootScope.$broadcast('notify', 'notice', 'Password has been changed! Please login.');
                                $location.url('/');
                                $rootScope.openLogin();
                            }).finally(function(){
                                $scope.resetLoading = false;
                            })
                        }
                    }
                }]
            })
            .state('eventSkeleton.account', {
                url:'/account',
                params:{
                    eventId:{}
                },
                templateUrl:'/views/users/account.html',
                controller: 'AccountController as accountCtrl'
            });

        $urlRouterProvider.when('','/event/1');
        $urlRouterProvider.otherwise('/event/1');
        $locationProvider.html5Mode(true);
        RestangularProvider.setBaseUrl('/api');

        //Make sure we transform dates into Date() objects on response data (since restangular doesn't do it automatically)
        RestangularProvider.addResponseInterceptor(function(data, operation, what, url, response, deferred)
            {
                if(operation === "getList" && data.length > 0)
                {
                    if(data[0].details != undefined)
                    {
                       for(var i = 0; i < data.length; i++)
                       {
                           data[i].details.timeStart = moment(data[i].details.timeStart);//new Date(data[i].details.timeStart);
                           data[i].details.timeEnd = moment(data[i].details.timeEnd);//new Date();
                           if(data[i].details.rules)
                               data[i].details.rules = angular.fromJson(data[i].details.rules);
                           if(data[i].details.servers)
                               data[i].details.servers = angular.fromJson(data[i].details.servers);
                           if(data[i].details.streams)
                               data[i].details.streams = angular.fromJson(data[i].details.streams);
                           if(data[i].details.prizes)
                               data[i].details.prizes = angular.fromJson(data[i].details.prizes);
                           if(data[i].details.location)
                               data[i].details.location = angular.fromJson(data[i].details.location);
                           if(data[i].details.faq)
                               data[i].details.faq = angular.fromJson(data[i].details.faq);
                       }
                    }
                    if(data[0].createdDate != undefined)
                    {
                        for(var u = 0; u < data.length; u++)
                        {
                            data[u].createdDate = moment(data[u].createdDate);//new Date(data[u].createdDate);
                        }
                    }
                }
                else if(data.details != undefined)
                {
                    data.details.timeStart = moment(data.details.timeStart);
                    data.details.timeEnd = moment(data.details.timeEnd);//new Date(data.details.timeEnd);
                    if(data.details.rules)
                        data.details.rules = angular.fromJson(data.details.rules);
                    if(data.details.servers)
                        data.details.servers = angular.fromJson(data.details.servers);
                    if(data.details.streams)
                        data.details.streams = angular.fromJson(data.details.streams);
                    if(data.details.prizes)
                        data.details.prizes = angular.fromJson(data.details.prizes);
                    if(data.details.credits)
                        data.details.credits = angular.fromJson(data.details.credits);
                    if(data.details.location)
                        data.details.location = angular.fromJson(data.details.location);
                    if(data.details.faq)
                        data.details.faq = angular.fromJson(data.details.faq);
                }
                else if(data.createdDate != undefined)
                {
                    data.createdDate = moment(data.createdDate);//new Date(data.createdDate)
                }
                return data;
            });

            $analyticsProvider.firstPageview(true); /* Records pages that don't use $state or $route */
            $analyticsProvider.withAutoBase(true);  /* Records full path */

    }]);
// @ngInject
angular.module('gtfest').run(["$rootScope", "Restangular", "Account", "$urlRouter", "$location", "$state","editableOptions", "editableThemes",'$http',
    function ($rootScope, Restangular, Account, $urlRouter, $location, $state, editableOptions, editableThemes, $http) {

        FastClick.attach(document.body);
        //This is a stand-in for angular's regular transformer where tests must pass in order to transform
        //Need this because I am returning all resposnes as json/application from scalatra. This needs to be fixed server side
        //But this will do for now
/*        var JSON_START = /^\s*(\[|\{[^\{])/,
            JSON_END = /[\}\]]\s*$/,
            PROTECTION_PREFIX = /^\)\]\}',?\n/,
            APPLICATION_JSON = 'application/json',
            CONTENT_TYPE_APPLICATION_JSON = {'Content-Type': APPLICATION_JSON + ';charset=utf-8'};
        var parseResponse = function(data, headers) {
            if (angular.isString(data)) {
                // strip json vulnerability protection prefix
                data = data.replace(PROTECTION_PREFIX, '');
                var contentType = headers('Content-Type');
                if ((contentType && contentType.indexOf(APPLICATION_JSON) === 0) &&
                    (JSON_START.test(data) && JSON_END.test(data))) {
                    data = angular.fromJson(data);
                }
            }
            return data;
        };

        $http.defaults.transformResponse = parseResponse;*/

        editableThemes.bs3.inputClass = 'form-control input-sm';
        editableThemes.bs3.buttonsClass = 'btn-sm';
        editableOptions.theme = 'bs3';

        //set momnetjs calendar formatting
        moment.locale('en', {
            calendar : {
                lastDay : '[Yesterday at] LT',
                sameDay : '[Today at] LT',
                nextDay : '[Tomorrow at] LT',
                lastWeek : '[Last] dddd [at] LT',
                nextWeek : 'dddd [at] LT',
                sameElse : 'L [at] LT'
            }
        });

    Restangular.setErrorInterceptor(function (response, deferred, responseHandler) {
        if(typeof response.data === 'string'){
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
        }
    });
    //on startup try and get the user from memory
    Account.validateToken().then(function () {
        Account.initUser();
    });
}]);
