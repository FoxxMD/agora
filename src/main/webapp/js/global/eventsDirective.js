/**
 * Created by Matthew on 8/28/2014.
 */
angular.module('gtfest')
    .directive('events', eventsDirective);

// @ngInject
function eventsDirective(Events, $state, Account, $timeout, $q){
    return {
        templateUrl:'/views/global/events.html',
        restrict:'E',
        controllerAs:'eventsCtrl',
        controller: /*@ngInject*/ ["$scope", function($scope){
            var that = this;
            var pageNo = 1;
            this.state = $state;
            Events.getEvents().then(function(response){
                that.eventsCollection = response;
            });
            this.createEventData = {
                joinType: 'Public',
                details: {}
            };
            this.getMoreEvents = function(){
                that.busy = true;
                pageNo++;
                Events.getEvents(pageNo).then(function(response){
                    if(response.length > 0)
                    {
                        that.eventsCollection=  that.eventsCollection.concat(response);
                        that.busy = false;
                    }
                });
            };
            this.loadEvents = function(query) {
                var deferred = $q.defer();
                deferred.resolve(that.eventsCollection);
                return deferred.promise;
            };
            this.loadEventsCity = function(query) {
                var deferred = $q.defer();
                var filteredEvents = [];
                    that.eventsCollection.map(function(x) {
                        if(filteredEvents.indexOf(x.details.city) == -1)
                            filteredEvents.push(x.details.city)
                });
                deferred.resolve(filteredEvents);
                return deferred.promise;
            };
            $scope.filterEvents = function(event) {
                var passed = true;
              if(that.eventNameTags.length > 0)
              {
                 passed = that.eventNameTags.filter(function(val, index, arr){
                     return val.name == event.name.toLowerCase().indexOf(val.name.toLowerCase()) != -1
                 }).length > 0;
              }
                if(that.eventCityTags.length > 0)
                {
                   passed = that.eventCityTags.filter(function(val,index,arr){
                       return event.details.city != undefined && val.text.toLowerCase() == event.details.city.toLowerCase()
                   }).length > 0;
                }
                return passed;
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
                    Account.initUser();
                    that.createEventData = {};
                    $scope.$broadcast('show-errors-reset');
                    $state.go('eventSkeleton.event',{eventId:response});
                });
            };
        }],
        link: function(scope, elem, attrs) {
        }
    }

}
eventsDirective.$inject = ["Events","$state", "Account", "$timeout", "$q"];
