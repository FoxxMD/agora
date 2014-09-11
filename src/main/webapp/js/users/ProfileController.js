/**
 * Created by Matthew on 9/11/2014.
 */
angular.module('gtfest')
    .controller('ProfileController', prof);

// @ngInject
function prof(Account, Users, userData, Events){
    var that = this;
    this.isEventProfile = function() {
      return  Events.getCurrentEvent() != undefined;
    };
    if(that.isEventProfile())
    {
        that.event = Events.getCurrentEvent();
    }
    this.Users = Users;
    this.account = Account;
    this.user = userData;
    this.user.accountType = that.isEventProfile() ? Users.isEventAdmin(userData, that.event.id) : Account.isAdmin();
    this.ownProfile = function(){
        return Account.isLoggedIn() && Account.user().id == userData.id;
    };
    this.isEditable = function(){
        return that.ownProfile() || (Account.isAdmin() && Account.adminEnabled());
    };

}
prof.$inject = ["Account", "Users", "userData", "Events"];