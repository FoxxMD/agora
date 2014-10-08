/**
 * Created by Matthew on 10/7/2014.
 */
angular.module('gtfest')
    .controller('RulesController', rulesC);

// @ngInject
function rulesC($scope, Account, $q, eventData, $rootScope, Events, $timeout, Tournaments) {
    this.tour = Tournaments.getCurrent();
    this.tour.details.rules = this.tour.details.rules || [{title:'A rule heading',
    nodes:[]}];
    $scope.newSubItem = function(scope) {
        var nodeData = scope.$modelValue;
        nodeData.nodes.push({
            id: nodeData.id * 10 + nodeData.nodes.length,
            title: nodeData.title + '.' + (nodeData.nodes.length + 1),
            nodes: []
        });
    };

    $scope.remove = function(scope) {
        scope.remove();
    };
}
rulesC.$inject = ["$scope", "Account", "$q", "eventData", "$rootScope", "Events", "$timeout", "Tournaments"];
