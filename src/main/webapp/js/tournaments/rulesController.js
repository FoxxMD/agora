/**
 * Created by Matthew on 10/7/2014.
 */
angular.module('gtfest')
    .controller('RulesController', rulesC);

// @ngInject
function rulesC($scope, Account, $q, eventData, $rootScope, Events, $timeout, Tournaments, $stateParams) {
    var firstRun = true;
    var that = this;
    this.isAdmin = function () {
        return Account.isEventAdmin($stateParams.eventId) && Account.adminEnabled();
    };
    $scope.tour = Tournaments.getCurrent();

    $scope.tour.details.rules = $scope.tour.details.rules != undefined ? $scope.tour.details.rules : that.isAdmin() ? populateDefault() : undefined;
    $scope.$watch(function () {
        return that.isAdmin(); //this is bad
    }, function (newVal, oldVal) {
        $scope.tour.details.rules = $scope.tour.details.rules != undefined ? $scope.tour.details.rules : that.isAdmin() ? populateDefault() : undefined;
    });

    this.treeOptions = {
        /*        beforeDrag: function(event) {
         var e = event;
         }*/
    };
    var ruleWatcher = $scope.$watch('tour.details.rules', function (newValue, oldValue) {
        if (newValue)
            stopListener();
    }, true);

    function stopListener() {
        if (firstRun)
            firstRun = false;
        else {
            ruleWatcher();
            that.showSave = true;
        }

    }

    $scope.newSubItem = function (scope) {
        var nodeData = scope.$modelValue;
        nodeData.nodes.push({
            id: nodeData.id * 10 + nodeData.nodes.length,
            title: 'A new title',
            nodes: []
        });
    };

    this.addGroup = function (form) {
        $scope.$broadcast('show-errors-check-validity');
        if (form.$valid) {
            $scope.tour.details.rules.push({
                title: that.newGroupName,
                nodes: [
                    {
                        title: 'A new rule',
                        nodes: []
                    }
                ]
            });
            that.newGroupName = null;
            $scope.$broadcast('show-errors-reset');
        }
    };

    $scope.remove = function (scope) {
        scope.remove();
    };

    this.saveRules = function () {
        that.rulesLoading = true;
        Tournaments.update({rules: $scope.tour.details.rules}).then(function () {
            that.showSave = false;
            $scope.$emit('notify', 'notice', 'Rules saved.', 2000);
            var r = ruleWatcher;
        }).finally(function () {
            that.rulesLoading = false;
        });
    };

    function populateDefault() {
        return [
            {
                title: 'General Rules Example',
                nodes: [
                    {
                        title: 'No cheating',
                        nodes: []
                    },
                    {
                        title: 'No mic spamming',
                        nodes: []
                    }
                ]
            },
            {
                title: 'Some Specific Rules Example',
                nodes: [
                    {
                        title: 'Use a specific gaming style',
                        nodes: [
                            {
                                title: 'With this attribute',
                                nodes: []
                            },
                            {
                                title: 'And another attribute',
                                nodes: []
                            }
                        ]
                    }
                ]
            }
        ];
    }
}
rulesC.$inject = ["$scope", "Account", "$q", "eventData", "$rootScope", "Events", "$timeout", "Tournaments", "$stateParams"];
