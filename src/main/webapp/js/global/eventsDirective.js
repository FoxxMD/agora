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
        controller: function($scope){
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
/*                var filteredEvents = that.eventsCollection.filter(function(value, index,ar){
                return value.details.city != undefined
                });*/
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
                  if(that.eventNameTags.indexOf(event.name) == -1)
                   passed = false;
              }
                if(that.eventCityTags.length > 0)
                {
                    if(that.eventCityTags.indexOf(event.details.city) == -1)
                    passed = false;
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
        },
        link: function(scope, elem, attrs) {
            var thatelem = elem;
            var anim = undefined;
            scope.$on('onRepeatLast', function(scope, element, attrs){
                $timeout(function(){
                    if(anim == undefined)
                    {
                        anim = new AnimOnScroll( $(thatelem).find( '#grid' )[0], {
                            minDuration : 0.4,
                            maxDuration : 0.7,
                            viewportFactor : 0.2,
                            scrollingElement: $('.st-content')[0]
                        } );
                    }
                    else{
                        anim.items =  Array.prototype.slice.call(elem[0].querySelectorAll( '#grid > li' ));
                    }

                    console.log(anim);
                },100);
            });
        }
    }

}
eventsDirective.$inject = ["Events","$state", "Account", "$timeout", "$q"];

angular.module('gtfest')
.directive('onLastRepeat', function() {
    return function(scope, element, attrs) {
        if (scope.$last) setTimeout(function(){
            scope.$emit('onRepeatLast', element, attrs);
        }, 1);
    };
});
