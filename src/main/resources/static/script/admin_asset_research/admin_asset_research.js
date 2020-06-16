var _userSrl = null;
var _userAuth = null;
var _markerCnt = 0;
var _markers = [];
/*
* userSrl : 유저 고유번호
* auth : 유저 권한
* */
function click_user(userSrl,auth) //왼쪽 NAV 클릭시 이벤트 함수
{
    _userSrl = userSrl;
    _userAuth = auth;
    setMarkers(); // 마커 초기화
    $("div[name=section-box]").removeClass('active');
    $('#'+userSrl).addClass('active');
    var jsonData = { "userSrl" : userSrl, "auth" : _userAuth};
    ajax_admin_info(jsonData);
    jsonData.pageNum = '1';
    jsonData.sortName = 'all';
    ajax_admin_asset_list(jsonData);
    ajax_page_add(jsonData);
    create_marker_map(_map);
}

function pageClick(obj) //페이지 번호 클릭시 이벤트 함수
{
    var pageNum = obj.id;
    var sortName = "all";
    if(_userSrl == null)
    {
        _userSrl = $('#userSrl').val();
    }
    if(_userAuth == null)
    {
        _userAuth = 'holder';
    }
    var jsonData = { "userSrl" : _userSrl , "pageNum" : pageNum, "auth" : _userAuth , "sortName" : sortName};
    ajax_admin_asset_list(jsonData);
    ajax_page_add(jsonData);
}

function load_detail_page(obj) //사진 클릭시 이벤트 함수
{
    location.href = "/obj/detail/search/"+obj.id;
}
/*자산 관리자 정보 가져오기*/
function ajax_admin_info(data)
{
    $.ajax
    ({
        type: 'POST',
        contentType: "application/json",
        url:'/ajax/admin/info',
        data : JSON.stringify(data),
        dataType : "json",
        async: false,
        beforeSend : function(xhr)
        {
            xhr.setRequestHeader(header, token);
        },
        success : function(data)
        {
            $('#userName').empty();
            $('#userInfo').empty();
            $('#userSrl').val(data.userSrl);
            $('#name').val(data.userName);
            $('#userPhone').val(data.phone);
            $('#totalObjCnt').val(data.totalObjCnt);
            var html = '';
            if(data.auth == 'holder')
            {
                html += '<b>[관리책임자] ' + data.userName + '</b>';
            }
            else
            {
                html += '<b>[현장기사] ' + data.userName + '</b>';
            }
            $('#userName').html(html);
            var userInfo = '<li>: <span style="font-weight: 600;">' +data.phone+ '</span><li>';
            userInfo += '<li>: <span >'+ data.totalObjCnt+ '개</span></li>';
            $('#userInfo').html(userInfo);
        },
        error : function(xhr, status, error)
        {
            console.log('error ====> ' + error);
        }
    });
}
/*자산 관리자 자산 리스트 호출*/
function ajax_admin_asset_list(data)
{
    $.ajax
    ({
        type: 'POST',
        contentType: "application/json",
        url:'/ajax/admin/asset/list',
        data : JSON.stringify(data),
        dataType : "json",
        async: false,
        beforeSend : function(xhr)
        {
            xhr.setRequestHeader(header, token);
        },
        success : function(data)
        {
            var html = '';
            if(data.length == 0)
            {
                    if(parseInt($('#totalObjCnt').val()) > 0)
                    {
                        //데이터 없음
                    }
                    else
                    {
                        $('#search').hide();
                        $('#assetMap').hide();
                        html += '<div class="default-remark">';
                        html += '등록된 자산이 없습니다.<p>아래 순서에 따라 자산을 등록하세요.';
                        html += '</div>';
                        html += '<div class="default-wrap">';
                        html += '<div class="default-box">';
                        html += '<div class="box"></div>';
                        html += "<span>등록 예정 자산에<p>QR 코드 스티커 부착</span>";
                        html += '</div>';
                        html += '<div class="default-box">';
                        html += '<div class="box"></div>';
                        html += "<span>'WEISER QR 자산관리'<p>APP 다운로드</span>";
                        html += '</div>';
                        html += '<div class="default-box">';
                        html += '<div class="box"></div>';
                        html += "<span>APP 실행 후<p>'내부 자산 등록' 선택</span>";
                        html += '</div>';
                        html += '</div>';
                    }
                $('#asset_container').html(html);
            }
            else
            {
                $('#search').show();
                $('#assetMap').show();
                for(var i=0; i < data.length; i++)
                {
                    if(data[i].auth == 'holder') //자산 소유자
                    {
                        if(data[i].objStatus == 'warehousing' || data[i].objStatus == 'warehousing_new') //출고 대기
                        {
                            html += '<div class="item-wait" id='+data[i].qrSrl+' onclick="load_detail_page(this)">';
                            html += '<div class="item">';
                            html += '<img src='+data[i].objImage+'>';
                            html += '</div>';
                            html += '<div class="item-text">';
                            html += '<div class="state">';
                            html += '<b>'+data[i].modelName+'</b>';
                            html += '</div>';
                            html += '<div class="date">'+data[i].companyName+'</div>';
                            html += '<div class="date"><span>'+data[i].statusAt+'</span> - 입고</div>';
                            html += '</div>';
                            html += '</div>';
                        }
                    }
                    if(data[i].auth == 'manager') //자산 관리자
                    {
                        if(data[i].objStatus == 'release' || data[i].objStatus == 'release_new') //출고 완료
                        {
                            html += '<div class="item-wait" id='+data[i].qrSrl+' onclick="load_detail_page(this)">';
                            html += '<div class="item">';
                            html += '<img src='+data[i].objImage+'>';
                            html += '</div>';
                            html += '<div class="item-text">';
                            html += '<div class="state">';
                            html += '<b>'+data[i].modelName+'</b>';
                            html += '</div>';
                            html += '<div class="date">'+data[i].companyName+'</div>';
                            html += '<div class="date"><span>'+data[i].statusAt+'</span> - 출고</div>';
                            html += '</div>';
                            html += '</div>';
                        }
                        if(data[i].objStatus == 'shipping') //출고 시작
                        {
                            html += '<div class="item-wait" id='+data[i].qrSrl+' onclick="load_detail_page(this)">';
                            html += '<div class="item">';
                            html += '<img src='+data[i].objImage+'>';
                            html += '</div>';
                            html += '<div class="item-text">';
                            html += '<div class="state">';
                            html += '<b>'+data[i].modelName+'</b>';
                            html += '</div>';
                            html += '<div class="date">'+data[i].companyName+'</div>';
                            html += '<div class="date"><span>'+data[i].statusAt+'</span> - 배송 중</div>';
                            html += '</div>';
                            html += '</div>';
                        }
                        if(data[i].objStatus == 'warehousing_wait')
                        {
                            html += '<div class="item-return-finish" id='+data[i].qrSrl+' onclick="load_detail_page(this)">';
                            html += '<div class="item">';
                            html += '<img src='+data[i].objImage+'>';
                            html += '</div>';
                            html += '<div class="item-text">';
                            html += '<div class="state">';
                            html += '<b>'+data[i].modelName+'</b>';
                            html += '</div>';
                            html += '<div class="date">'+data[i].companyName+'</div>';
                            html += '<div class="date"><span>'+data[i].statusAt+'</span> - 입고 대기</div>';
                            html += '</div>';
                            html += '</div>';
                        }
                    }
                }
                $('#asset_container').html(html);
            }
        },
        error : function(xhr, status, error)
        {
            console.log('error ====> ' + error);
        }
    });
}
function ajax_admin_add(data)
{
    $.ajax
    ({
        type: 'POST',
        contentType: "application/json",
        url:'/ajax/user/insert/info',
        data : JSON.stringify(data),
        beforeSend : function(xhr)
        {
            xhr.setRequestHeader(header, token);
        },
        dataType : "text",
        cache : false,
        success : function(data)
        {
            if(data == 'false')
            {
                alert('이미 가입된 현장 기사거나 정보가 잘못되었습니다.');
            }
            if(data =='true')
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
function ajax_admin_update(data)
{
    $.ajax
    ({
        type: 'POST',
        contentType: "application/json",
        url:'/ajax/user/update/info',
        data : JSON.stringify(data),
        beforeSend : function(xhr)
        {
            xhr.setRequestHeader(header, token);
        },
        dataType : "text",
        cache : false,
        success : function(data)
        {
            if(data == 'false')
            {
                alert('현장기사 정보수정 실패');
            }
            if(data =='true')
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
function ajax_page_add(data)
{
    $.ajax
    ({
        type: 'POST',
        contentType: "application/json",
        url:'/ajax/paging',
        data : JSON.stringify(data),
        beforeSend : function(xhr)
        {
            xhr.setRequestHeader(header, token);
        },
        dataType : "json",
        cache : false,
        success : function(data)
        {
            $('#paging_page').empty();
            var html = "";
            if(data.prev)
            {
                html += '<span>';
                    html += '<img src="/image/paging/list-previous.svg" onclick="pageClick(this);" id='+(data.startPage - 1 )+'/>';
                html += '</span>';
            }
            if(data.totalcount != 0)
            {
                for (var i = data.startPage; i <= data.endPage; i++)
                {
                    html += '<span class="page_num">';
                    if(data.pagenum == i)
                    {
                        html += '<span class="page_center" id="'+ i + '"onclick="pageClick(this);" style="cursor:pointer; font-weight:800;">' + i + '</span>';
                    }
                    else
                    {
                        html += '<span class="page_center" id="'+ i + '"onclick="pageClick(this);" style="cursor:pointer;">' + i + '</span>';
                    }
                    html += '</span>';
                }
            }
            if(data.next)
            {
                html += '<span>';
                    html += '<img src="/image/paging/list-next.svg" onclick="pageClick(this);" id='+(data.endPage + 1)+'/>';
                html += '</span>';
            }
            $('#paging_page').html(html);
        },
        error : function(xhr, status, error)
        {
            console.log('error ====> ' + error);
        }
    });
}

/* 지도 생성 */
function create_map()
{
    var mapContainer = document.getElementById('map'), // 지도를 표시할 div
        mapOption = {
            center: new kakao.maps.LatLng(36.2082709,127.9770043), // 지도의 중심좌표
            level: 12 // 지도의 확대 레벨
        };
    var map = new kakao.maps.Map(mapContainer, mapOption); // 지도를 생성합니다
    return map;
}

/* 마커 생성*/
function create_marker_map(map)
{
    if(_userSrl == null)
    {
        _userSrl = $('#userSrl').val();
    }
    if(_userAuth == null)
    {
        _userAuth = 'holder';
    }
    var jsonData = { "userSrl" : _userSrl , "auth" : _userAuth};
    $.ajax({
        type: 'POST',
        contentType: "application/json",
        url:'/ajax/call/user/asset/marker',
        data: JSON.stringify(jsonData),
        beforeSend : function(xhr)
        {
            xhr.setRequestHeader(header, token);
        },
        dataType: "json",
        cache :false,
        success: function(data)
        {
            var outerMarkerImageSrc = "https://qr-s3.s3.ap-northeast-2.amazonaws.com/private/main-icon-map-c.svg";
            var imageSize = new kakao.maps.Size(24, 35); //마커 사이즈 수정

            for(var i=0; i<data.length; i++)
            {
                    var markerImage = new kakao.maps.MarkerImage(outerMarkerImageSrc, imageSize);
                    var marker = new kakao.maps.Marker({
                        map: map,
                        position : new kakao.maps.LatLng(data[i].latitude,data[i].longitude),
                        title : data[i].companyName,
                        image : markerImage // 마커 이미지
                    });
                    create_pop_up(marker,map,data[i]);
                    _markers.push(marker);
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
        ++_markerCnt;
        if(_markerCnt > 1)
        {
            _overlay.setMap(null);
        }
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

function closeOverlay()
{
    _overlay.setMap(null);
}

function setMarkers()
{
    for (var i = 0; i < _markers.length; i++)
    {
        _markers[i].setMap(null);
    }
}