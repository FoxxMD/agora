/**
 * Created by Matthew on 10/1/2014.
 */
angular.module('gtfest')
    .controller('TournamentsController', toursCtrl);

// @ngInject
function toursCtrl($scope, Events, $state, $stateParams, Account, $timeout, $q, Tournaments, Games) {
    var that = this,
        pageNo = 1;
    this.state = $state;
    this.event = Events.getCurrentEvent();
    Tournaments.getTournaments($stateParams.eventId.toString()).then(function (response) {
        that.toursCollection = response.plain();
    });
    if($stateParams.gameFilter != null)
    {
        that.tourGameTags =[
            {
                text: $stateParams.gameFilter
            }
        ];
    }
    function newTournamentData() {
        return {
            registrationType: 'Public',
            details: {
                timeStart: that.event.details.timeStart,
                timeEnd: that.event.details.timeStart
            }
        };
    }

    this.createTourData = newTournamentData();
    this.getMoreTours = function () {
        that.busy = true;
        pageNo++;
        Tournaments.getTournaments($stateParams.eventId.toString(), pageNo).then(function (response) {
            if (response.length > 0) {
                that.toursCollection = that.toursCollection.concat(response.plain());
                that.busy = false;
            }
        });
    };

    this.loadTours = function (query) {
        var deferred = $q.defer();
        deferred.resolve(that.eventsCollection);
        return deferred.promise;
    };
    this.loadGames = function (query) {
        var deferred = $q.defer();
        var games = [];
        that.toursCollection.map(function (x) {
            if (games.indexOf(x.game.name) == -1)
                games.push(x.game.name)
        });
        deferred.resolve(games);
        return deferred.promise;
    };
    this.loadTypes = function (query) {
        var deferred = $q.defer();
        var theTypes = [];
        that.toursCollection.map(function (x) {
            if (theTypes.indexOf(x.tournamentType.name) == -1)
                theTypes.push(x.tournamentType.name)
        });
        deferred.resolve(theTypes);
        return deferred.promise;
    };

    this.filterTours = function (tour) {
        var passed = true;
        if (that.tourNameTags.length > 0) {
            passed = that.tourNameTags.filter(function (val, index, arr) {
                return tour.details.name.toLowerCase().indexOf(val.text.toLowerCase()) != -1
            }).length > 0;
        }
        if (that.tourGameTags.length > 0) {
            passed = that.tourGameTags.filter(function (val, index, arr) {
                return tour.game.name.toLowerCase().indexOf(val.text.toLowerCase()) != -1
            }).length > 0;
        }
        if (that.tourTypeTags.length > 0) {
            passed = that.tourTypeTags.filter(function (val, index, arr) {
                return tour.tournamentType.name.toLowerCase().indexOf(val.text.toLowerCase()) != -1
            }).length > 0;
        }
        return passed;
    };
    this.openStopTime = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();
        that.stopOpened = true;
    };
    this.openStartTime = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();
        that.startOpened = true;
    };
    this.format = 'dd-MMMM-yyyy';

    this.tryCreateTour = function (form) {
        $scope.$broadcast('show-errors-check-validity');
        if (form.$valid) {
            Tournaments.createTournament($stateParams.eventId, that.createTourData).then(function (response) {
                that.createTourData = newTournamentData();
                $scope.$broadcast('show-errors-reset');
                $state.go('eventSkeleton.tournament.roster', {eventId: that.event.id.toString(), tournamentId: response.id});
            });
        }
    };
    this.populateTourTypes = function (item) {
        console.log(item);
    };
    Games.getGames().then(function (games) {
        that.gamesCollection = $(games.plain()).not($scope.gamearray).get();
    });
}
toursCtrl.$inject = ["$scope", "Events", "$state", "$stateParams", "Account", "$timeout", "$q", "Tournaments", "Games"];
