var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

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
      $('ul.tabs li').click(function(){
        var tab_id = $(this).attr('data-tab');
        $('ul.tabs li').removeClass('current');
        $('.tab-content').removeClass('current');
        $(this).addClass('current');
        $("#"+tab_id).addClass('current');
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
    if( $('#hamburger').is(':visible') ) {
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


/* 20.05.15 추가 */
function qr_edit(item,event) {
  $(item).children().addClass("edit_img_active");
  $(item).next().addClass("qr_save_active");
  var input_di = $(item).parent().next().next().children().children().find('textarea')[0];
  input_di.disabled = false;
  var input_di2 = $(item).parent().next().next().next().children().children().find('textarea')[0];
  input_di2.disabled = false;
}

function qr_save(item,event){
  $(item).prev().children().removeClass("edit_img_active");
  $(item).prev().next().removeClass("qr_save_active");
  var input_di =  $(item).parent().next().next().children().children().find('textarea')[0];
  input_di.disabled = true;
  var input_di2 =  $(item).parent().next().next().next().children().children().find('textarea')[0];
  input_di2.disabled = true;

  var modelName = $('#modelName').val();
  var srlName = $('#srlName').val();
  var qrSrl = $('#qrSrl').text();
  var jsonData = { "qrSrl" : qrSrl ,"modelName" : modelName, "srlName": srlName};
  if(confirm('수정하시겠습니까?'))
  {
    ajax_update_obj(jsonData);
  }

}//end
/* 20.05.18 추가*/
function ajax_update_obj(data)
{
  $.ajax
  ({
      type: 'POST',
      contentType: "application/json",
      url:'/ajax/obj/update',
      data : JSON.stringify(data),
      beforeSend : function(xhr)
      {
        xhr.setRequestHeader(header, token);
      },
      dataType : "text",
      cache : false,
      success : function(data)
      {
        if(data == 'true')
        {
          location.reload();
        }
      },
      error : function(xhr, status, error)
      {
        console.log('error ====> ' + error);
      }
  });
}

function manage_history_paging(pageNum)
{
  var qrSrl = $('#qrSrl').text();
  var jsonData = { "qrSrl" : qrSrl ,"pageNum" : pageNum};
  ajax_call_obj_history_list(jsonData);
  ajax_call_obj_history_page(jsonData);
}//end
function ajax_call_obj_history_list(jsonData)
{
  $.ajax
  ({
    type: 'POST',
    contentType: "application/json",
    url:'/ajax/obj/history/list',
    data : JSON.stringify(jsonData),
    beforeSend : function(xhr)
    {
      xhr.setRequestHeader(header, token);
    },
    dataType : "json",
    cache : false,
    success : function(data)
    {
      var html = '';
      if(data.length > 0)
      {
        for(var i = 0; i < data.length; i++)
        {
          html += '<tr>';
          html += '<td>' + data[i].createAt + '</td>';
          if(data[i].objStatus == 'return_finish') //반납 완료
          {
            html += '<td>' + data[i].holderName + '</td>';
          }
          else
          {
            html += '<td>' + data[i].userName + '</td>';
          }
          html += '<td>' + data[i].addr + '</td>';
          if(data[i].objStatus == 'inner_wait') //내부자산 등록
          {
            html += '<td><b>내부자산 등록</b></td>';
          }
          if(data[i].objStatus == 'outer_wait') //외부자산 등록
          {
            html += '<td><b>외부자산 등록</b></td>';
          }
          if(data[i].objStatus == 'release_start') //출고 시작
          {
            html += '<td><b>출고 시작</b></td>';
          }
          if(data[i].objStatus == 'release_finish') //출고 완료
          {
            html += '<td><b>출고 완료</b></td>';
          }
          if(data[i].objStatus == 'return_wait') //반납 대기
          {
            html += '<td><b>반납 대기</b></td>';
          }
          if(data[i].objStatus == 'return_finish') //반납 완료
          {
            html += '<td><b>반납 완료</b></td>';
          }
          if(data[i].objStatus == 'return_start') //반납 시작
          {
            html += '<td><b>반납 시작</b></td>';
          }
          html += '</tr>';
        }
      }
      else
      {
        html += '<tr><td colspan=4>데이터가 없습니다.</tr></td>'
      }
      $('#objHistoryList').html(html);
    },
    error : function(xhr, status, error)
    {
      console.log('error ====> ' + error);
    }
  });
}//end
function ajax_call_obj_history_page(jsonData)
{
  $.ajax
  ({
    type: 'POST',
    contentType: "application/json",
    url:'/ajax/obj/history/page',
    data : JSON.stringify(jsonData),
    beforeSend : function(xhr)
    {
      xhr.setRequestHeader(header, token);
    },
    dataType : "json",
    cache : false,
    success : function(data)
    {
      var html = '';
      if(data.prev)
      {
        html += '<span> <img src="/image/paging/list-previous.svg" onclick="pageClick(this);" id='+(data.startPage - 1 )+' name="pageNum"></span>';
      }
      if (data.totalcount != 0)
      {
        for (var i = data.startPage; i <= data.endPage; i++)
        {
          html += '<span class="page_num">';
          if(data.pagenum == i)
          {
            html += '<span class="page" id="'+ i + '"onclick="pageClick(this);" style="cursor:pointer; font-weight:800;">' + i + '</span>';
          }
          else
          {
            html += '<span class="page" id="'+ i + '"onclick="pageClick(this);" style="cursor:pointer;">' + i + '</span>';
          }

          html += '</span>';
        }
      }
      if(data.next)
      {
        html += '<span> <img src="/image/paging/list-next.svg" onclick="pageClick(this);" id='+(data.startPage + 1 )+' name="pageNum"></span>';
      }
      $('#pageHandler').html(html);
    },
    error : function(xhr, status, error)
    {
      console.log('error ====> ' + error);
    }
  });
}//end
function pageClick(obj)
{
  var qrSrl = $('#qrSrl').text();
  var pageNum = obj.id;
  var jsonData = { "qrSrl" : qrSrl ,"pageNum" : pageNum};


  ajax_call_obj_history_list(jsonData);
  ajax_call_obj_history_page(jsonData);
}//end
function create_map()
{
  var latitude = $('#latitude').val();
  var longitude = $('#longitude').val();
  var mapContainer = document.getElementById('map'), // 지도를 표시할 div
      mapOption = {
        center: new kakao.maps.LatLng(latitude, longitude), // 지도의 중심좌표
        level: 3 // 지도의 확대 레벨
      };
  var map = new kakao.maps.Map(mapContainer, mapOption); // 지도를 생성합니다
  var markerPosition  = new kakao.maps.LatLng(latitude, longitude);
  var marker = new kakao.maps.Marker({
    position: markerPosition
  });

  marker.setMap(map);
}//end
function delete_asset()
{
  var qrSrl = $('#qrSrl').text();
  var jsonData = { "qrSrl" : qrSrl};
  if(qrSrl != null || qrSrl != '')
  {
    confirm(qrSrl+'자산을 삭제하시겠습니까?')
    {
      $.ajax
      ({
        type: 'POST',
        contentType: "application/json",
        url:'/ajax/obj/delete',
        data : JSON.stringify(jsonData),
        beforeSend : function(xhr)
        {
          xhr.setRequestHeader(header, token);
        },
        dataType : "text",
        cache : false,
        success : function(data)
        {
          if(data)
          {
            alert('해당 자산을 삭제 했습니다.');
            window.history.back();
          }
        },
        error : function(xhr, status, error)
        {
          console.log('error ====> ' + error);
        }
      });
    }
  }
} //end
