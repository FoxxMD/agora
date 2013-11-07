$(document).ready(function () {
    $('#main-menu-left.nav a ').on('click', function (e) {
        $('.contentContainer').fadeTo(400, 0, function () {
            $('.contentPane').spin();
            $.ajax({
                url: $(e.currentTarget).attr('data-link'),
            }).done(function (data) {
                $('.contentContainer').html(data);
                $('.contentPane').spin(false);
                $('.contentContainer').fadeTo(400, 1, function () {
                });
            });
        });
    });
});