/**
 * Created by Matthew on 10/1/2014.
 */
// @ngInject
angular.module('gtfest')
    .service('Tournaments', ["Restangular", "$q", "$stateParams", "$rootScope","Account","Events", function (Restangular, $q, $stateParams, $rootScope, Account, Events) {
        var currentTournament = undefined;
        var that = this;

        //var tournaments = Restangular.all('events').one($stateParams.eventId.toString()).all('tournaments');
        function restNew() {
            return Restangular.all('events').one($stateParams.eventId.toString()).all('tournaments');
        }
        function RestTour(eventId) {
            return Restangular.all('events').one(eventId).all('tournaments');
        };

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

        this.deleteTournament = function(tourId) {
            return restNew().one(tourId).remove();
        };

        this.update = function(data, tourId)
        {
            tourId = tourId || currentTournament.id.toString();
            return restNew().one(tourId).patch(data);
        };

        this.isModerator = function(userId, tour) {
            tour = tour || currentTournament;
            return tour != undefined && (that.isAdmin(userId, tour) || _.some(tour.users, {'id': userId, 'idModerator': true}));
/*            return _.find(tour.users, function(tuser) {
                return tuser.id == userId && (tuser.isModerator || tuser.isAdmin);
            });*/
        };
        this.isAdmin = function(userId, tour) {
            tour = tour || currentTournament;
            return tour != undefined && _.some(tour.users, {'id': userId, 'isAdmin': true});
/*            return _.find(tour.users, function(tuser) {
                return tuser.id == userId && tuser.isAdmin;
            });*/
        };
        this.isOnTeamInRoster = function(userId, tour) {
            tour = tour || currentTournament;
           return _.find(tour.teams, function(team) {
               return _.find(team.teamPlayers, function(player){
                    return player.User.id == userId;
                }) !== undefined;
            });
        };
        this.isUserInRoster = function(userId, tour) {
            tour = tour || currentTournament;
           return _.find(tour.users, function(user) {
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
        };
        this.joinTeam = function(teamId, userId, isCaptain, tourId) {
            tourId = tourId || currentTournament.id.toString();
            userId = userId || Account.user().id;
            isCaptain = isCaptain || false;
            return restNew().one(tourId).all('teams').one(teamId).all('members').post({userId:userId,isCaptain:isCaptain});
        };
        this.leaveTeam = function(teamId, userId, tourId) {
            tourId = tourId || currentTournament.id.toString();
            userId = userId || Account.user().id;
            return restNew().one(tourId).all('teams').one(teamId).all('members').remove({userId:userId});
        };
        this.deleteTeam = function(teamId, tourId) {
            tourId = tourId || currentTournament.id.toString();
            return restNew().one(tourId).all('teams').one(teamId).remove();
        };
        this.changeTeamPresent = function(team, tourId) {
            team.isPresent = !team.isPresent;
            tourId = tourId || currentTournament.id.toString();
            return restNew().one(tourId).all('teams').one(team.id.toString()).patch(team);
        };
        this.changeUserPresent = function(user, tourId) {
            user.isPresent = !user.isPresent;
            tourId = tourId || currentTournament.id.toString();
            return restNew().one(tourId).all('players').one(user.id.toString()).patch({isPresent: user.isPresent})
        };
        this.changeUserAdmin = function(user, tourId) {
            user.isAdmin = !user.isAdmin;
            tourId = tourId || currentTournament.id.toString();
            return restNew().one(tourId).all('players').one(user.id.toString()).patch({isAdmin: user.isAdmin})
        };
        this.changeUserModerator = function(user, tourId) {
            user.isModerator = !user.isModerator;
            tourId = tourId || currentTournament.id.toString();
            return restNew().one(tourId).all('players').one(user.id.toString()).patch({isModerator: user.isModerator})
        };
        this.addPlayer = function(userId, tourId) {
            userId = userId || Account.user().id.toString();
            tourId = tourId || currentTournament.id.toString();
            return restNew().one(tourId).all('players').post({userId: userId})
        };
        this.removePlayer = function(userId, tourId) {
            userId = userId || Account.user().id.toString();
            tourId = tourId || currentTournament.id.toString();
            return restNew().one(tourId).all('players').one(userId).remove();
        }
    }]);
