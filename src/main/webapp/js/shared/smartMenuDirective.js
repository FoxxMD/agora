angular.module('gtfest')
.directive('smartMenu', smartMenu);

// @ngInject
function smartMenu($rootScope, Account, Events){
    return {
        templateUrl:'/views/shared/smartMenu.html',
        restrict: 'E',
        scope: true,
        controllerAs:'smartMenu',
        controller: /*@ngInject*/ ["$scope", function($scope){

        }],
        link: function(scope, elem, attrs, control)
        {
            var letter = null;
            scope.permission = function(){
               if(Account.isAdmin() && Account.adminEnabled())
                   return 'A';
                else if(Events.getCurrentEvent() != undefined && Account.adminEnabled()) {
                 return Account.isEventAdmin(Events.getCurrentEvent().id)
               }
                return false;
            };
            var navigationContainer = $('#cd-nav'),
                mainNavigation = navigationContainer.find('#cd-main-nav ul');
            navigationContainer.addClass('is-fixed').find('.cd-nav-trigger').one('webkitAnimationEnd oanimationend msAnimationEnd animationend', function(){
                mainNavigation.addClass('has-transitions');
            });
            $(elem).find('.cd-nav-trigger').on('click', function(){
               $rootScope.toggleMenu();
            });
        }
    }
}
smartMenu.$inject = ["$rootScope" , "Account", "Events"];
