/**
 * Created by Matthew on 10/9/2014.
 */
angular.module('gtfest')
    .controller('PrizesController', prizesC);

// @ngInject
function prizesC($scope, Account, $q, eventData, $rootScope, Events, $timeout, Tournaments, $stateParams) {
    $scope.tour = Tournaments.getCurrent();
    var firstRun = true;
    var that = this;
    this.isAdmin = function () {
        return Account.isEventAdmin($stateParams.eventId) && Account.adminEnabled();
    };

    $scope.tour.details.prizes = $scope.tour.details.prizes != undefined ? $scope.tour.details.prizes : that.isAdmin() ? populateDefault() : undefined;
    $scope.$watch(function () {
        return that.isAdmin(); //this is bad
    }, function (newVal, oldVal) {
        $scope.tour.details.prizes = $scope.tour.details.prizes != undefined ? $scope.tour.details.prizes : that.isAdmin() ? populateDefault() : undefined;
    });

    this.treeOptions = {
        /*        beforeDrag: function(event) {
         var e = event;
         }*/
    };
    var ruleWatcher = $scope.$watch('tour.details.prizes', function (newValue, oldValue) {
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
            title: 'A new prize',
            nodes: []
        });
    };

    this.addGroup = function (form) {
        $scope.$broadcast('show-errors-check-validity');
        if (form.$valid) {
            $scope.tour.details.prizes.push({
                title: that.newGroupName,
                nodes: [
                    {
                        title: 'A new prizes',
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

    this.savePrizes = function () {
        that.prizesLoading = true;
        Tournaments.update({prizes: $scope.tour.details.prizes}).then(function () {
            that.showSave = false;
            $scope.$emit('notify', 'notice', 'Prizes saved.', 2000);
            var r = ruleWatcher;
        }).finally(function () {
            that.prizesLoading = false;
        });
    };
    function populateDefault() {
        return [
            {
                title: '1st Place',
                nodes: [
                    {
                        title: 'A neat gaming keyboard',
                        link: 'http://example.com',
                        nodes: []
                    },
                    {
                        title: 'A game pack',
                        link: 'http://example.com',
                        nodes: [
                            {
                                title: 'Game A',
                                link: 'http://example.com',
                                nodes: []
                            },
                            {
                                title: 'Game B',
                                link: 'http://example.com',
                                nodes: []
                            },
                            {
                                title: 'Game C',
                                link: 'http://example.com',
                                nodes: []
                            }
                        ]
                    }
                ]
            },
            {
                title: '2nd Place',
                nodes: [
                    {
                        title: 'Some game credits',
                        nodes: []
                    }
                ]
            }
        ];
    }
}
prizesC.$inject = ["$scope", "Account", "$q", "eventData", "$rootScope", "Events", "$timeout", "Tournaments", "$stateParams"];
