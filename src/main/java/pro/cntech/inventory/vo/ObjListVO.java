package pro.cntech.inventory.vo;

import lombok.Data;

@Data
public class ObjListVO extends PagingVO
{
    private String objSrl; //자산 고유 번호
    private String objStatus; //자산 상태
    private String statusAt; //상태 변경일
    private String userName; //담당자 혹은 관리자
    private String modelName; //자산 모델명
    private String srlName; //자산 시리얼명
    private String modelImageName; //모델 사진 경로
    private String srlImageName; //시리얼 사진 경로
    private String modelStatus; //자산 상태
    private String qrSrl; //QR 번호
    private String objKinds; //자산 종류
    private String userSrl; //회원 고유 번호
    private String companyName; //업체 이름
    private String objImage;
    private String auth;
    private String sortName;
    private String filterName;
    private String searchCondition; //검색 조건
    private String searchValue; //검색 명
}
