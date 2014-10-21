/**
 * Created by Matthew on 8/7/2014.
 */
angular.module('gtfest')
    .controller('EventController', eventController);

// @ngInject
function eventController($scope, Account, $q, eventData, $rootScope, Events, $sce, $timeout, $localStorage, $state){

    var that = this;
    this.event = eventData.plain();
    this.tourneyNames = ['Frosty 2v2','Epic 4v4','Round-Robin(20 player)','Deathmatch 100-kill Win'];
    this.sce = $sce;
    this.frontPage = that.event.details.description || "<h3>This is your description page, please edit it!</h3>";

    this.isAdmin = function() {
        return Account.isLoggedIn() && Events.isAdmin(Account.user()) && Account.adminEnabled();
    };
    this.isModerator = function() {
        return Account.isLoggedIn() && Events.isModerator(Account.user()) && Account.adminEnabled();
    };

    $rootScope.$on('accountStatusChange', function(){
        if(Account.isLoggedIn() && Account.isRegisteredForEvent(that.event.id) && !Account.hasPaid(that.event.id) && !$localStorage.reminders[Account.user().id][that.event.id])
        {
            $timeout(function(){
                $scope.$emit('notify', 'important', 'You haven\'t pre-paid for this event yet! <a href="'+ $state.href("eventSkeleton.pay",{eventId:that.event.id})+ '" class="btn btn-info">Pay Now</a> <a style="border:solid 1px grey;" class="btn reminderButton">Don\'t Remind Me Again</a>', 0, 'barBottom');
            },1000);
        }
    });
    //This is terrible and I feel bad for doing it.
    //So sue me.
    $(document).on('click', '.reminderButton', function(){
        $localStorage.reminders[Account.user().id][that.event.id] = true;
        $scope.$apply();
        $scope.$emit('closeNotification');
    });

    this.tryDescUpdate = function(content)
    {
        that.event.details.description = content;
        return Events.updateEvent(that.event);
        //return Events.setDescription(eventData.id.toString(),content);
    };
    this.tryTimeUpdate = function() {
        var deferred = $q.defer();
      console.log(that.event.details.timeStart);
       Events.updateEvent(that.event).then(function(){
            deferred.resolve();
        }, function(){
           deferred.reject();
       });
        return deferred.promise;
    };
    this.tryJoin = function() {
        Events.joinEvent(that.event.id.toString()).then(function(){
            $scope.$emit('notify','notice','Registration successful.', 2000);
            Account.initUser();
        });
    };
    this.tryLeave = function() {
        Events.leaveEvent(that.event.id.toString()).then(function () {
            $scope.$emit('notify', 'notice', 'You have left successfully.', 2000);
            Account.initUser();
        });
    };
    this.savePlace = function(){
        that.placeLoading = true;
        Events.updateEvent(that.event)
            .then(function(){
                that.editPlace = false;
                $scope.$emit('notify', 'notice', 'Location saved.', 2000);
            }).finally(function(){
                that.placeLoading = false;
            });
    }
}
eventController.$inject = ["$scope", "Account", "$q", "eventData", "$rootScope", "Events", "$sce", "$timeout", "$localStorage", "$state"];
