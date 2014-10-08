/**
 * Created by Matthew on 10/7/2014.
 */
angular.module('gtfest')
    .controller('RulesController', rulesC);

// @ngInject
function rulesC($scope, Account, $q, eventData, $rootScope, Events, $timeout, Tournaments) {
    $scope.tour = Tournaments.getCurrent();
    $scope.tour.details.rules = JSON.parse($scope.tour.details.rules) || [{title:'A rule heading',
    nodes:[]}];
    var firstRun = true;
    var that = this;

    this.treeOptions = {
/*        beforeDrag: function(event) {
         var e = event;
         }*/
    };
    var ruleWatcher = $scope.$watch('tour.details.rules', function(newValue, oldValue){
        if(newValue)
            stopListener();
    }, true);

    function stopListener() {
        if(firstRun)
            firstRun = false;
        else
        {
            ruleWatcher();
            that.showSave = true;
        }

    }

    $scope.newSubItem = function(scope) {
        var nodeData = scope.$modelValue;
        nodeData.nodes.push({
            id: nodeData.id * 10 + nodeData.nodes.length,
            title: 'A new title',
            nodes: []
        });
    };

    $scope.remove = function(scope) {
        scope.remove();
    };

    this.saveRules = function(){
        that.rulesLoading = true;
        Tournaments.update({rules: $scope.tour.details.rules}).then(function(){
            that.showSave = false;
            $scope.$emit('notify','notice','Rules saved.', 2000);
            var r = ruleWatcher;
        }).finally(function(){
            that.rulesLoading = false;
        });
    }
}
rulesC.$inject = ["$scope", "Account", "$q", "eventData", "$rootScope", "Events", "$timeout", "Tournaments"];
