/**
 * Created by Matthew on 10/9/2014.
 */
angular.module('gtfest')
    .controller('EventAboutController', about);

// @ngInject
function about($scope, $state, $stateParams, Events, Account) {
    var that = this,
        firstRun = true;
    this.isAdmin = function () {
        return Account.isEventAdmin($stateParams.eventId) && Account.adminEnabled();
    };
    $scope.event = Events.getCurrentEvent();
    $scope.event.details.credits = $scope.event.details.credits != undefined ? $scope.event.details.credits : that.isAdmin() ? populateDefault() : undefined;
    $scope.$watch(function () {
        return that.isAdmin(); //this is bad
    }, function (newVal, oldVal) {
        $scope.event.details.credits = $scope.event.details.credits != undefined ? $scope.event.details.credits : that.isAdmin() ? populateDefault() : undefined;
    });

    var ruleWatcher = $scope.$watch('event.details.credits', function (newValue, oldValue) {
        if (newValue)
            stopListener();
    }, true);

    function stopListener(){
        if(firstRun)
            firstRun = false;
        else
        {
            ruleWatcher();
            that.showSave = true;
        }
    }

    this.addGroup = function (form) {
        $scope.$broadcast('show-errors-check-validity');
        if (form.$valid) {
            $scope.event.details.credits.push({
                title: that.newGroupName,
                nodes: [
                    {
                        title: 'A person to thank',
                        byline: 'Their byline',
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
        Events.updateEvent($scope.event).then(function () {
            that.showSave = false;
            $scope.$emit('notify', 'notice', 'Credits saved.', 2000);
            var r = ruleWatcher;
        }).finally(function () {
            that.loading = false;
        });
    };

    function populateDefault()
    {
        return [
            {
                title:'Execs',
                nodes:[
                    {
                        title:'Henry \'Hanky\' Mei',
                        byline:'eSports Co-Chair'
                    },
                    {
                        title:'Joey Benamy',
                        byline:'eSports Co-Chair'
                    }
                ]
            },
            {
                title:'Helpers',
                nodes:[
                    {
                        title:'Joe Schmoe',
                        byline:'Volunteer'
                    }
                ]
            }
        ]
    }
}
about.$inject = ["$scope", "$state", "$stateParams", "Events", "Account"];
