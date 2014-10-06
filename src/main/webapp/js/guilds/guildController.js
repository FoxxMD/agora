/**
 * Created by Matthew on 9/15/2014.
 */
angular.module('gtfest')
    .controller('GuildController', teamCtrl);

// @ngInject
function teamCtrl($scope, Guilds, Events, Account, guildData) {
    var that = this;
    this.guild = guildData.plain();
    this.account = Account;
    this.isEventProfile = function () {
        return Events.getCurrentEvent() != undefined;
    };
    this.isAdmin = function () {
        return Account.isAdmin() && Account.adminEnabled();
    };
    this.isEditable = function () {
        return Account.isLoggedIn() && (Account.isAdmin() || Guilds.isCaptain(Account.user().id, guildData)) && Account.adminEnabled();
    };
    this.onGuild = function () {
        return Guilds.userInGuild(Account.user().id, guildData);
    };
    this.toggleGuildMembership = function () {
        that.guildLoading = true;
        if (that.onGuild())
        {
            Guilds.leaveGuild(Account.user().id, guildData).then(function(){
                $scope.$emit('notify','notice',"Successfully left Guild.",4000);
                Guilds.getGuild(guildData.id.toString()).then(function(response){
                    guildData = response;
                    that.guild = guildData.plain();
                });
            }).finally(function(){
                that.guildLoading = false;
            });
        }
        else
        {
            Guilds.joinGuild(Account.user().id, guildData).then(function(){
                $scope.$emit('notify','notice',"Successfully joined Guild.",4000);
                Guilds.getGuild(guildData.id.toString()).then(function(response){
                    guildData = response;
                    that.guild = guildData.plain();
                });
            }).finally(function(){
                that.guildLoading = false;
            });
        }
    };
}
teamCtrl.$inject = ["$scope", "Guilds", "Events", "Account", "guildData"];
