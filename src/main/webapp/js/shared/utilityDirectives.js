/**
 * Created by Matthew on 9/19/2014.
 */
// @ngInject
angular.module('gtfest')
    .directive('onLastRepeat', function() {
        return function(scope, element, attrs) {
            if (scope.$last) setTimeout(function(){
                scope.$emit('onRepeatLast', element, attrs);
            }, 1);
        };
    });
