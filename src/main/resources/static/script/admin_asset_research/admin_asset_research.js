var _userSrl = null;
var _userAuth = null;

/*
* userSrl : 유저 고유번호
* auth : 유저 권한
* */
function click_user(userSrl,auth) //왼쪽 NAV 클릭시 이벤트 함수
{
    _userSrl = userSrl;
    _userAuth = auth;

    if(_userAuth == 'holder')
    {
        $('#managerOption').hide();
        $('#holderOption').show();
    }
    if(_userAuth == 'manager')
    {
        $('#holderOption').hide();
        $('#managerOption').show();
    }
    $("div[name=section-box]").removeClass('active');
    $('#'+userSrl).addClass('active');
    var jsonData = { "userSrl" : userSrl, "auth" : _userAuth};
    ajax_admin_info(jsonData);
    jsonData.pageNum = '1';
    jsonData.sortName = 'all';
    ajax_admin_asset_list(jsonData);
    ajax_page_add(jsonData);
}

function pageClick(obj) //페이지 번호 클릭시 이벤트 함수
{
    var pageNum = obj.id;
    var sortName = "all";
    if(_userAuth == 'holder')
    {
        sortName = $('#holderOption option:selected').val();
    }
    if(_userAuth == 'manager')
    {
        sortName = $('#managerOption option:selected').val();
    }
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
        beforeSend : function(xhr)
        {
            xhr.setRequestHeader(header, token);
        },
        success : function(data)
        {
            $('#userName').empty();
            $('#userInfo').empty();
            $('#userSrl').val(data.userSrl);
            var html = '';
            if(data.auth == 'holder')
            {
                html += '<b>[관리 책임자] ' + data.userName + '</b>';
            }
            else
            {
                html += '<b>[현장 기사] ' + data.userName + '</b>';
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
        beforeSend : function(xhr)
        {
            xhr.setRequestHeader(header, token);
        },
        success : function(data)
        {
            //$('#asset_container').empty();
            var html = '';
            if(data.length == 0)
            {
                $('#holderOption').hide(); //select box
                $('#managerOption').hide();

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
                $('#asset_container').html(html);
            }
            else
            {
                for(var i=0; i < data.length; i++)
                {
                    if(data[i].auth == 'holder') //자산 소유자 (반납 완료, 내부 등록)
                    {
                        if(data[i].objStatus == 'return_finish' || data[i].objStatus == 'inner_wait') //출고 대기
                        {
                            html += '<div class="item-wait" id='+data[i].qrSrl+' onclick="load_detail_page(this)">';
                            html += '<div class="item">';
                            html += '<img src='+data[i].objImage+'>';
                            html += '</div>';
                            html += '<div class="item-text">';
                            html += '<div class="state">';
                            html += '<b>출고 대기</b>';
                            html += '</div>';
                            html += '<div class="date">'+data[i].statusAt+'</div>';
                            html += '</div>';
                            html += '</div>';
                        }
                    }
                    if(data[i].auth == 'manager') //자산 관리자 (출고 완료, 출고 시작, 반납 시작, 반납 완료, 반납 대기, 외부자산 등록)
                    {
                        if(data[i].objStatus == 'release_finish' || data[i].objStatus == 'outer_wait') //출고 완료
                        {
                            html += '<div class="item-release-ok" id='+data[i].qrSrl+' onclick="load_detail_page(this)">';
                            html += '<div class="item">';
                            html += '<img src='+data[i].objImage+'>';
                            html += '</div>';
                            html += '<div class="item-text">';
                            html += '<div class="state">';
                            html += '<b>출고 완료</b>';
                            html += '</div>';
                            html += '<div class="date">'+data[i].statusAt+'</div>';
                            html += '</div>';
                            html += '</div>';
                        }
                        if(data[i].objStatus == 'release_start') //출고 시작
                        {
                            html += '<div class="item-release-ok" id='+data[i].qrSrl+' onclick="load_detail_page(this)">';
                            html += '<div class="item">';
                            html += '<img src='+data[i].objImage+'>';
                            html += '</div>';
                            html += '<div class="item-text">';
                            html += '<div class="state">';
                            html += '<b>출고 시작</b>';
                            html += '</div>';
                            html += '<div class="date">'+data[i].statusAt+'</div>';
                            html += '</div>';
                            html += '</div>';
                        }
                        if(data[i].objStatus == 'return_start')
                        {
                            html += '<div class="item-return-start" id='+data[i].qrSrl+' onclick="load_detail_page(this)">';
                            html += '<div class="item">';
                            html += '<img src='+data[i].objImage+'>';
                            html += '</div>';
                            html += '<div class="item-text">';
                            html += '<div class="state">';
                            html += '<b>반납 시작</b>';
                            html += '</div>';
                            html += '<div class="item-id">'+data[i].statusAt+'</div>';
                            html += '</div>';
                            html += '</div>';
                        }
                        if(data[i].objStatus == 'return_wait')
                        {
                            html += '<div class="item-return-finish" id='+data[i].qrSrl+' onclick="load_detail_page(this)">';
                            html += '<div class="item">';
                            html += '<img src='+data[i].objImage+'>';
                            html += '</div>';
                            html += '<div class="item-text">';
                            html += '<div class="state">';
                            html += '<b>반납 대기중</b>';
                            html += '</div>';
                            html += '<div class="item-id">'+data[i].statusAt+'</div>';
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
            if(data)
            {
                alert('입력 완료');
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
            if(data)
            {
                alert('수정 완료');
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