/**
 * Created by Matthew on 9/15/2014.
 */
angular.module('gtfest')
    .controller('TeamController', teamCtrl);

// @ngInject
function teamCtrl($scope, Teams, Events, Account, teamData) {
    var that = this;
    this.team = teamData.plain();
    this.isEventProfile = function () {
        return  Events.getCurrentEvent() != undefined;
    };
    this.isAdmin = function(){
        return Account.isAdmin() && Account.adminEnabled();
    };
    this.isEditable = function(){
        return Account.isLoggedIn() && (Account.isAdmin() || Teams.isCaptain(Account.user().id, teamData)) && Account.adminEnabled();
    };
    this.onTeam = function(){
        Teams.userOnTeam(Account.user().id, teamData);
    };
}
teamCtrl.$inject = ["$scope", "Teams", "Events", "Account", "teamData"];