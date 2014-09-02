/**
 * Created by Matthew on 9/2/2014.
 */
angular.module('gtfest')
.directive('morphingButton', morphButton);

function morphButton($timeout){
    return {
        templateUrl:'views/shared/morphButton.html',
        restrict:'E',
        replace:true,
        transclude: true,
        scope: {
            title:'@'
        },
        controller:function(){

        },
        link:function(scope, elem, attrs)
        {
            var morphButton = new UIMorphingButton( elem[0]);
            scope.$on('adjustMorphHeight', function(){
                $timeout(function(){
                    morphButton.adjustHeight();
                },0);
            });
            scope.$on('toggleMorph', function() {
                morphButton.toggle();
            })
        }
    }
}