/**
 * Created by Matthew on 8/13/2014.
 */
angular.module('gtfest')
.directive('sidebar', sidebar);

// @ngInject
function sidebar($rootScope, $timeout, Account){
    return {
        templateUrl:'views/shared/sidebar.html',
        restrict:'E',
        controllerAs: 'sidebar',
        controller: function(){
            this.account = Account;
            this.loginVisible = false;
            this.registerVisible = false;
        },
        link: function($scope, elem, attrs)
        {
            $rootScope.toggleMenu = function () {
                var container = $(document).find('.st-container');
                if (!container.hasClass('st-menu-open')) {
                    container.addClass('st-menu-open');
                    $timeout(function () {
                        container.find('.st-pusher').on('click', function () {
                            container.removeClass('st-menu-open');
                            container.find('.st-pusher').off();
                        });
                    }, 0);

                }
            };
            //TODO these should probably be using $on to watch for a broadcast event so $rootScope doesn't get polluted...
            $rootScope.openLogin = function() {
                $rootScope.toggleMenu();
                $scope.sidebar.loginVisible = true;
            };
            $rootScope.openRegister = function() {
                $rootScope.toggleMenu();
                $scope.sidebar.registerVisible = true;
            };
            //Watches for URL change and if it finds
            // /login or /register at the end of the URL opens the sidemenu and respective action
            $rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams){
                switch(toParams.opt)
                {
                    case('login'):
                        $rootScope.openLogin();
                        break;
                    case('register'):
                        $rootScope.openRegister();
                        break;
                }
            });
        }
    }
}
sidebar.$inject = ["$rootScope", "$timeout", "Account"];