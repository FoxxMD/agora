/**
 * Created by Matthew on 8/11/2014.
 */
angular.module('gtfest')
.directive('notify', notify);
// @ngInject
function notify($rootScope, toaster)
{
    return {
        scope:true,
        restrict:'E',
        templateUrl:'views/shared/toaster.html',
        link: function(scope,elem,attrs)
        {
            var notification = {};
            $rootScope.$on('closeNotification', function(){
                toaster.clear();
            });
            $rootScope.$on('notify',function(event, type, message, time, notificationType)
            {
                if(type == 'notice')
                    type = 'note';

                if(typeof message === 'object')
                {
                    message = message.error || message.message || message.response;
                }
                toaster.pop(type, type, message, time == undefined ? 6000 : time, 'trustedHtml');
            });
        }
    }
}
notify.$inject = ["$rootScope", "toaster"];
