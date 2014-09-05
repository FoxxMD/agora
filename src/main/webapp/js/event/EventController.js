/**
 * Created by Matthew on 8/7/2014.
 */
angular.module('gtfest')
    .controller('EventController', eventController);

// @ngInject
function eventController($scope, Account, $state, eventData, $rootScope, Events, $sanitize){
    var that = this;

    $rootScope.$on('accountStatusChange', function(){
        adminStatusChecker();
    });
    $scope.$on('$destroy', function(){
        $rootScope.$broadcast('permissionsStatusChange', null);
    });
    this.event = eventData.plain();
    this.tourneyNames = ['Frosty 2v2','Epic 4v4','Round-Robin(20 player)','Deathmatch 100-kill Win'];
    this.frontPage = that.event.details.description || "<h3>This is your description page, please edit it!</h3>";

    function adminStatusChecker() {
       var status = Account.isLoggedIn() && $.grep(eventData.admins, function(e) { return e.id == Account.user().id}).length == 1 ? 'A': null;
       $rootScope.$broadcast('permissionsStatusChange', status);
    }
    adminStatusChecker();
    this.tryDescUpdate = function(content)
    {
        return Events.setDescription(eventData.id.toString(),content);
    }
}
eventController.$inject = ["$scope", "Account", "$state", "eventData", "$rootScope", "Events", "$sanitize"];