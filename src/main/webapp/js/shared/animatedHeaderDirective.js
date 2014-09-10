/**
 * Created by Matthew on 8/13/2014.
 */
angular.module('gtfest')
.directive('animatedheader', animatedheader);

// @ngInject
function animatedheader(Events){
    return {
        templateUrl:'views/shared/animatedheader.html',
        restrict:'E',
       controllerAs:'headerCtrl',
        controller: function(){
        },
        link: function(scope,elem,attrs)
        {
            var docElem = document.documentElement,
                header = elem.find('.cbp-af-header')[0],
                content = $(document).find('.st-content')[0],
                didScroll = false,
                changeHeaderOn = 100;

            function init() {
                content.addEventListener( 'scroll', function( event ) {
                    if( !didScroll ) {
                        didScroll = true;
                        setTimeout( scrollPage, 250 );
                    }
                }, false );
            }

            function scrollPage() {
                var sy = scrollY();
                if ( sy >= changeHeaderOn ) {
                    classie.add( header, 'cbp-af-header-shrink' );
                }
                else {
                    classie.remove( header, 'cbp-af-header-shrink' );
                }
                didScroll = false;
            }

            function scrollY() {
                return content.scrollTop;
            }

            init();
            var menu = new cbpHorizontalSlideOutMenu( elem.find('#cbp-hsmenu-wrapper' )[0] );
        }
    }
}