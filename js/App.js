$(document).ready(function () {
    $('#main-menu-left.nav a ').on('click', function (e) {
        $('#mainContent.contentContainer').fadeTo(400, 0, function () {
            $('.contentPane').spin();
            $.ajax({
                url: $(e.currentTarget).attr('data-link'),
            }).done(function (data) {
                $('#mainContent.contentContainer').html(data);
                $('.contentPane').spin(false);
                $('#mainContent.contentContainer').fadeTo(400, 1, function () {
                });
            });
        });
    });
});