/**
 * Created by Matthew on 8/11/2014.
 */
angular.module('gtfest')
    .directive('login', login);
// @ngInject
function login() {
    return {
        templateUrl: '/views/shared/login.html',
        restrict: 'E',
        controllerAs: 'loginCtrl',
        controller: function (Account) {
            var that = this;
            this.tryLogin = function () {
                Account.login(this.formData.email, this.formData.password).promise.then(
                    function () {
                        Account.initUser();
                    }, function (response) {
                        that.error = true;
                    });
            }
        }
    }
}