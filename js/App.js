//initialize app
var app = angular.module('app', ['ngAnimate', 'ngStorage', 'ui.router', 'ui.bootstrap', 'restangular', 'app.directives']);

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