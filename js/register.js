$(document).ready(function () {

    $('#formPostModal').modal({
        show: false
    });

    $('#signup').validate({
        rules: {
            password: {
                minlength: 5
            },
            conpassword: {
                minlength: 5,
                equalTo: "#password"
            }
        }
    });
    $('#signup').on('submit', function (e) {
        e.preventDefault();
        if ($('#signup').valid()) {
            $.ajax({
                url: 'php/register.php',
                data: $(e.currentTarget).serialize(),
                method: 'POST',
            }).done(function (data) {
                if (data == 1)
                {
                    $('#formPostModal .modal-body').text('You are now registered for GT Gamefest! Check back later to login.')
                    $('#formPostModal').each(function () {
                        this.reset();
                    });
                }
                   
                else if (data == 0)
                    $('#formPostModal .modal-body').text('Registration was not successful! Either your Alias or Email may be duplicated, please try again.');
                $('#formPostModal').modal('show');
            });
        }
    });
});