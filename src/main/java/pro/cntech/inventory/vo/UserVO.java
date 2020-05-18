package pro.cntech.inventory.vo;

import lombok.Data;

@Data
public class UserVO
{

    public UserVO ()
    {

    }

    public UserVO(String userSrl,String auth)
    {
     this.userSrl = userSrl;
     this.auth = auth;
    }

    private String userSrl;
    private String userName;
    private String auth;
    private String addr;
    private String phone;
    private String latitude;
    private String longitude;
    private String password;
    private int totalObjCnt;
    private String grade;
    private String masterSrl;
    private String companyName;
    private String companyPhone;
}
