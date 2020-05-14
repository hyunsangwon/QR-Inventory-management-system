package pro.cntech.inventory.vo;

import lombok.Data;

@Data
public class LogVO
{
    private String logSrl;
    private String objSrl;
    private String qrSrl;
    private String objStatus;
    private String latitude;
    private String longitude;
    private String objAddr;
    private String createAt;
    private String holderName;
}