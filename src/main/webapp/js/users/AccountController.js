/**
 * Created by Matthew on 9/11/2014.
 */
angular.module('gtfest')
    .controller('AccountController', accountCtrl);

// @ngInject
function accountCtrl(Account, $rootScope, Users, $scope, $location){
    var that = this;
    $rootScope.$on('accountStatusChange', function(){
        that.user = Account.user();
    });
    this.account = Account;
    this.user = Account.user();

    this.updateEmail = function(email){
        that.user.email = email;
        Users.updateUser(that.user.id.toString(), that.user).then(function(){
            $scope.$emit('notify', 'notice', 'Email updated.', 3000);
            return true;
        });
    };
    this.changePassword = function(form) {
        $scope.$broadcast('show-errors-check-validity');
        if(form.$valid) {
            Users.updateUserPassword(that.user.id.toString(), that.passwordChange).then(function(){
                $scope.$emit('notify', 'notice', 'Password updated.', 3000);
                that.passwordChange = {};
                $scope.$broadcast('show-errors-reset');
            });
        }
    };
    this.deleteUser = function() {
      Users.deleteUser(that.user.id.toString()).then(function(){
          $scope.$emit('notify', 'notice', 'Your account and all related information has been deleted. We\'re sorry to see you go!', 5000);
          $location.url('/');
      })
    }
}
accountCtrl.$inject = ["Account", "$rootScope", "Users", "$scope", "$location"];
