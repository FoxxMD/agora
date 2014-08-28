/**
 * Created by Matthew on 8/22/2014.
 */
angular.module('gtfest')
    .service('Teams', function (Restangular, $q, $rootScope) {

        var teams = Restangular.all('teams');

        this.getTeams = function (pageNo, eventId) {
            pageNo == undefined ? pageNo = 1 : pageNo;
            if (eventId !== undefined) {
                var eventTeams = Restangular.service('teams', Restangular.one('events', eventId));
                return eventTeams.getList({page: pageNo}).$object;
            }
            else {
                return teams.getList({pageNo: pageNo}).$object;
            }
        };
        this.createTeam = function (team) {
            var deferred = $q.defer();
            teams.post(team).then(function (response) {
                    $rootScope.$broadcast('notify', 'notice', 'The team <strong>'+team.name+'</strong> has been created!', 4000);
                    deferred.resolve(response)
                },
                function (error) {
                    $rootScope.$broadcast('notify', 'warning', response, 4000);
                    deffered.reject();
                });
            return deferred;
        }
    });