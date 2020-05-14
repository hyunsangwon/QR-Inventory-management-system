package pro.cntech.inventory.vo;

import lombok.Data;

@Data
public class MarkerVO
{
    private String objSrl;
    private String latitude;
    private String longitude;
    private String companySrl;
    private String companyName;
    private String companyAddr;
    private String companyPhone;
    private String createAt;
    private String objStatus;
}
