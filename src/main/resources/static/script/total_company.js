function w3_open() {
    document.getElementById("mySidebar").style.display = "block";
    document.getElementById("myOverlay").style.display = "block";
}

function w3_close() {
    document.getElementById("mySidebar").style.display = "none";
    document.getElementById("myOverlay").style.display = "none";
}
/* select-box active*/
$(function(){
    var bBtn = $(".section-box");    //  function-bo를 sBtn으로 칭한다.
    bBtn.find("a").click(function(){ // sBtn에 속해 있는  a 찾아 클릭 하면.
        bBtn.removeClass("active");     // sBtn 속에 (active) 클래스를 삭제 한다.
        $(this).parent().addClass("active"); // 클릭한 a에 (active)클래스를 넣는다.
    });
    var bBtn = $(".section-box");
    bBtn.find("a").click(function(){
        bBtn.removeClass("")
    });

    $(".plus-btn").click(function() {
        $("#obj-info-plus").css({
            "display": "block"
        });
    });

    $(".close").click(function() {
        $("#obj-info-plus").css({
            "display": "none"
        });
    });

    $(".edit-btn").click(function() {
        $("#obj-info-edit").css({
            "display": "block"
        });
    });

    $(".close").click(function() {
        $("#obj-info-edit").css({
            "display": "none"
        });
    });
});