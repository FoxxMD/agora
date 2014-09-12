/**
 * Created by Matthew on 9/11/2014.
 */
angular.module('gtfest')
    .directive('users', users);
// @ngInject
function users(Users, Events, $state, $stateParams, Account){
    return {
        templateUrl:'views/shared/users.html',
        restrict:'E',
        scope:'true',
        controllerAs:'usersCtrl',
        controller: function($scope){
            var that = this;
            this.account = Account;
            this.isAdmin = function(){
                return (Account.isAdmin() || Account.isEventAdmin()) && Account.adminEnabled();
            };
            this.userCollection = [];
            if ($state.$current.includes.globalSkeleton) {
                that.userCollection = Users.getUsers().then(function(response){
                    that.displayCollection = [].concat(response.plain());
                    return response.plain();
                });
                that.isGlobal = true;
            }
            else if ($state.$current.includes.eventSkeleton) {
                Events.getUsers($stateParams.eventId.toString()).then(function(response){
                    plainArray = response.plain();
                    that.displayCollection = [].concat(plainArray);
                    that.userCollection = plainArray;
                });
                that.isEvent = true;
            }
            this.tableGoTo = function($event, id) {
                if($($event.target).is('td'))
                {
                    var thestate = '';
                    if($state.$current.includes.globalSkeleton)
                        $state.go('globalSkeleton.profile',{userId:id});
                    else
                        $state.go('eventSkeleton.profile',{userId:id, eventId:$stateParams.eventId});
                }

            };
            this.tryChangePresent = function(userId, status, row) {
                row.presentLoading = true;
              Events.setPresent(Events.getCurrentEvent().id.toString(),userId.toString(),status).then(function(){
                  that.userCollection[that.userCollection.indexOf(row)].isPresent = status;
                  $scope.$emit('notify','notice', 'Presence successfully changed.',3000);
              }).finally(function(){
                  row.presentLoading = false;
              });
            };
            this.tryChangePaid = function(userId, paidStatus, row) {
                row.paidLoading = true;
                Events.payRegistration(Events.getCurrentEvent().id.toString(), undefined, undefined, userId.toString(), paidStatus).then(function(){
                    that.userCollection[that.userCollection.indexOf(row)].hasPaid = paidStatus;
                    $scope.$emit('notify','notice', 'Payment status successfully changed.',3000);
                }).finally(function(){
                    row.paidLoading = false;
                });
            }
        },
        link: function(scope, elem, attrs){

        }
    }
}
users.$inject = ["Users", "Events", "$state", "$stateParams", "Account"];