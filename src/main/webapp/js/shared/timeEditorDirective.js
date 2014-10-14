/**
 * Created by Matthew on 9/8/2014.
 */
angular.module('gtfest')
    .directive('timeEditor', timeEditor);

// @ngInject
function timeEditor($q) {
    return {
        restrict: 'A',
        templateUrl: 'views/shared/timeEditor.html',
        controllerAs: 'timeEditor',
        scope: {
            updateProperty: '&',
            iseditable: '=',
            timeEnd: '=',
            timestart: '=',
            starteditable: '=',
            multimode: '=',
            horizontal: '='
        },
        controller: /*@ngInject*/ ["$scope", function ($scope) {
            var that = this;
            this.editable = $scope.starteditable || false;

            this.format = 'dd-MMMM-yyyy';

            this.openStopTime = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                that.stopOpened = true;
            };
            this.openStartTime = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                that.startOpened = true;
            };
            this.tryEdit = function(){
                that.editable = $scope.iseditable;
            };

            this.updateTimeData = function(){
                $scope.updateProperty().then(function(){
                   that.editable = false;
                    $scope.$emit('notify', 'notice', 'Update successful.', 1000);
                }, function(){

                });
            };

        }],
        link: function (scope, element, attrs) {
        }

    }
}
timeEditor.$inject = ["$q"];
