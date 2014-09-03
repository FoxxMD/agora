/**
 * Created by Matthew on 9/3/2014.
 */

angular.module('gtfest')
    .directive('halloEditor', halloEditor);

function halloEditor() {
    return {
        restrict: 'A',
        require: '?ngModel',
        link: function(scope, element, attrs, ngModel) {
            if (!ngModel) {
                return;
            }

            element.hallo({
                plugins: {
                    'halloformat': {},
                    'halloheadings': [1,2,3,4],
                    'hallolists':{},
                    'hallojustify' : {}
                },
                toolbar:'halloToolbarFixed',
                options:{
                    toolbarOptions:{
                    }
                }
            });

            ngModel.$render = function() {
                element.html(ngModel.$viewValue || '');
            };

            element.on('hallodeactivated', function() {
                ngModel.$setViewValue(element.html());
                scope.$apply();
            });
        }
    };
}