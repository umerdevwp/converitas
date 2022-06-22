// This is a manifest file that'll be compiled into application.js.
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
// You're free to add application-wide JavaScript to this file, but it's generally better
// to create separate JavaScript files as needed.
//
//= require jquery-3.3.1.min
//= require bootstrap
//= require popper.min
//= require tempcolor
//= require_self


$(
    function() {
        let $company = $("#companyInput");
        let $companyOptions = $("#companyOptions");

        $('#addCompany').on('click', function(event) {
            event.preventDefault();
            $("#addCompanyToView").show()
            // location.replace("/view/addCompanyToView?uuid="+uuid+"&viewId="+viewId);
        });


        $company.on('input', function() {
            const input = $(this).val();
            $companyOptions.empty()
            $companyOptions.hide()
            $('#addButton').hide()
            if (input) {
                $.getJSON("/api/suggestions?input=" + input, function (data) {
                    let hasEntry = false
                    $.each(data, function () {
                        $companyOptions.append($("<option />").val(this.uuid).text(this.canonicalName));
                        hasEntry = true
                    });
                    if (hasEntry) {
                        $companyOptions.show();
                        // $companyOptions.dropdown('toggle')
                    }
                });
            }
        });

        function onSelChange() {
            return function () {
                $selectedCompany = $( "#companyOptions option:selected");
                let text = $selectedCompany.text();
                if (text!=='') {
                    $company.val(text);
                    var uuid = $('#companyUUID').val($selectedCompany.val());
                    var uuid = $("#companyOptions").find(":selected").attr('value');
                    console.log('seelected uuid', uuid);
                    $('#addButton').show();
                } else {
                    $('#addButton').hide()
                    $companyOptions.hide()
                }
            };
        }

        $companyOptions.on('change', onSelChange());
        $companyOptions.on('click', onSelChange());
    }
);


$('.buttonWrapper').on('click', function() {
    $('.messageSection').removeClass('hide');
    setTimeout(function(){
        $('.messageSection').addClass('hide');
    }, 2000);
})


var tooltips = document.querySelectorAll('.cvtooltip  .ttspan');

window.onmousemove = function (e) {
    var x = (e.clientX + 20) + 'px',
        y = (e.clientY + 20) + 'px';
    for (var i = 0; i < tooltips.length; i++) {
        tooltips[i].style.top = y;
        tooltips[i].style.left = x;
    }
};

function requestUrl (url) {
    $.ajax({
        url: url,
        success: function(data) {
            console.log('call '+url+' succeeded');
        },
        error: function(err, status) {
            if (err.status===403) {
                location.replace("/auth/login?url="+window.location.href);
            }
            alert(err.responseJSON.message);
        }
    })
}
