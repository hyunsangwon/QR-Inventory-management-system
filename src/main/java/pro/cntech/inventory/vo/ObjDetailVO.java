package pro.cntech.inventory.vo;

import lombok.Data;

@Data
public class ObjDetailVO extends PagingVO
{
    private String qrSrl;
    private String userSrl; // 회원 고유 번호
    private String objSrl; //자산 고유 번호
    private String objStatus; //자산 상태
    private String userName; //담당자 혹은 관리자
    private String modelName; //자산 모델명
    private String srlName;  //자산 시리얼 이름
    private String objImage; //자산 이미지
    private String objSrlImage; //자산 시리얼 이미지
    private String objModelImage; //자산 모델명 이미지
    private String latitude; // 위도
    private String longitude; // 경도
    private String addr; // gps 주소
    private String companySrl; //업체 고유번호
    private String inputAddr; //입력 주소
    private String companyAddr;
    private String companyDetailAddr; //업체 상세 주소
    private String companyName; //업체 이름
    private String companyPhone; //업체 연락처
    private String userPhone;
    private String createAt;
    private String auth;
    private String holderName;
    private String objKinds; //장비 종류
    private String companyLat;
    private String companyLon;
    private int radiusDistance; //반경 거리
}
