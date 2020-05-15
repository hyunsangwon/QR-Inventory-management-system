package pro.cntech.inventory.mapper;

import org.springframework.stereotype.Repository;
import pro.cntech.inventory.vo.OrderVO;

@Repository
public interface OrderMapper
{
    void setOrder (OrderVO orderVO);
}
