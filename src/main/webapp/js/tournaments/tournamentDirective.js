/**
 * Created by Matthew on 10/2/2014.
 */
angular.module('gtfest')
    .directive('tournament', tourDirective);
// @ngInject
function tourDirective(Tournaments, Events, Guilds, $state, $stateParams, Account, $q) {
    return {
        restrict: 'E',
        templateUrl: '/views/tournaments/tournament.html',
        controllerAs: 'tourCtrl',
        controller: /*@ngInject*/ ["$scope", function ($scope) {
            var that = this;
            this.tour = Tournaments.getCurrent();
            console.log(that.tour.plain());

            this.isAdmin = function() {
                return Account.isEventAdmin($stateParams.eventId) && Account.adminEnabled();
            };
            this.updateName = function(name) {
                Tournaments.update({name:name}).then(function(){
                    return true;
                }, function(){
                    return false;
                });
            };
            this.updateTime = function() {
              return Tournaments.update({timeStart: that.tour.details.timeStart, timeEnd: that.tour.details.timeEnd});
            }
        }],
        link: function (scope, elem, attrs) {
            var content = $(document).find('.st-content')[0],
                tabs = $(elem).find('.tabs')[0],
                fixed = false;
            content.addEventListener( 'scroll', function( event ) {
                if(!fixed && event.currentTarget.scrollTop > 196)
                {
                    $(tabs).addClass('fix');
                    fixed = true;
                }
                else if(fixed && event.currentTarget.scrollTop < 196){
                    $(tabs).removeClass('fix');
                    fixed = false;
                }
            }, false );
        }
    }
}
tourDirective.$inject = ["Tournaments", "Events", "Guilds", "$state", "$stateParams", "Account", "$q"];
