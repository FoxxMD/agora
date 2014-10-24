/**
 * Created by Matthew on 9/11/2014.
 */
angular.module('gtfest')
    .controller('ProfileController', prof);

// @ngInject
function prof($scope, Account, Users, userData, Events, $state) {
    var that = this;
    this.isEventProfile = function () {
        return  Events.getCurrentEvent() != undefined;
    };
    if (that.isEventProfile()) {
        that.event = Events.getCurrentEvent();
        that.eventIndex = Users.getEventIndex(userData, that.event.id);
    }
    this.Users = Users;
    this.account = Account;
    this.user = userData;
    this.isAdmin = function(){
        return (Account.isAdmin() || Account.isEventAdmin()) && Account.adminEnabled();
    };
    this.isModerator = function(){
        return (Account.isAdmin() || Account.isEventAdmin()) && Account.adminEnabled();
    };
    this.user.accountType = that.isEventProfile() ? Users.isEventAdmin(userData, that.event.id) : Account.isAdmin();
    this.ownProfile = function () {
        return Account.isLoggedIn() && Account.user().id == userData.id;
    };
    this.isEditable = function () {
        return that.ownProfile() || (Account.isAdmin() && Account.adminEnabled());
    };
    this.tryChangePaid = function () {
        that.paidLoading = true;
        Events.payRegistration(that.event.id.toString(), undefined, undefined, userData.id.toString(), !that.user.events[that.eventIndex].hasPaid).then(function () {
            that.user.events[that.eventIndex].hasPaid = !that.user.events[that.eventIndex].hasPaid;
            $scope.$emit('notify', 'notice', 'Payment status successfully changed.', 3000);
        }).finally(function () {
            that.paidLoading = false;
        });
    };
    this.tryChangePresent = function () {
        that.presentLoading = true;
        Events.setPresent(that.event.id.toString(), userData.id.toString(), !that.user.events[that.eventIndex].isPresent).then(function () {
            that.user.events[that.eventIndex].isPresent = !that.user.events[that.eventIndex].isPresent;
            $scope.$emit('notify', 'notice', 'Presence successfully changed.', 3000);
        }).finally(function () {
            that.presentLoading = false;
        });
    };
    this.tryRemoveUser = function () {
        Events.leaveEvent(that.event.id.toString(), userData.id.toString()).then(function () {
            $scope.$emit('notify', 'notice', 'User has been removed from this event.', 3000);
            $state.go('eventSkeleton.users', {eventId: that.event.id});
        })
    };
    this.tryChangeModerator = function () {
        that.modLoading = true;
        Events.setModerator(that.event.id.toString(), userData.id.toString(), !that.user.events[that.eventIndex].isModerator).then(function () {
            that.user.events[that.eventIndex].isModerator = !that.user.events[that.eventIndex].isModerator;
            $scope.$emit('notify', 'notice', 'Mod status successfully changed.', 3000);
        }).finally(function () {
            that.modLoading = false;
        });
    };
    this.tryChangeEventAdmin = function () {
        that.eventAdminLoading = true;
        Events.setAdmin(that.event.id.toString(), userData.id.toString(), !that.user.events[that.eventIndex].isAdmin).then(function () {
            that.user.events[that.eventIndex].isAdmin = !that.user.events[that.eventIndex].isAdmin;
            $scope.$emit('notify', 'notice', 'Admin status successfully changed.', 3000);
        }).finally(function () {
            that.eventAdminLoading = false;
        });
    };
    this.tryUpdateHandle = function(handle){
        Users.setHandle(that.user.id.toString(), handle).then(function(){
            $scope.$emit('notify', 'notice', 'Handle updated.', 3000);
            return true;
        });
    };
    this.tryCreateGameProfile = function(){
        that.gameProfileLoading = true;
        Users.addGamePlatformProfile(that.user.id.toString(), that.newPlatformData).then(function(){
            $scope.$broadcast('toggleMorph');
            that.user.gameProfiles.push(that.newPlatformData);
            that.newPlatformData = {};
            $scope.$broadcast('show-errors-reset');
            $scope.$emit('notify', 'notice', 'Platform added.', 3000);
        }).finally(function(){
            that.gameProfileLoading = false;
        });
    };
    this.hasGameProfile = function(profile){
        return _.some(that.user.gameProfiles, {'platform': profile})
    };
    this.tryRemoveGameProfile = function(platform){
        Users.removeGamePlatformProfile(that.user.id.toString(), platform.platform).then(function(){
            $scope.$emit('notify', 'notice', 'Platform removed.', 3000);
            that.user.gameProfiles.slice(that.user.gameProfiles.indexOf(platform),1);
        });
    };
    this.tryUpdateGameProfile = function(platform) {
      Users.updateGamePlatformProfile(that.user.id.toString(), platform).then(function(){
          $scope.$emit('notify', 'notice', 'Platform update.', 3000);
      });
    };
    this.deleteUser = function(){
        Users.deleteUser(that.user.id.toString()).then(function(){
            $scope.$emit('notify', 'notice', 'User has been deleted.', 5000);
            $state.go('eventSkeleton.users');
        })
    }
    this.parseTime = function(time){
        return moment(time);
    }

}
prof.$inject = ["$scope", "Account", "Users", "userData", "Events", "$state"];
