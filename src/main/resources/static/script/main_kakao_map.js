var _overlay = null;

/* 지도 마커 생성*/
function create_marker_map(map)
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
            var innerMarkerImageSrc = "https://qr-s3.s3.ap-northeast-2.amazonaws.com/private/main-icon-map-c.svg";
            var outerMarkerImageSrc = "https://qr-s3.s3.ap-northeast-2.amazonaws.com/private/main-icon-map-a.svg";
            var imageSize = new kakao.maps.Size(24, 35); //마커 사이즈 수정

            for(var i=0; i<data.length; i++)
            {
                if(data[i].objStatus == 'return_finish' || data[i].objStatus == 'inner_wait'  || data[i].objStatus == 'return_wait')//내부 자산
                {
                    var markerImage = new kakao.maps.MarkerImage(innerMarkerImageSrc, imageSize);
                    var marker = new kakao.maps.Marker({
                        map: map,
                        position : new kakao.maps.LatLng(data[i].latitude,data[i].longitude),
                        title : data[i].companyName,
                        image : markerImage // 마커 이미지
                    });
                    create_pop_up(marker,map,data[i]);
                }
                if(data[i].objStatus == 'release_finish' || data[i].objStatus == 'outer_wait' || data[i].objStatus == 'release_start')//외부 자산
                {
                    var markerImage = new kakao.maps.MarkerImage(outerMarkerImageSrc, imageSize);
                    var marker = new kakao.maps.Marker({
                        map: map,
                        position : new kakao.maps.LatLng(data[i].latitude,data[i].longitude),
                        title : data[i].companyName,
                        image : markerImage // 마커 이미지
                    });
                    create_pop_up(marker,map,data[i]);
                }
            }
        },
        error:function(xhr,status,error)
        {
            console.log('error ====> '+error);
        }
    });
}

function create_pop_up(marker,map,data)
{
    kakao.maps.event.addListener(marker, 'click', function() {
        var content = '<div class="map-wrap">' +
            '    <div class="info">' +
            '            <div class="close" onclick="closeOverlay()" title="닫기"></div>' +
            '            <div class="factory-nm-grade">'+data.companyName+'</div>' +
            '            <div class="jibun ellipsis">'+data.companyAddr+'</div>' +
            '                <div class="company-dambo">' +
            '                   <div class="dambo">업체 연락처 : '+data.companyPhone+'</div> '+
            '                </div>'+
            '    </div>' +
            '</div>';
        _overlay = new kakao.maps.CustomOverlay({
            content: content,
            map: map,
            position: marker.getPosition()
        });
        _overlay.setMap(map);
    });
}

function closeOverlay() {
    _overlay.setMap(null);
}

function changeMarker(status)
{
    alert('작업중...!');
}