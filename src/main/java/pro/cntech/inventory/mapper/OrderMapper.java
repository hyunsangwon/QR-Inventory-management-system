package pro.cntech.inventory.mapper;

import org.springframework.stereotype.Repository;
import pro.cntech.inventory.vo.OrderVO;

import java.util.List;

@Repository
public interface OrderMapper
{
    void setOrder (OrderVO orderVO);
    List<OrderVO> getOrderList(OrderVO orderVO);
    int getOrderListCnt(OrderVO orderVO);
}
