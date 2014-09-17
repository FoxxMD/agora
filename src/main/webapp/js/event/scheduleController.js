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
            header:{
                left: 'title',
                center: '',
                right: 'today prev,next'
            },
            defaultView: 'agendaDay',
            defaultDate: that.event.details.timeStart,
            timezone: 'local',
            eventDrop: function(event, dayDelta, minuteDelta, allDay, revertFunc, jsEvent, ui, view) {
                for(var i = 0; i < that.event.details.scheduledEvents.length; i++)
                {
                    if(that.event.details.scheduledEvents[i].title == event.title && that.event.details.scheduledEvents[i].id == event.id) {
                        that.event.details.scheduledEvents[i].start = event.start._d;
                        that.event.details.scheduledEvents[i].end = event.end._d;
                    }
                }
                Events.updateEvent(that.event)
            },
            eventResize: function(event, dayDelta, minuteDelta, revertFunc, jsEvent, ui, view){
                for(var i = 0; i < that.event.details.scheduledEvents.length; i++)
                {
                    if(that.event.details.scheduledEvents[i].title == event.title && that.event.details.scheduledEvents[i].id == event.id)
                    {
                        that.event.details.scheduledEvents[i].start = event.start._d;
                        that.event.details.scheduledEvents[i].end = event.end._d;
                    }
                }
                Events.updateEvent(that.event)
            },
            eventRender: function(event, element) {
                if(that.isAdmin())
                {
                    element.find('.fc-content').append('<button id="'+event.id+event.title+'" class="btn btn-danger btn-sm calendarAction">Delete</button>');
                }

                if(event.location) {
                    element.find('.fc-title').after("<p>Location: " + event.location + "</p>");
                }
                if (event.description)  {
                    element.find('.fc-content').append("<p>" + event.description + "</p>");
                }
            }
        }
    };
    that.event.details.scheduledEvents = that.event.details.scheduledEvents || [];
    $scope.schedule = [that.event.details.scheduledEvents];
    function getNewTime() {
        return {
            start: that.event.details.timeStart,
            end: that.event.details.timeStart,
            editable: true,
            startEditable: true,
            durationEditable: true,
            id: Math.floor((Math.random() * 100) + 1)
        }
    }
    this.newTime = getNewTime();
    this.tryAddActivity = function(){
        if($scope.newActivityForm.$valid) {
            that.event.details.scheduledEvents.push(that.newTime);
            Events.updateEvent(that.event).then(function(){
                $scope.$emit('notify','notice','Event added.');
            });
            that.newTime = getNewTime();
        }
    };
    $(document).on('click', '.calendarAction', function(event){
        var ident = null;
        for(var i = 0; i < that.event.details.scheduledEvents.length; i++)
        {
            if(that.event.details.scheduledEvents[i].id+that.event.details.scheduledEvents[i].title == event.target.attributes.id.value)
                ident = i;
        }
        if(ident != null)
        {
            that.event.details.scheduledEvents.splice(ident,1);
            Events.updateEvent(that.event).then(function(){
                $scope.$emit('notify','notice','Event removed.');
            });
        }
    });
}
schController.$inject = ["$scope", "eventData", "Events", "Account"];