/*
/* nav 
$('#nav ul li a').hover(function () {
    $('.dep_area').stop(true,false).slideDown();
},

function () {
    $('.dep_area').stop(true,false).slideUp();
});


/* function active */
$(function(){
	  var sBtn = $(".function-box");    //  function-bo를 sBtn으로 칭한다. 
	  sBtn.find("a").click(function(){   // sBtn에 속해 있는  a 찾아 클릭 하면.
	   sBtn.removeClass("active");     // sBtn 속에 (active) 클래스를 삭제 한다.
	   $(this).parent().addClass("active"); // 클릭한 a에 (active)클래스를 넣는다.
	  });
	  
	  var aBtn = $(".function-box");
	  aBtn.find("a").click(function(){
		  aBtn.removeClass("")
	  });
});
// pc
var speed = 0.3;
function pcMenu(){
  var depthBg = $('.depth2-bg');

  $('#gnb').on('mouseenter',function(){
    $(this).find('.depth2').stop().fadeIn('fast');
    depthBg.stop().fadeIn('fast');
  });

  $('#header').on('mouseleave',function(){
    $('#gnb').find('.depth2').stop().fadeOut('fast');
    depthBg.stop().fadeOut('fast');
  });
}
// mobile
function moMenu(){
  hamburgerToggle();
  noTarget();
}

function noTarget(){
  $('.gnb-wrap > li > a').on('click',function(){
    if( $('#hamburger').is(':visible'))
    {
      $(this).parent().siblings().children('.depth2').slideUp('fast');
      $(this).next('.depth2').stop().slideToggle('fast');
    }

    return false;
  });
}

function hamburgerToggle(){
  $('#hamburger').on('click',function(){
  
    var $this = $(this),
        barTop = new TimelineMax(),
        barBot = new TimelineMax();

    $this.toggleClass('open');

    if( $this.hasClass('open') ){
      TweenMax.to($this.children('.bar-mid'), speed, {
        autoAlpha: 0
      });

      barTop.to($this.children('.bar-top'), speed/2, {
        top: '50%'
      }).to($this.children('.bar-top'), speed/2, {
        rotation: 45
      });

      barBot.to($this.children('.bar-bot'), speed/2, {
        top: '50%'
      }).to($this.children('.bar-bot'), speed/2, {
        rotation: -45
      });

      $('#gnb').stop().fadeIn('fast');

    } else {
      TweenMax.to($this.children('.bar-mid'), speed, {
        autoAlpha: 1
      });

      barTop.to($this.children('.bar-top'), speed/2, {
        rotation: 0
      }).to($this.children('.bar-top'), speed/2, {
        top: 17
      });

      barBot.to($this.children('.bar-bot'), speed/2, {
        rotation: 0
      }).to($this.children('.bar-bot'), speed/2, {
        top: 37
      });

      $('#gnb').stop().fadeOut('fast');

      if( $('#gnb').find('.depth2').is(':visible') ) {
        $('#gnb').find('.depth2').stop().slideUp('fast');
      }
    }

  });
}
// 삭제 이벤트 핵심
function delEvent(){
  $('#gnb').removeAttr('style');
  // pc의 모든 이벤트및 내용 삭제
  $('#gnb, #header').off();
  $('#gnb').find('.depth2').removeAttr('style');
  // mobile의 모든 이벤트및 내용삭제
  $('.gnb-wrap > li > a, #hamburger').off();
  $('.gnb-wrap').find('.depth2').removeAttr('style');
  $('#hamburger').removeAttr('class');
  $('#hamburger').find('span').removeAttr('style');
}

$(window).on('resize',function(){
  delEvent(); // 삭제 이벤트

  if ($(this).width() >= 767 ) {
    console.log('pc');
    pcMenu();
  }else{
    console.log('mobile');
    moMenu();
  }

}).resize();

/* 탭 Active */
$(document).ready(function(){

	$('ul.tabs li').click(function(){
		var tab_id = $(this).attr('data-tab');
        if(tab_id == 'tab-1') //배송 자산
        {
          location.href = "/obj/status/shipping/list/1";
        }
        if(tab_id == 'tab-2') //출고 자산
        {
          location.href = "/obj/status/release/list/1";
        }
	});

});

/*check 전체선택 및 전체해제*/
function checkAll(){
      if( $("#checkAll").is(':checked') ){
        $(".check").prop("checked", true);
      }else{
        $(".check").prop("checked", false);
      }
}