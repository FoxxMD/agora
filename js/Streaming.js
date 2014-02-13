$(document).ready(function () {
    $('.thumbnail').on('click', function (e) {
        console.log('click!');
        if (!$('#streamId').hasClass('showPane'))
            $('#streamId').addClass('showPane');
        $('.streamContainer').fadeTo(400, 0, function () {
            $('.streamPane').spin();
            $.ajax({
                url: $(e.currentTarget).attr('data-link'),
            }).done(function (data) {
                $('.streamContainer').html(data);
                $('.streamPane').spin(false);
                $('.streamContainer').fadeTo(400, 1, function () {
                });
            });
        });
    });
});