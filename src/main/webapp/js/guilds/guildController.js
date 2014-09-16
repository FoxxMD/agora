/**
 * Created by Matthew on 9/15/2014.
 */
angular.module('gtfest')
    .controller('GuildController', teamCtrl);

// @ngInject
function teamCtrl($scope, Guilds, Events, Account, guildData) {
    var that = this;
    this.guild = guildData.plain();
    this.isEventProfile = function () {
        return  Events.getCurrentEvent() != undefined;
    };
    this.isAdmin = function(){
        return Account.isAdmin() && Account.adminEnabled();
    };
    this.isEditable = function(){
        return Account.isLoggedIn() && (Account.isAdmin() || Guilds.isCaptain(Account.user().id, guildData)) && Account.adminEnabled();
    };
    this.onTeam = function(){
        Guilds.userOnTeam(Account.user().id, guildData);
    };
}
teamCtrl.$inject = ["$scope", "Guilds", "Events", "Account", "guildData"];