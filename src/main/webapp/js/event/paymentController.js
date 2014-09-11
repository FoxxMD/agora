/**
 * Created by Matthew on 9/10/2014.
 */
angular.module('gtfest')
    .controller('PaymentController', paymentController);

// @ngInject
function paymentController($scope, eventData, $state, Events, Account){
    Stripe.setPublishableKey(eventData.payments[0].publicKey);
    var that = this;

    var checkPaid = function() {
        if (Account.hasPaid(eventData.id)) {
            $scope.$emit('notify', 'important', "You have already paid for this event!",6000);
            $state.go('eventSkeleton.event', {eventId: eventData.id});
        }
    };
    $scope.$on('accountStatusChange', function(){
        checkPaid();
    });
    checkPaid();

    $scope.handleStripe = function(status, response) {
        if(response.error)
        {
            $scope.$emit('notify','error',response,5000);
        }
        else
        {
            console.log(response.id);
            Events.payRegistration(eventData.id.toString(),'Stripe', response.id).then(function(){
                $scope.$emit('notify','notice','Successfully payed!',5000);
                Account.initUser();
                $state.go('eventSkeleton.event',{eventId: eventData.id});
            });
        }
    }
}
paymentController.$inject = ["$scope", "eventData", "$state", "Events", "Account"];