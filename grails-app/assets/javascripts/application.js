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
                        $companyOptions.append($("<li />").attr('value', this.uuid).text(this.canonicalName));
                        hasEntry = true
                    });
                    if (hasEntry) {
                        $companyOptions.show();
                        // $industryOptions.dropdown('toggle')
                    }
                });
            }
        });

        // $company.val();
        $(document.body).on('click change', '#companyOptions li' ,function(){
            let text = $(this).html();
            if (text!=='') {
                $company.val(text);
                $companyOptions.hide();
                var uuid = $(this).attr('value');
                $('#companyUUID').val(uuid);
                console.log('uuid', uuid);
                $('#addButton').show();
            } else {
                $('#addButton').hide()
                $companyOptions.hide()
            }
         });

        // add industry start:
        let $industry = $("#industryInput");
        let $industryOptions = $("#industryOptions");

        // $('#addIndustry').on('click', function(event) {
        //     event.preventDefault();
        //     $("#addIndustryToView").show()
        //     // location.replace("/view/addCompanyToView?uuid="+uuid+"&viewId="+viewId);
        // });

        $industry.on('input', function() {
            const input = $(this).val();
            $industryOptions.empty()
            $industryOptions.hide()
            $('#addIndButton').hide()
            if (input) {
                $.getJSON("/api/suggestIndustries?input=" + input, function (data) {
                    let hasEntry = false
                    $.each(data, function () {
                        $industryOptions.append($("<li />").text(this));
                        hasEntry = true
                    });
                    if (hasEntry) {
                        $industryOptions.show();
                        // $industryOptions.dropdown('toggle')
                    }
                });
            }
        });

        // $('#companyInput').val();
        $(document.body).on('click change', '#industryOptions li' ,function(){
            let text = $(this).html();
            if (text!=='') {
                $industry.val(text);
                $industryOptions.hide();
                $('.industry').val(text);
                console.log('industry', text);
                $('#addIndButton').show();
            } else {
                $('#addIndButton').hide()
                $industryOptions.hide()
            }
        });

        // add category start:
        let $category = $("#categoryInput");
        let $categoryOptions = $("#categoryOptions");

        // $('#addCategory').on('click', function(event) {
        //     event.preventDefault();
        //     $("#addCategoryToView").show()
        //     // location.replace("/view/addCompanyToView?uuid="+uuid+"&viewId="+viewId);
        // });

        $category.on('input', function() {
            const input = $(this).val();
            $categoryOptions.empty()
            $categoryOptions.hide()
            $('#addCatButton').hide()
            if (input) {
                $.getJSON("/api/suggestCategories?input=" + input, function (data) {
                    let hasEntry = false
                    $.each(data, function () {
                        $categoryOptions.append($("<li />").text(this));
                        hasEntry = true
                    });
                    if (hasEntry) {
                        $categoryOptions.show();
                        // $categoryOptions.dropdown('toggle')
                    }
                });
            }
        });

        // $('#companyInput').val();
        $(document.body).on('click change', '#categoryOptions li' ,function(){
            let text = $(this).html();
            if (text!=='') {
                $category.val(text);
                $categoryOptions.hide();
                $('.category').val(text);
                console.log('category', text);
                $('#addCatButton').show();
            } else {
                $('#addCatButton').hide()
                $categoryOptions.hide()
            }
        });


        // function onSelChange() {
        //     return function () {
        //         //$selectedCompany = $( "#companyOptions option:selected");
        //         //let text = $selectedCompany.text();
        //         if (text!=='') {
        //             $company.val(text);
        //             var uuid = $('#companyUUID').val($selectedCompany.val());
        //             var uuid = $("#companyOptions").find(":selected").attr('value');
        //             console.log('seelected uuid', uuid);
        //             $('#addButton').show();
        //         } else {
        //             $('#addButton').hide()
        //             $categoryOptions.hide()
        //         }
        //     };
        // }

        // $categoryOptions.on('change', onSelChange());
        // $categoryOptions.on('click', onSelChange());

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
