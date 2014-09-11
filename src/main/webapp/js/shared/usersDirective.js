/**
 * Created by Matthew on 9/11/2014.
 */
angular.module('gtfest')
    .directive('users', users);
// @ngInject
function users(Users, Events, $state, $stateParams){
    return {
        templateUrl:'views/shared/users.html',
        restrict:'E',
        scope:'true',
        controllerAs:'usersCtrl',
        controller: function(){
            var that = this;
            this.userCollection = [];
            if ($state.$current.includes.globalSkeleton) {
                that.userCollection = Users.getUsers().$object;
                that.isGlobal = true;
            }
            else if ($state.$current.includes.eventSkeleton) {
                that.userCollection = Events.getUsers($stateParams.eventId.toString()).$object;
                that.isEvent = true;
            }
        },
        link: function(scope, elem, attrs){

        }
    }
}
users.$inject = ["Users", "Events", "$state", "$stateParams"];