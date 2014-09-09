/**
 * Created by Matthew on 9/9/2014.
 */
angular.module('gtfest')
    .controller('EventSettingsController', eventSettingsController);

// @ngInject
function eventSettingsController($scope, eventData, $rootScope, Events) {
    this.event = eventData;
}
eventSettingsController.$inject = ["$scope", "eventData", "$rootScope", "Events"];