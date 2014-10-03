// Declare app level module which depends on filters, and services
angular.module('gtfest', ['ngResource', 'ui.bootstrap', 'restangular', 'ui.router', 'ngStorage',
        'ui.bootstrap.showErrors', 'ngAnimate', 'ui.validate', 'smart-table', 'angular-loading-bar', 'ngSanitize','angular-ladda',
        'xeditable','angularPayments', 'toggle-switch', 'ui.calendar','infinite-scroll','wu.masonry','ngTagsInput'],
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
                controller: 'GlobalController as globalCtrl',
                resolve: {
                    nothing: function(Events){
                        Events.setCurrentEvent(undefined);
                    }
                }
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
                            Events.setCurrentEvent(response.plain());
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
            .state('eventSkeleton.schedule', {
                url: '/schedule',
                controller:'ScheduleController as scheduleCtrl',
                resolve: {
                    tourData: function(Tournaments, $stateParams) {
                      return Tournaments.getTournaments($stateParams.eventId.toString());
                    }
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
                    eventId:{}
                },
                templateUrl:'/views/tournaments/tournaments.html'
            })
            .state('eventSkeleton.tournament',{
                abstract:true,
                url:'/tournaments/{tournamentId:[0-9]+}',
                template:'<tournament></tournament>',
                resolve:{
                    tournamentData: function(Tournaments, $stateParams, $q, $state) {
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
                    }
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
                url:'',
                templateUrl:'views/tournaments/rules.html',
                params:{
                    eventId:{},
                    tournamentId:{}
                }
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
                    guildData: function(Guilds, $stateParams, $state, $q) {
                        var deferred = $q.defer();
                        Guilds.getGuild($stateParams.guildId.toString()).then(function(response){
                            return deferred.resolve(response);
                        }, function(error){
                            console.log(error);
                            $state.go('eventSkeleton.event',{eventId:$stateParams.eventId});
                            return deferred.reject();
                        });
                        return deferred.promise;
                    }
                }
            })
            .state('eventSkeleton.users',{
                url:'/users',
                template:'<users></users>',
                params:{
                    eventId:{}
                }
            })
            .state('eventSkeleton.profile', {
                url:'/users/:userId',
                params:{
                    eventId:{},
                    userId:{}
                },
                resolve: {
                    userData: function(Account, Events, $stateParams, $state, $q){
                        if(Account.isLoggedIn() && $stateParams.userId == Account.user().id)
                           return Account.user();
                        else {
                            var deferred = $q.defer();
                            Events.getUser($stateParams.eventId,$stateParams.userId.toString()).then(function(response){
                                return deferred.resolve(response);
                            }, function(error){
                                $state.go('eventSkeleton.event',{eventId:$stateParams.eventId});
                                return deferred.reject();
                            });
                            return deferred.promise;
                        }
                    }
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
            })
            .state('globalSkeleton.account', {
                url:'/account',
                templateUrl:'/views/users/account.html',
                controller: 'AccountController as accountCtrl'
            });

        $urlRouterProvider.otherwise('/');
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
                }
                else if(data.createdDate != undefined)
                {
                    data.createdDate = moment(data.createdDate);//new Date(data.createdDate)
                }
                return data;
            });


    }]);

angular.module('gtfest').run(["$rootScope", "Restangular", "Account", "$urlRouter", "$location", "$state","editableOptions", "editableThemes",
    function ($rootScope, Restangular, Account, $urlRouter, $location, $state, editableOptions, editableThemes) {

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
    //on startup try and get the user from memory
    Account.validateToken().then(function () {
        Account.initUser();
    });
}]);
