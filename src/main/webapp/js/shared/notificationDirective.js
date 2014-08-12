/**
 * Created by Matthew on 8/11/2014.
 */
angular.module('gtfest')
.directive('notify', notify);
// @ngInject
function notify($rootScope)
{
    return {
        scope:true,
        restrict:'E',
        link: function(scope,elem,attrs)
        {
            $rootScope.$on('notify',function(event, type, message)
            {
                var notification = new NotificationFx({
                    message : '<span class="icon icon-megaphone"></span><p>'+ message +'</p>',
                    layout : 'bar',
                    effect : 'slidetop',
                    type : type, // notice, warning or error,
                    ttl:10000
                });
                // show the notification
                notification.show();
            });
        }
    }
}