/**
 * Created by Matthew on 9/19/2014.
 */
angular.module('gtfest')
    .directive('infiniteScrollerAnimated', scroller);

// @ngInject
function scroller($timeout){
    return {
        restrict:'E',
        template:'<div ng-transclude></div>',
        controllerAs:'animatedScrollCtrl',
        transclude: true,
        replace: true,
        controller: /*@ngInject*/ ["$scope", function($scope) {}],
        link: function(scope, elem, attr){
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
                        anim.itemsRenderedCount = 0;
                        anim.items =  Array.prototype.slice.call(elem[0].querySelectorAll( '#grid > li' ));
                        anim._scrollPage();
                    }

                    //console.log(anim);
                },100);
            });
        }
    }
}
scroller.$inject = ["$timeout"];
