/**
 * Created by Matthew on 9/3/2014.
 */

angular.module('gtfest')
    .directive('halloEditor', halloEditor);
// @ngInject
function halloEditor($q) {
    return {
        restrict: 'A',
        require: '?ngModel',
        scope: {
            updateProperty: '&',
            showUpdateStatus: '&'
        },
        link: function (scope, element, attrs, ngModel) {
            if (!ngModel) {
                return;
            }
            var isModified = false;
            element.hallo({
                plugins: {
                    'halloformat': {},
                    'halloheadings': [1, 2, 3, 4],
                    'hallolists': {},
                    'hallojustify': {},
                    'hallo-image-insert-edit': {}
                },
                toolbar: 'halloToolbarFixed',
                toolbarOptions: {
                    affixTopOffset: -5
                }
            });
            ngModel.$render = function () {
                element.html(ngModel.$viewValue || '');
            };

            element.on('hallodeactivated', function () {
                if (isModified) {
                    element.hallo({editable: false});
                    scope.updateProperty({content: element.html()}).then(function () {
                            if(scope.showUpdateStatus() == undefined || scope.showUpdateStatus())
                            scope.$emit('notify', 'notice', 'Update successful.', 1000);
                            ngModel.$setViewValue(element.html());
                            isModified = false;
                        },
                        function () {
                            scope.$emit('notify', 'error', 'There was a problem updating.', 1000);
                        }).finally(function () {
                            element.hallo({editable: true});
                        });
                }
            });
            element.on('hallomodified', function () {
                isModified = true;
            });
        }
    };
}
halloEditor.$inject = ["$q"];
