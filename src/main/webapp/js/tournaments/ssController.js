/**
 * Created by Matthew on 10/9/2014.
 */
/**
 * Created by Matthew on 10/9/2014.
 */
angular.module('gtfest')
    .controller('SSController', ssC);

// @ngInject
function ssC($scope, Account, $q, eventData, $rootScope, Events, $timeout, Tournaments, $stateParams) {
    var that = this;
    var firstRunServer = true,
        firstRunStream = true;
    this.isAdmin = function () {
        return Account.isEventAdmin($stateParams.eventId) && Account.adminEnabled();
    };
    $scope.tour = Tournaments.getCurrent();
    $scope.tour.details.servers = $scope.tour.details.servers != undefined ? $scope.tour.details.servers : that.isAdmin() ? populateDefaultServers() : undefined;
    $scope.tour.details.streams = $scope.tour.details.streams != undefined ? $scope.tour.details.streams : that.isAdmin() ? populateDefaultStreams() : undefined;
    $scope.$watch(function () {
        return that.isAdmin(); //this is bad
    }, function (newVal, oldVal) {
        $scope.tour.details.servers = $scope.tour.details.servers != undefined ? $scope.tour.details.servers : that.isAdmin() ? populateDefaultServers() : undefined;
        $scope.tour.details.streams = $scope.tour.details.streams != undefined ? $scope.tour.details.streams : that.isAdmin() ? populateDefaultStreams() : undefined;
    });

    this.treeOptions = {
        /*        beforeDrag: function(event) {
         var e = event;
         }*/
    };
    var serverRuleWatcher = $scope.$watch('tour.details.streams', function (newValue, oldValue) {
        if (newValue)
            stopListener("s");
    }, true);
    var streamRuleWatcher = $scope.$watch('tour.details.servers', function (newValue, oldValue) {
        if (newValue)
            stopListener("st");
    }, true);

    function stopListener(theType) {
        if(theType === "s")
        {
            if (firstRunServer)
                firstRunServer = false;
            else {
                serverRuleWatcher();
                that.showSave = true;
            }
        }
        else if(theType == "st"){
            if (firstRunStream)
                firstRunStream = false;
            else {
                streamRuleWatcher();
                that.showSave = true;
            }
        }

    }

    $scope.newSubItem = function (scope) {
        var nodeData = scope.$modelValue;
        nodeData.nodes.push({
            id: nodeData.id * 10 + nodeData.nodes.length,
            title: 'A new server',
            nodes: []
        });
    };

    this.addGroup = function (form) {
        $scope.$broadcast('show-errors-check-validity');
        if (form.$valid) {
            $scope.tour.details.servers.push({
                title: that.newGroupName,
                nodes: [
                    {
                        title: 'A new server group',
                        nodes: []
                    }
                ]
            });
            that.newGroupName = null;
            $scope.$broadcast('show-errors-reset');
        }
    };
    this.addStream = function (form) {
        $scope.$broadcast('show-errors-check-validity');
        if (form.$valid) {
            $scope.tour.details.streams.push({
                title: that.newGroupName,
                nodes: [
                    {
                        title: 'A new stream',
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

    this.save = function () {
        that.loading = true;
        Tournaments.update({servers: $scope.tour.details.servers, streams: $scope.tour.details.streams}).then(function () {
            that.showSave = false;
            $scope.$emit('notify', 'notice', 'Servers and Streams saved.', 2000);
            var r = serverRuleWatcher;
            var rs = streamRuleWatcher;
        }).finally(function () {
            that.loading = false;
        });
    };

    function populateDefaultServers() {
        return [
            {
                title: 'East Coast Server',
                nodes: [
                    {
                        title: '127.0.0.1',
                        nodes: []
                    }
                ]
            },
            {
                title: 'West Coast Server',
                nodes: [
                    {
                        title: '127.0.0.1',
                        nodes: []
                    }
                ]
            }
        ];
    }
    function populateDefaultStreams() {
        return [
            {
                title: 'Some Stream',
                link:'http://twitch.tv/somestream',
                nodes: []
            },
            {
                title: 'Another stream',
                link:'http://twitch.tv/anotherstream',
                nodes: []
            }
        ];
    }
}
ssC.$inject = ["$scope", "Account", "$q", "eventData", "$rootScope", "Events", "$timeout", "Tournaments", "$stateParams"];
