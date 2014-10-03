/**
 * Created by Matthew on 10/3/2014.
 */
angular.module('gtfest')
    .directive('roster', rostDirective);
// @ngInject
function rostDirective(Tournaments, Events, Guilds, $state, $stateParams, Account, $q) {
    return {
        restrict: 'E',
        templateUrl: '/views/tournaments/roster.html',
        controllerAs: 'rosterCtrl',
        controller: function ($scope) {
            var that = this;
        },
        link: function (scope, elem, attrs) {
        }
    }
}
rostDirective.$inject = ["Tournaments", "Events", "Guilds", "$state", "$stateParams", "Account", "$q"];
tourDirective.$inject = ["Tournaments", "Events", "Guilds", "$state", "$stateParams", "Account", "$q"];
