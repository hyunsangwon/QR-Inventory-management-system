package pro.cntech.inventory.vo;

import lombok.Data;

@Data
public class CompanyVO
{
    private String latitude;
    private String longitude;
    private String companySrl;
    private String companyName;
    private String companyAddr;
    private String companyPhone;
    private String createAt;
}
