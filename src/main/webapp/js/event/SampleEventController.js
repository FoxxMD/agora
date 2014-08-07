/**
 * Created by Matthew on 8/7/2014.
 */
angular.module('gtfest')
    .controller('SampleEventController', eventController);

// @ngInject
function eventController(Restangular){
    this.name = "NE Gamethang";
    this.tourneyNames = ['Frosty 2v2','Epic 4v4','Round-Robin(20 player)','Deathmatch 100-kill Win']
}
eventController.$inject = ["Restangular"];