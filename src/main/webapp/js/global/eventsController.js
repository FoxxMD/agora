/**
 * Created by Matthew on 8/28/2014.
 */
angular.module('gtfest')
    .controller('EventsController', EventsController);

// @ngInject
function EventsController(Events){
    this.eventsCollection = Events.getEvents();
}
