/**
 * Created by Matthew on 10/9/2014.
 */
angular.module('gtfest')
    .controller('TourAdminController', admin);

// @ngInject
function admin($scope, Account, Tournaments, $state, $stateParams, Events) {
    var that = this;
    this.tour = Tournaments.getCurrent();

    Events.getUsers($stateParams.eventId.toString()).then(function(response){
        that.potentialUsers = response.plain();
    });

    $scope.filterAdminUsers = function(user) {
        return user.isAdmin || user.isModerator;
    };
    $scope.nonAdminUsers = function(user) {
        return _.find(that.tour.users, function(tourUser){
           return tourUser.name == user.globalHandle && (tourUser.isAdmin || tourUser.isModerator);
        }) == undefined;
    };
    this.addAdmin = function(user) {
       var existingUser = _.find(that.tour.users, function(auser){
            return auser.name == user.globalHandle;
        });
        if(existingUser == undefined)
        {
            Tournaments.addPlayer(user.id, that.tour.id.toString()).then(function(response){
                existingUser = response.plain();
                that.tour.users.push(existingUser);
                Tournaments.changeUserAdmin(existingUser, that.tour.id.toString()).then(function(){
                });
            })
        }
        else{
            Tournaments.changeUserAdmin(existingUser, that.tour.id.toString()).then(function(){
            });
        }
        that.selectedUser = null;
    };
    this.removeAdmin = function(user) {
        Tournaments.changeUserAdmin(user, that.tour.id.toString()).then(function(){

        });
    };
    this.tryDelete = function(){
        Tournaments.deleteTournament(that.tour.id.toString()).then(function(){
            $state.go('eventSkeleton.tournaments',{eventId: $stateParams.eventId});
            $scope.$emit('notify','important',"Tournament deleted.",6000);
        })
    };
}
admin.$inject = ["$scope", "Account", "Tournaments", "$state", "$stateParams", "Events"];
