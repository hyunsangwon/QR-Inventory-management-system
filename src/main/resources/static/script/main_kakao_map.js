var _overlay = null;
var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

$(function(){
    var mapContainer = document.getElementById('map'), // 지도의 중심좌표
        mapOption = {
            center: new kakao.maps.LatLng(36.2082709,127.9770043), // 지도의 중심좌표
            level: 12 // 지도의 확대 레벨
        };
    var map = new kakao.maps.Map(mapContainer, mapOption); // 지도를 생성합니다
    //create_marker_map();
});

/* 지도 마커 생성*/
function create_marker_map()
{
    var userSrl = $('#userSrl').val();
    var jsonData = { "userSrl" : userSrl}
    $.ajax({
        type: 'POST',
        contentType: "application/json",
        url:'/ajax/call/kakaomap/marker',
        data: JSON.stringify(jsonData),
        beforeSend : function(xhr)
        {
            xhr.setRequestHeader(header, token);
        },
        dataType: "json",
        cache :false,
        success: function(data)
        {
            var marker = [];
            for(var i=0; i<data.length; i++)
            {
                marker[i] = new kakao.maps.Marker({
                    map: map,
                    position : new kakao.maps.LatLng(data[i].latitude,data[i].longitude),
                    title : data[i].companyName
                });
            }
        },
        error:function(xhr,status,error)
        {
            console.log('error ====> '+error);
        }
    });
}

//커스텀 오버레이를 닫기 위해 호출되는 함수입니다
function closeOverlay() {
    overlay.setMap(null);
}
