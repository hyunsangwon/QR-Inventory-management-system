package pro.cntech.inventory.vo;

import lombok.Data;

@Data
public class OrderVO extends PagingVO
{
    private String orderSrl;
    private String userSrl;
    private String orderCnt; //주문 수량
    private String addr;
    private String detailAddr;
    private String userName;
    private String userPhone;
    private String orderRequests;
    private String orderStatus;
    private String orderDate;
}
