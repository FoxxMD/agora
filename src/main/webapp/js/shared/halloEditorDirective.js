/**
 * Created by Matthew on 9/3/2014.
 */

angular.module('gtfest')
    .directive('halloEditor', halloEditor);

function halloEditor($sanitize, $q) {
    return {
        restrict: 'A',
        require: '?ngModel',
        scope: {
          updateProperty: '&',
          isClean: '='
        },
        link: function(scope, element, attrs, ngModel) {
            if (!ngModel) {
                return;
            }
            var isModified = false;
            element.hallo({
                plugins: {
                    'halloformat': {},
                    'halloheadings': [1,2,3,4],
                    'hallolists':{},
                    'hallojustify' : {}
                },
                toolbar:'halloToolbarFixed',
                toolbarOptions:{
                    affixTopOffset: -5
                }
            });
            ngModel.$render = function() {
                element.html($sanitize(ngModel.$viewValue) || '');
            };

            element.on('hallodeactivated', function() {
                if(isModified) {
                    var deferred = $q.defer();
                    element.hallo({editable: false});
                    scope.$watch('isClean', function (newVal, oldVal) {
                        if (newVal)
                            deferred.resolve();
                    });
                    scope.updateProperty({content: element.html()});
                    deferred.promise.then(function () {
                        ngModel.$setViewValue(element.html());
                        element.hallo({editable: true});
                        scope.isClean = false;
                        isModified = false;
                    });
                    //scope.$apply();
                }
            });
            element.on('hallomodified', function(){
                isModified = true;
            });
        }
    };
}