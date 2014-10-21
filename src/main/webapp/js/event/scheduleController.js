/**
 * Created by Matthew on 9/16/2014.
 */
angular.module('gtfest')
    .controller('ScheduleController', schController);

// @ngInject
function schController($scope, eventData, tourData, Events, Account, $state){
    var that = this;
    this.event = eventData;
    this.isAdmin = function() {
        return Account.isEventAdmin(that.event.id) && Account.adminEnabled();
    };
    this.tour = tourData.plain().map(function(x){
        x.details.location = x.details.location || ['TBD'];
        return x
    });

    var theResources = function() {
        var locations = _.uniq(
            _.flatten(that.tour.map(function(x){return x.details}),'location')
                .concat(that.event.details.scheduledEvents.map(function(x){return x.resourcesName})), function(u){
                return u.toLowerCase();
            });
        return _.map(locations, function(loc) {
           return {
               'id': loc.replace(/[^A-Z0-9]+/ig, "_").toLowerCase(),
               'name': loc,
               'className':[]
           }
        });
    };
    if(that.event.details.scheduledEvents != undefined) {
        that.event.details.scheduledEvents = _.map(that.event.details.scheduledEvents, function(e) {
            e.resources = e.resources != undefined ? e.resources.replace(/[^A-Z0-9]+/ig, "_").toLowerCase() : 'tbd';
            return e;
        });
    }
    else{
        that.event.details.scheduledEvents = [];
    }
    this.uiConfig = {
        calendar:{
            height:'auto',
            //aspectRatio: 2,
/*            header:{
                left: 'title',
                center: '',
                right: 'today prev,next'
            },*/
            slotDuration: '01:00:00',
            resources: theResources(),
            minTime: that.event.details.timeStart.format('hh:mm:ss'),
            slotEventOverlap: false,
            defaultView: 'resourceDay',
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
                var content = element.find('.fc-content');
                if(event.cssClass)
                {
                    element.addClass(event.cssClass);
                }
                if(that.isAdmin())
                {
                    element.find('.fc-content').append('<button id="'+event.id+event.title+'" class="btn btn-danger btn-sm calendarAction">Delete</button>');
                }
                if(event.tourType) {
                    content.append('<p style="margin-bottom:0px">'+event.tourType+'</p>');
                }
                if(event.details)
                {
                    content.append('<p>'+event.details+'</p>');
                }
                if(event.location != undefined) {
                    element.find('.fc-content').append('<p style="margin-top:5px;"><strong>Location:</strong> ' + event.location + '</p>');
                }
                if (event.description)  {
                    element.find('.fc-content').append("<p>" + event.description + "</p>");
                }
            }
        }
    };
    var tourneys = tourData.plain().map(function(x) {
       return {
           title: x.game.name,
           tourType: x.tournamentType.name,
           details: x.details.name,
           location: x.details.locationsub,
           resources: _.map(x.details.location, function(u){ return u.replace(/[^A-Z0-9]+/ig, "_").toLowerCase() }),
           start: x.details.timeStart,
           end: x.details.timeEnd,
           editable: false,
           cssClass:'tournament',
           url:  $state.href('eventSkeleton.tournament.roster',{eventId: eventData.id, tournamentId: x.id})
       }
    });
    $scope.schedule = [that.event.details.scheduledEvents, tourneys];
    function getNewTime() {
        return {
            start: that.event.details.timeStart,
            end: that.event.details.timeStart.clone().add(1, 'h'),
            editable: true,
            startEditable: true,
            durationEditable: true,
            id: Math.floor((Math.random() * 100) + 1)
        }
    }
    this.newTime = getNewTime();
    this.tryAddActivity = function(){
        if($scope.newActivityForm.$valid) {
            that.newTime.resourcesName = that.newTime.resources;
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
schController.$inject = ["$scope", "eventData", "tourData", "Events", "Account", "$state"];
