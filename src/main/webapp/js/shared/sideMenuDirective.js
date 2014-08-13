/**
 * Created by Matthew on 8/13/2014.
 */
angular.module('gtfest')
.directive('sidebar', sidebar);

// @ngInject
function sidebar($rootScope, $timeout){
    return {
        templateUrl:'views/shared/sidebar.html',
        restrict:'E',
        controllerAs: 'sidebar',
        controller: function(){

        },
        link: function(scope, elem, attrs)
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
        }
    }
}