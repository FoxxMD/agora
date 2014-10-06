/**
 * Created by Matthew on 8/22/2014.
 */
// @ngInject
angular.module('gtfest')
    .service('Guilds', ["Restangular", "$q", "$rootScope", function (Restangular, $q, $rootScope) {

        var guilds = Restangular.all('guilds');

        this.getGuilds = function (pageNo, eventId) {
            pageNo == undefined ? pageNo = 1 : pageNo;
            if (eventId !== undefined) {
                var eventTeams = Restangular.service('guilds', Restangular.one('events', eventId));
                return eventTeams.getList({page: pageNo});
            }
            else {
                return guilds.getList({page: pageNo});
            }
        };
        this.createGuild = function (guild) {
            var deferred = $q.defer();
            guilds.post(guild).then(function (response) {
                    $rootScope.$broadcast('notify', 'notice', 'The guild <strong>'+guild.name+'</strong> has been created!', 4000);
                    deferred.resolve(response)
                },
                function (error) {
                    $rootScope.$broadcast('notify', 'warning', response, 4000);
                    deffered.reject();
                });
            return deferred.promise;
        };
        this.getGuild = function(guildId) {
            return guilds.one(guildId).get();
        };
        this.isCaptain = function(userId, guildData) {
           return $.grep(guildData.members, function (t) {
                return t.User.id == userId && t.User.isCaptain
            }).length > 0;
        };
        this.userInGuild = function(userId, guildData) {
            return $.grep(guildData.members, function (t) {
                return t.User.id == userId }).length > 0;
        };
        this.joinGuild = function(userId, guildData) {
            return guilds.one(guildData.id.toString()).post('members',{userId: userId});
        };
        this.leaveGuild = function(userId, guildData) {
            return guilds.one(guildData.id.toString()).one('members').remove({userId: userId});
        };
    }]);
