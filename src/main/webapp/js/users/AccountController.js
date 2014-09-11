/**
 * Created by Matthew on 9/11/2014.
 */
angular.module('gtfest')
    .controller('AccountController', accountCtrl);

// @ngInject
function accountCtrl(Account, $rootScope){
    var that = this;
    $rootScope.$on('accountStatusChange', function(){
        that.user = Account.user();
    });
    this.account = Account;
    this.user = Account.user();
}
accountCtrl.$inject = ["Account", "$rootScope"];