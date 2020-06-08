package pro.cntech.inventory.util;

public class ObjStatusCode {

    /*신버전*/
    public static final String NEW_RELEASE = "release_new"; // 출고 첫 등록
    public static final String NEW_WAREHOUSING = "warehousing_new"; //입고 첫 등록
    public static final String RELEASE = "release"; // 출고
    public static final String SHIPPING = "shipping"; // 배송중
    public static final String WAREHOUSING = "warehousing"; //입고
    public static final String WAREHOUSING_WAIT = "warehousing_wait"; //입고 대기
    public static final String ASSET_DELETE = "asset_delete"; //자산 삭제
}
