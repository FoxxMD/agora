/**
 * Created by Matthew on 10/1/2014.
 */
// @ngInject
angular.module('gtfest')
    .service('Tournaments', ["Restangular", "$q", "$rootScope","Account", function (Restangular, $q, $rootScope, Account) {
        var currentTournament = undefined;

        function RestTour(eventId) {
            return Restangular.all('events').one(eventId).all('tournaments');
        }

        this.getCurrent = function(){
            return currentTournament;
        };

        this.setCurrent = function(tour) {
            currentTournament = tour;
        };

        this.getTournaments = function(eventId, pageNo) {
           return RestTour(eventId).getList({page: pageNo});
        };
        this.getTournament = function(eventId, tourId) {
            return RestTour(eventId).one(tourId).get();
        };
        this.createTournament = function(eventId, tourData) {
            return RestTour(eventId).post(tourData);
        };

        this.isModerator = function(userId, tour) {
            tour = tour || currentTournament;
            return _.find(tour.users, function(tuser) {
                return tuser.id == userId && (tuser.isModerator || tuser.isAdmin);
            });
        };
        this.isAdmin = function(userId, tour) {
            tour = tour || currentTournament;
            return _.find(tour.users, function(tuser) {
                return tuser.id == userId && tuser.isAdmin;
            });
        };
        this.isOnTeamInRoster = function(userId, tour) {
            tour = tour || currentTournament;
            _.find(tour.teams, function(team) {
               return _.find(team.teamPlayers, function(player){
                    return player.User.id == userId;
                }) !== undefined;
            });
        };
        this.isUserInRoster = function(userId, tour) {
            tour = tour || currentTournament;
            _.find(tour.users, function(user) {
                return user.id == userId;
            })
        };
        this.canAddTeamMember = function(team, tour) {
            tour = tour || currentTournament;
          return team.length < tour.details.teamMaxSize || tour.details.teamMaxSize == 0;
        };
        this.isValidTeamSize = function(team, tour) {
            return (team.length <= tour.details.teamMaxSize || tour.details.teamMaxSize == 0) && (team.length >= tour.details.teamMinSize || tour.details.teamMinSize == 0);
        };
        this.createTeam = function(eventId, tourId, teamData) {
          return RestTour(eventId).one(tourId).post('teams',teamData);
        }
    }]);
