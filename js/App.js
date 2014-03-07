//initialize app
var app = angular.module('app', ['ngAnimate', 'ngStorage', 'ui.router', 'ui.bootstrap', 'restangular']);

//configure routing
//hydrate all states for application in order to setup site structure
app.config(['$stateProvider', '$urlRouterProvider',
    function ($stateProvider, $urlRouterProvider) {

        $stateProvider
            .state('index', {
                templateUrl: 'templates/shared/skeleton.html',
                abstract: true,
                controller: 'cnc'
            })
            .state('home', {
                templateUrl: 'templates/home.html',
                url: '/',
                parent: 'index'
            })
            .state('schedule', {
                templateUrl: 'templates/schedule.html',
                url: '/schedule',
                parent: 'index'
            })
            .state('404', {
                templateUrl: 'templates/shared/404.html',
                url: '/404',
                parent: 'index'
            });
        //make sure root goes to home state
        $urlRouterProvider.when('', '/');
        //anything else gets 404'd
        $urlRouterProvider.otherwise('404');
    }]);

//configure RESTful provider -- will be using to hydrate domain model
app.config(['RestangularProvider', '$httpProvider', function (RestangularProvider, $httpProvider) {
    RestangularProvider.setBaseUrl('/');
}]);


/*

 ============== Controllers ==================

 */

//Site skeleton controller
/*

 Will control site navigation and login/register actions and user notification.
 Basically everything on the menu bar.

 */
app.controller('cnc', ['$scope', '$state', '$modal', function ($scope, $state, $modal) {

    $scope.openLogin = function () {

        var modalInstance = $modal.open({
            templateUrl: 'loginModalContent.html',
            controller: ModalLoginCtrl//,
            /*            resolve: {
             items: function () {
             return $scope.items;
             }
             }*/
            //may use this later
        });
    }

    var ModalLoginCtrl = function ($scope, $modalInstance) {
        $scope.login = function () {
            alert("you're logging in!");
        }
        $scope.close = function () {
            $modalInstance.dismiss('canceled');
        }
    };

}]);

app.directive('scheduleDirective', ['$compile', function ($compile) {
    var directive = {
        restrict: 'AE',
        template: '<button class="btn btn-info pull-right" ng-click="changeDay(\'2014-03-08\')">Day 1</button><button class="btn btn-info pull-right" ng-click="changeDay(\'2014-03-09\')">Day 2</button><div id="calendar" style="float:left;"></div>',
        link: function (scope, element, attrs) {

               var calendar = $(element).find('#calendar').calendar({
                    events_source: [
                        {
                            'id': 1,
                            'title': 'Test Event',
                            'start': 1394301600000,
                            'end': 1394308800000,
                            'class':'event-inverse'
                        },
                        {
                            'id': 2,
                            'title': 'Test Event 2',
                            'start': 1394305200000,
                            'end': 1394312400000,
                            'class':'event-inverse'
                        }
                    ],
                    tmpl_path:'js/calendar/tmpls/',
                    view:'day',
                    day: '2014-03-08'
                });

            scope.changeDay = function(theDay){
                calendar.setOptions({day: theDay});
                calendar.view();
            };

        }
    };
    return directive;
}]);