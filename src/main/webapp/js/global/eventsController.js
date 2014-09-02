/**
 * Created by Matthew on 8/28/2014.
 */
angular.module('gtfest')
    .controller('EventsController', EventsController);

// @ngInject
function EventsController($scope, Events, $state){
    var that = this;
    this.eventsCollection = Events.getEvents();
    this.createEventData = {
        joinType: 'Public',
        details: {}
    };

    this.openStopTime = function($event) {
        $event.preventDefault();
        $event.stopPropagation();
        that.stopOpened = true;
    };
    this.openStartTime = function($event) {
        $event.preventDefault();
        $event.stopPropagation();
        that.startOpened = true;
    };
    this.format = 'dd-MMMM-yyyy';

    this.tryCreateEvent = function(){
        Events.createEvent(that.createEventData).then(function(response){
            that.createEventData = {};
            $scope.$broadcast('show-errors-reset');
            $state.go('eventSkeleton.event',{eventId:response});
        });
    }
}
EventsController.$inject = ["$scope","Events","$state"];
