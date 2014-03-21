angular.module('app.directives', [])
    .directive('scheduleDirective', ['$compile', function ($compile) {
        var directive = {
            restrict: 'AE',
            template: '<button class="btn btn-info pull-right" ng-click="changeDay(\'2014-03-08\')">Day 1</button><button class="btn btn-info pull-right" ng-click="changeDay(\'2014-03-09\')">Day 2</button><div id="calendar" style="float:left;"></div>',
            link: function (scope, element, attrs) {

                var calendar = $(element).find('#calendar').calendar({
                    events_source: [
                        {
                            'id': 1,
                            'title': 'Test Event',
                            'start': 1394301600000,
                            'end': 1394308800000,
                            'class': 'event-inverse'
                        },
                        {
                            'id': 2,
                            'title': 'Test Event 2',
                            'start': 1394305200000,
                            'end': 1394312400000,
                            'class': 'event-inverse'
                        }
                    ],
                    tmpl_path: 'js/calendar/tmpls/',
                    view: 'day',
                    day: '2014-03-08'
                });

                scope.changeDay = function (theDay) {
                    calendar.setOptions({day: theDay});
                    calendar.view();
                };

            }
        };
        return directive;
    }])
    //stolen from http://rogeralsing.com/2013/08/26/angularjs-directive-to-check-that-passwords-match-followup/
    .directive('passwordMatch', [function () {
        return {
            restrict: 'A',
            scope: true,
            require: 'ngModel',
            link: function (scope, elem, attrs, control) {
                var checker = function () {

                    //get the value of the first password
                    var e1 = scope.$eval(attrs.ngModel);

                    //get the value of the other password
                    var e2 = scope.$eval(attrs.passwordMatch);
                    return e1 == e2;
                };
                scope.$watch(checker, function (n) {

                    //set the form control to valid if both
                    //passwords are the same, else invalid
                    control.$setValidity("unique", n);
                });
            }
        };
    }])
    .directive('stripeDir', ['$rootScope','userService','$state', function($rootScope, userService, $state){
        return {
            restrict:'A',
            templateUrl:'/templates/pay.html',
            link: function(scope, elem, attrs) {
                Stripe.setPublishableKey('pk_test_C5kuVaBMR3FiCbMYfxS9mxpq'); //test key
            },
            controller: function($scope){
                if(userService.getProfile().paid == 1)
                {
                    userService.alreadyPaid(true);
                    $state.go('profile');
                }

                $scope.handleStripe = function(status, response) {
                    if(response.error) {
                        // there was an error. Fix it.
                        $scope.formErrorMessage = '<strong>There was an error submitting your information.</strong> Please ensure that you have input all of your information correctly and try again.';
                        $scope.formErrorTechMessage = response.error;
                    } else {
                        // got stripe token, send
                        var data = { token: response.id};
                        userService.payRegistration(data).promise.then(function(response) {
                            //payment success
                            if(response != null)
                            {
                                userService.justPaid(true);
                                $state.go('profile');
                            } else {
                                $scope.formErrorMessage = '<strong>Your payment was submitted successfully! But there was a problem recording your payment.</strong> Please contant an admin to fix this issue. <strong>Do not resubmit your payment!</strong>';
                                $scope.formErrorTechMessage = response;
                            }

                        }, function(response){
                            $scope.formErrorMessage = '<strong>There was an error processing your payment, you have not been charged.</strong> Please ensure your card and billing information is correct before trying again.';
                            $scope.formErrorTechMessage = response;
                        });
                    }
                }
            }

        };
    }])
    .directive('gamesectionDir', [function(){
        return {
            restrict:'A',
            templateUrl:'templates/games.html',
            link: function(scope, element, attrs){
                $(element).find('.thumbnail').on('click', function(ev, target){
                    $(element).find('.thumbnail').removeClass('active orange');
                    $(this).addClass('active orange');
                });
            }
        }
    }])
    .directive('gameDir', ['$http','$state', function($http, $state){
       return {
            restrict:'A',
           templateUrl:'templates/gameDirective.html',
            controller: function($scope, $element){
                $http.get($state.current.data).success(function(data){
                    $scope.gameInfo = data;
                });
            },
            link: function(scope, element, attrs){

            }
        }
    }])
    .directive('aboutDir', ['$http', function($http){
        return {
            restrict:'A',
            templateUrl:'templates/about.html',
            controller:function($scope){
                $http.get('content/credits.json').success(function(data)
                {
                    $scope.credits = data;
                });
            },
            link: function(scope, element, attrs)
            {
                $(element).find('#regCredits').on('click',function(){
                    $(element).find('#start').remove();
                    $(element).find('#titles').attr('id','boringtitle');
                    $(element).find('#titlecontent').attr('id','boringtitlecontent');
                    $(element).closest('html').css('overflow','auto');
                });
            }
        }
    }]);