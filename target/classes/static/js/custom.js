$(document).ready(function () {
  //$("#input_id").focus();
  var temp = "11111111";
  
  $("#input_id").focus(function () {
    
    $(this).next(".underline1").css("border-bottom", "1px solid #000000");
  });
  $("#input_id").blur(function () {
    
    $(this).next(".underline1").css("border-bottom", "1px solid #b8b8b8");
  });
  
  $("#input_pass").focus(function () {
    $(this).next(".underline2").css("border-bottom", "1px solid #000000");
  });
  $("#input_pass").blur(function () {
    
    $(this).next(".underline2").css("border-bottom", "1px solid #b8b8b8");
  });
  

  var selectTarget = $('.selectbox select');

  // focus 가 되었을 때와 focus 를 잃었을 때
  selectTarget.on({
    'focus': function() {
      $(this).parent().addClass('focus');
    },
    'blur': function() {
      $(this).parent().removeClass('focus');
    }
  });

  selectTarget.change(function() {
    var select_name = $(this).children('option:selected').text();
    $(this).siblings('label').text(select_name);
    $(this).parent().removeClass('focus');
  });

});
/*function focusFunct(className) {
        if (className == "input_id") {
          document.getElementsByClassName("underline1").style.backgroundColor =
            "#000000";
        } else {
          document.getElementsByClassName("underline2").style.backgroundColor =
            "#000000";
        }
      }*/
