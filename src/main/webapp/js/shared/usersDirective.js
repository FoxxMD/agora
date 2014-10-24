/**
 * Created by Matthew on 9/11/2014.
 */
angular.module('gtfest')
    .directive('users', users);
// @ngInject
function users(Users, Events, $state, $stateParams, Account, $q){
    return {
        templateUrl:'views/shared/users.html',
        restrict:'E',
        scope:'true',
        controllerAs:'usersCtrl',
        controller: /*@ngInject*/ ["$scope", function($scope){
            var that = this;
            this.account = Account;
            this.state = $state;
            this.stateParams = $stateParams;
            var pageNo = 1;

            this.isAdmin = function(){
                return (Account.isAdmin() || Account.isEventAdmin()) && Account.adminEnabled();
            };
            $scope.$watch(function(){
                if(that.filtered != undefined)
                    return that.filtered.length == 0 && !that.busy;
            }, function(newVal, oldVal){
                //newVal == 0 &&
                if(oldVal != undefined && newVal)
                    that.getMoreUsers();
            });

            this.userCollection = [];
            if ($state.$current.includes.globalSkeleton) {
                Users.getUsers().then(function(response){
                    that.userCollection = response.plain();
                });
                that.isGlobal = true;
            }
            else if ($state.$current.includes.eventSkeleton) {
                Events.getUsers($stateParams.eventId.toString()).then(function(response){
                    that.userCollection = response.plain();
                });
                that.isEvent = true;
            }

            this.getMoreUsers = function(){
                that.busy = true;
                pageNo++;
                if (that.isGlobal) {
                    Users.getUsers(pageNo).then(function(response){
                        if(response.length > 0)
                        {
                            that.userCollection = that.userCollection.concat(response.plain());
                            that.busy = false;
                        }
                    });
                }
                else if (that.isEvent) {
                    Events.getUsers($stateParams.eventId.toString(), pageNo).then(function(response){
                        if(response.length > 0)
                        {
                            that.userCollection = that.userCollection.concat(response.plain());
                            that.busy = false;
                        }
                    });
                }
            };
            this.loadPlatforms = function(query) {
                var deferred = $q.defer();
                var plats = [{'text':'Steam'},
                    {'text':'Battle.net'},
                    {'text':'Riot'}];
                deferred.resolve(plats);
                return deferred.promise;
            };
            this.filterUsers = function(user) {
                var passed = true;
                if(that.userNameTags.length > 0)
                {
                    passed = that.userNameTags.filter(function(val, index, arr){
                        return user.globalHandle.toLowerCase().indexOf(val.text.toLowerCase()) != -1;
                    }).length > 0;
                }
                if(that.userPlatformTags.length > 0)
                {
                    passed = that.userPlatformTags.filter(function(val, index, arr){
                       for(var i = 0; i < user.gameProfiles.length; i++){
                           if(user.gameProfiles[i].platform == val.text)
                            return true;
                       }
                    }).length > 0;
                }
                return passed;
            };

            this.tryChangePresent = function(user) {
                user.presentLoading = true;
              Events.setPresent(Events.getCurrentEvent().id.toString(),user.id.toString(),!user.isPresent).then(function(){
                  that.userCollection[that.userCollection.indexOf(user)].isPresent = !user.isPresent;
                  $scope.$emit('notify','notice', 'Presence successfully changed.',3000);
              }).finally(function(){
                  user.presentLoading = false;
              });
            };
            this.tryChangePaid = function(user) {
                user.paidLoading = true;
                Events.payRegistration(Events.getCurrentEvent().id.toString(), undefined, undefined, user.id.toString(), !user.hasPaid).then(function(){
                    that.userCollection[that.userCollection.indexOf(user)].hasPaid = !user.hasPaid;
                    $scope.$emit('notify','notice', 'Payment status successfully changed.',3000);
                }).finally(function(){
                    user.paidLoading = false;
                });
            }
        }],
        link: function(scope, elem, attrs){

        }
    }
}
users.$inject = ["Users", "Events", "$state", "$stateParams", "Account", "$q"];
