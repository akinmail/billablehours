jQuery(document).ready(function( $ ) {
  url = 'csv'

  $(window).scroll(function () {
    var height = $(window).height();
    var scroll = $(window).scrollTop();
    if (scroll) {
      $(".header-hide").addClass("scroll-header");
    } else {
      $(".header-hide").removeClass("scroll-header");
    }

  });

  // Back to top button
  $(window).scroll(function() {
    if ($(this).scrollTop() > 100) {
      $('.back-to-top').fadeIn('slow');
    } else {
      $('.back-to-top').fadeOut('slow');
    }
  });
  $('.back-to-top').click(function(){
    $('html, body').animate({scrollTop : 0},1500, 'easeInOutExpo');
    return false;
  });

  // Initiate the wowjs animation library
  new WOW().init();

  // Initiate superfish on nav menu
  $('.nav-menu').superfish({
    animation: {
      opacity: 'show'
    },
    speed: 400
  });

  // Mobile Navigation
  if ($('#nav-menu-container').length) {
    var $mobile_nav = $('#nav-menu-container').clone().prop({
      id: 'mobile-nav'
    });
    $mobile_nav.find('> ul').attr({
      'class': '',
      'id': ''
    });
    $('body').append($mobile_nav);
    $('body').prepend('<button type="button" id="mobile-nav-toggle"><i class="fa fa-bars"></i></button>');
    $('body').append('<div id="mobile-body-overly"></div>');
    $('#mobile-nav').find('.menu-has-children').prepend('<i class="fa fa-chevron-down"></i>');

    $(document).on('click', '.menu-has-children i', function(e) {
      $(this).next().toggleClass('menu-item-active');
      $(this).nextAll('ul').eq(0).slideToggle();
      $(this).toggleClass("fa-chevron-up fa-chevron-down");
    });

    $(document).on('click', '#mobile-nav-toggle', function(e) {
      $('body').toggleClass('mobile-nav-active');
      $('#mobile-nav-toggle i').toggleClass('fa-times fa-bars');
      $('#mobile-body-overly').toggle();
    });

    $(document).click(function(e) {
      var container = $("#mobile-nav, #mobile-nav-toggle");
      if (!container.is(e.target) && container.has(e.target).length === 0) {
        if ($('body').hasClass('mobile-nav-active')) {
          $('body').removeClass('mobile-nav-active');
          $('#mobile-nav-toggle i').toggleClass('fa-times fa-bars');
          $('#mobile-body-overly').fadeOut();
        }
      }
    });
  } else if ($("#mobile-nav, #mobile-nav-toggle").length) {
    $("#mobile-nav, #mobile-nav-toggle").hide();
  }

  // Smooth scroll for the menu and links with .scrollto classes
  $('.nav-menu a, #mobile-nav a, .scrollto').on('click', function() {
    if (location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '') && location.hostname == this.hostname) {
      var target = $(this.hash);
      if (target.length) {
        var top_space = 0;

        if ($('#header').length) {
          top_space = $('#header').outerHeight();

          if( ! $('#header').hasClass('header-fixed') ) {
            top_space = top_space - 20;
          }
        }

        $('html, body').animate({
          scrollTop: target.offset().top - top_space
        }, 1500, 'easeInOutExpo');

        if ($(this).parents('.nav-menu').length) {
          $('.nav-menu .menu-active').removeClass('menu-active');
          $(this).closest('li').addClass('menu-active');
        }

        if ($('body').hasClass('mobile-nav-active')) {
          $('body').removeClass('mobile-nav-active');
          $('#mobile-nav-toggle i').toggleClass('fa-times fa-bars');
          $('#mobile-body-overly').fadeOut();
        }
        return false;
      }
    }
  });


  // Init Owl Carousel
  $('.owl-carousel').owlCarousel({
    items: 4,
    autoplay: true,
    loop: true,
    margin: 30,
    dots: true,
    responsiveClass: true,
    responsive: {

      320: { items: 1},
      480: { items: 2},
      600: { items: 2},
      767: { items: 3},
      768: { items: 3},
      992: { items: 4}
    }
  });

// custom code



  var Upload = function (file) {
    this.file = file;
  };

  Upload.prototype.getType = function() {
    return this.file.type;
  };
  Upload.prototype.getSize = function() {
    return this.file.size;
  };
  Upload.prototype.getName = function() {
    return this.file.name;
  };
  Upload.prototype.doUpload = function () {
    var that = this;
    var formData = new FormData();

    // add assoc key values, this will be posts values
    formData.append("file", this.file, this.getName());
    formData.append("upload_file", true);

    $.ajax({
      type: "POST",
      url: url + "/upload",
      xhr: function () {
        var myXhr = $.ajaxSettings.xhr();
        if (myXhr.upload) {
          myXhr.upload.addEventListener('progress', that.progressHandling, false);
        }
        return myXhr;
      },
      success: function (data) {
        // your callback here
        console.log(data)

        var number_of_rows = data.length;
        var table_body = "Result<br>";
        for(j=0; j<number_of_rows; j++){
          table_body += '<br><br>' +data[j].companyName+"<br>"+
              '<table border="1" id="example"><thead><tr><th>Employee ID</th><th>Number of Hours</th><th>Unit Price</th><th>Cost</th></tr></thead><tbody>';

          for(i =0;i<data[j].employeeBills.length;i++){

            table_body+='<tr>';
            table_body +='<td>';
            table_body +=data[j].employeeBills[i]["id"];
            table_body +='</td>';

            table_body +='<td>';
            table_body +=data[j].employeeBills[i]["noOfHours"];
            table_body +='</td>';

            table_body +='<td>';
            table_body +=data[j].employeeBills[i]["unitPrice"];
            table_body +='</td>';

            table_body +='<td>';
            table_body +=data[j].employeeBills[i]["cost"];
            table_body +='</td>';

            table_body+='</tr>';
          }
          table_body+='</tbody></table>';
          $('#mainContent').hide();
          $('#tableDiv').html(table_body);
          alert(table_body);

        }


      },
      error: function (error) {
        // handle error
      },
      async: true,
      data: formData,
      cache: false,
      contentType: false,
      processData: false,
      timeout: 60000
    });
  };

  Upload.prototype.progressHandling = function (event) {
    var percent = 0;
    var position = event.loaded || event.position;
    var total = event.total;
    var progress_bar_id = "#progress-wrp";
    if (event.lengthComputable) {
      percent = Math.ceil(position / total * 100);
    }
    // update progressbars classes so it fits your code
    $(progress_bar_id + " .progress-bar").css("width", +percent + "%");
    $(progress_bar_id + " .status").text(percent + "%");
  };




  $("#csvFormSubmit").click(function(e) {

    e.preventDefault(); // avoid to execute the actual submit of the form.

    var fileInput = document.getElementById('the-file');
    var upload = new Upload(fileInput.files[0]);

    // maby check size or type here with upload.getSize() and upload.getType()

    // execute upload
    upload.doUpload();

  });

});
