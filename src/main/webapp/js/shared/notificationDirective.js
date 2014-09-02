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
            //time is measured in milliseconds
            $rootScope.$on('notify',function(event, type, message, time)
            {
                var notification = new NotificationFx({
                    message : '<span class="icon icon-megaphone"></span><p>'+ message +'</p>',
                    layout : 'bar',
                    effect : 'slidetop',
                    type : type, // notice, warning or error,
                    ttl: time == undefined ? 6000 : time,
                    wrapper: document.body
                });
                // show the notification
                notification.show();
            });
        }
    }
}
notify.$inject = ["$rootScope"];