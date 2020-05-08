package pro.cntech.inventory.vo;

import lombok.Data;

@Data
public class UserVO
{
    private String userSrl;
    private String userName;
    private String auth;
    private String addr;
    private String phone;
    private String latitude;
    private String longitude;
    private String password;
}
