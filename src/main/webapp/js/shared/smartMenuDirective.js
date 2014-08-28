angular.module('gtfest')
.directive('smartMenu', smartMenu);

// @ngInject
function smartMenu($rootScope){
    return {
        templateUrl:'/views/shared/smartMenu.html',
        restrict: 'E',
        scope: true,
        controllerAs:'smartMenu',
        controller: function(Account){
            this.account = Account;
        },
        link: function(scope, elem, attrs, control)
        {
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