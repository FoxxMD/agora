/**
 * Created by Matthew on 10/2/2014.
 */
angular.module('gtfest')
    .directive('tournament', tourDirective);
// @ngInject
function tourDirective(Tournaments, Events, Guilds, $state, $stateParams, Account, $q) {
    return {
        restrict: 'E',
        templateUrl: '/views/tournaments/tournament.html',
        controllerAs: 'tourCtrl',
        controller: function ($scope) {
            var that = this;
            this.tour = Tournaments.getCurrent();
            console.log(that.tour.plain());

            this.isAdmin = function() {
                return Account.isEventAdmin(that.event.id) && Account.adminEnabled();
            };
        },
        link: function (scope, elem, attrs) {

        }
    }
}