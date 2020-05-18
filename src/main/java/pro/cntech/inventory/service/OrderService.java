package pro.cntech.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pro.cntech.inventory.mapper.OrderMapper;
import pro.cntech.inventory.vo.OrderVO;

@Service
public class OrderService
{
    @Autowired
    private OrderMapper orderMapper;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void setOrder(OrderVO orderVO)
    {
        String addr = orderVO.getAddr();
        String detailAddr = orderVO.getDetailAddr();
        orderVO.setAddr(addr+", "+detailAddr);
        orderMapper.setOrder(orderVO);
    }

}
