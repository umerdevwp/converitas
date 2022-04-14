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
      let $company = $("#organization");
      let $companyOptions = $("#companyOptions");

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
            var uuid = $('#uuid').val($selectedCompany.val());

            var uuid = $("#companyOptions").find(":selected").attr('value');
            console.log('seelected uuid', uuid);
            $('#addButton').show();
            $('#addButton').on('click', function(event) {
              event.preventDefault();
              location.replace("/organization/info?uuid="+uuid);
              });            
          } else {
            $('#addButton').hide()
            $companyOptions.hide()
          }
        };
      }


      // Company Lookup Section----------------------------------------------------------------
      let $companyL = $("#companyL");
      let $companyOptionsL = $("#companyOptionsL");
      $companyL.on('input', function() {
        const input = $(this).val();
        $companyOptionsL.empty()
        $companyOptionsL.hide()
        $('#addButtonL').hide()
        if (input) {
          $.getJSON("/api/suggestions?input=" + input, function (data) {
            let hasEntry = false
            $.each(data, function () {
              $companyOptionsL.append($("<option />").val(this.uuid).text(this.canonicalName));
              hasEntry = true
            });
            if (hasEntry) {
              $companyOptionsL.show();
              // $companyOptions.dropdown('toggle')
            }
          });
        }
      });
      
      function onSelChangeL() {
        return function () {
          $selectedCompanyL = $( "#companyOptionsL option:selected");
          let text = $selectedCompanyL.text();
          if (text!=='') {
            $companyL.val(text);
            var uuid = $('#uuid').val($selectedCompanyL.val());

            var uuid = $("#companyOptionsL").find(":selected").attr('value');
            console.log('seelected uuid', uuid);
            $('#addButtonL').show();
            $('#addButtonL').on('click', function(event) {
              event.preventDefault();
              location.replace("/organization/info?uuid="+uuid);
              });            
          } else {
            $('#addButtonL').hide()
            $companyOptions.hide()
          }
        };
      }      
      

      $companyOptions.on('change', onSelChange());
      $companyOptions.on('click', onSelChange());
      $companyOptionsL.on('change', onSelChangeL());
      $companyOptionsL.on('click', onSelChangeL()); 

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

