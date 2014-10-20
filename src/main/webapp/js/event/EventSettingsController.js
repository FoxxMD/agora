/**
 * Created by Matthew on 9/9/2014.
 */
angular.module('gtfest')
    .controller('EventSettingsController', eventSettingsController);

// @ngInject
function eventSettingsController($scope, eventData, $rootScope, Events) {
    var that = this;
    this.event = eventData;
    this.privacyChange = false;
    this.paymentCreation = {
        payType: 'Stripe' //default payment since it's the only one right now
    };

    this.changePrivacy = function(){
        if(that.privacyChange)
        {
            that.privacyLoading = true;
            Events.updateEvent(that.event).then(function(){
                $scope.$emit('notify','notice',"Settings updated.", 1000);
            }).finally(function(){
                that.privacyLoading = false;
                that.privacyChange = false;
            });
        }
    };
    this.tryCreatePayment = function(form){
        $scope.$broadcast('show-errors-check-validity');
        if(form.$valid) {
            that.paymentLoading = true;
            Events.createPayment(eventData.id.toString(), that.paymentCreation).then(function(response){
                that.event.payments = response;
                that.paymentCreation = {
                    payType: 'Stripe'
                };
                $scope.$broadcast('show-errors-reset');
                $scope.$broadcast('toggleMorph');
                $scope.$emit('notify','notice',"Payment created.", 1000);
            }).finally(function(){
                that.paymentLoading = false;
            });
        }
    };
    this.tryChangePayment = function(option) {
        Events.changePayment(eventData.id.toString(),option.id.toString(),option).then(function(response) {
            $scope.$emit('notify','notice',"Payment updated.", 1000);
            option.changed = false;
        });
    };
    this.tryDeletePayment = function(id){
        Events.deletePayment(eventData.id.toString(),id.toString()).then(function(response){
            $scope.$emit('notify','notice',"Payment deleted.", 1000);
            that.event.payments = response;
        });
    }
}
eventSettingsController.$inject = ["$scope", "eventData", "$rootScope", "Events"];
