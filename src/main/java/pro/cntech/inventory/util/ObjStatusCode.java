package pro.cntech.inventory.util;

public class ObjStatusCode {

    public static final String INNER_REG_WAIT = "inner_wait"; //내부자산 등록
    public static final String INNER_REG_FAIL = "inner_fail"; //내부자산 승인 실패

    public static final String OUTER_REG_WAIT = "outer_wait"; //외부자산 등록
    public static final String OUTER_REG_FAIL = "outer_fail"; //외부자산 승인 실패

    public static final String RELEASE_START = "release_start"; //출고 시작
    public static final String RELEASE_FINISH  = "release_finish"; //출고 완료

    public static final String RETURN_START = "return_start" ; //반납 시작
    public static final String RETURN_WAIT = "return_wait" ; //반납 대기
    public static final String RETURN_FINISH = "return_finish" ; //반납 완료

}
