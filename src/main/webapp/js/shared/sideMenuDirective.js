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
            $rootScope.openLogin = function() {
                $scope.sidebar.loginVisible = true;
                $rootScope.toggleMenu();
            };
            $rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams){
                switch(toParams.opt)
                {
                    case('login'):
                        $rootScope.openLogin();
                        break;
                    case('register'):
                        break;
                }
            });
        }
    }
}