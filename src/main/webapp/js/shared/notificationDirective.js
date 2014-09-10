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
            var notification = {};
            $rootScope.$on('closeNotification', function(){
                notification.dismiss();
            });
            //time is measured in milliseconds
            $rootScope.$on('notify',function(event, type, message, time, notificationType)
            {
                var icon = 'fa-lightbulb-o';
                switch(type) {
                    case 'important':
                        icon = 'fa-exclamation-circle';
                        type = 'notice';
                        break;
                    case 'warning':
                        icon = 'fa-warning';
                        break;
                    case 'error':
                        icon = 'fa-close';
                        break;
                }
                notification = new NotificationFx({
                    message : '<i class="fa '+icon+' fa-2x"></i><p>'+ message +'</p>',
                    layout : notificationType || 'bar',
                    effect : notificationType ? 'slidebottom' : 'slidetop',
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