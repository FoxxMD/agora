/**
 * Created by Matthew on 10/20/2014.
 */
angular.module('gtfest')
    .controller('EventFaqController', faqController);

// @ngInject
function faqController($scope, eventData, Events, Account, $stateParams, $q, $sce){
    var that = this,
        firstRun = true;
    that.sce = $sce;
    $scope.event = eventData;
    this.isAdmin = function () {
        return Account.isEventAdmin($stateParams.eventId) && Account.adminEnabled();
    };
    $scope.$watch(function () {
        return that.isAdmin(); //this is bad
    }, function (newVal, oldVal) {
        $scope.event.details.faq = $scope.event.details.faq != undefined ? $scope.event.details.faq : that.isAdmin() ? populateDefault() : undefined;
    });
    var ruleWatcher = $scope.$watch('event.details.faq', function (newValue, oldValue) {
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
            title: 'A new question',
            content: 'answer description',
            nodes: []
        });
    };
    this.addGroup = function (form) {
        $scope.$broadcast('show-errors-check-validity');
        if (form.$valid) {
            $scope.event.details.faq.push({
                title: that.newGroupName,
                nodes: [
                    {
                        title: 'A new faq group',
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
    this.saveFaq = function () {
        that.faqLoading = true;
        Events.updateEvent($scope.event).then(function () {
            that.showSave = false;
            $scope.$emit('notify', 'notice', 'FAQ saved.', 2000);
            var r = ruleWatcher;
        }).finally(function () {
            that.faqLoading = false;
        });
    };
    $scope.update = function(meh) {
        $scope.showSave = true;
        var deferred = $q.defer();
        deferred.resolve();
        return deferred.promise;
    };
    function populateDefault() {
        return [
            {
                title: 'General FAQ Example',
                nodes: [
                    {
                        title: 'What do I need to bring?',
                        content:'Your A game and a controller',
                        nodes: []
                    },
                    {
                        title: 'Is food provided?',
                        content: 'No.',
                        nodes: []
                    }
                ]
            },
            {
                title: 'Payment',
                nodes: [
                    {
                        title: 'What payment processor do you use?',
                        content:'Stripe!',
                        nodes: []
                    }
                ]
            }
        ];
    }
}
faqController.$inject = ["$scope", "eventData", "Events", "Account" ,"$stateParams", "$q" , "$sce"];
