angular.module('app.directives',[])
    .directive('scheduleDirective', ['$compile', function ($compile) {
        var directive = {
            restrict: 'AE',
            template: '<button class="btn btn-info pull-right" ng-click="changeDay(\'2014-03-08\')">Day 1</button><button class="btn btn-info pull-right" ng-click="changeDay(\'2014-03-09\')">Day 2</button><div id="calendar" style="float:left;"></div>',
            link: function (scope, element, attrs) {

                var calendar = $(element).find('#calendar').calendar({
                    events_source: [
                        {
                            'id': 1,
                            'title': 'Test Event',
                            'start': 1394301600000,
                            'end': 1394308800000,
                            'class':'event-inverse'
                        },
                        {
                            'id': 2,
                            'title': 'Test Event 2',
                            'start': 1394305200000,
                            'end': 1394312400000,
                            'class':'event-inverse'
                        }
                    ],
                    tmpl_path:'js/calendar/tmpls/',
                    view:'day',
                    day: '2014-03-08'
                });

                scope.changeDay = function(theDay){
                    calendar.setOptions({day: theDay});
                    calendar.view();
                };

            }
        };
        return directive;
    }])
.directive('tournamet')