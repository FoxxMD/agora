/**
 * Created by Matthew on 9/16/2014.
 */
angular.module('gtfest')
    .controller('ScheduleController', schController);

// @ngInject
function schController($scope, eventData, Events, Account){
    var that = this;
    this.event = eventData;
    this.isAdmin = function() {
        return Account.isEventAdmin(that.event.id) && Account.adminEnabled();
    };
    this.uiConfig = {
        calendar:{
            editable: that.isAdmin(),
            header:{
                left: 'title',
                center: '',
                right: 'today prev,next'
            },
            defaultView: 'agendaDay',
            eventClick: $scope.alertOnEventClick,
            eventDrop: $scope.alertOnDrop,
            eventResize: $scope.alertOnResize
        }
    };
    that.event.details.scheduledEvents = [];
    this.schedule = that.event.details.scheduledEvents;
    this.tryAddEvent = function(){
        that.newTime.title = "A new event!";
        that.event.details.scheduledEvents.push(that.newTime);
        that.newTime = {};
    }
}
schController.$inject = ["$scope", "eventData", "Events", "Account"];