package pro.cntech.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import pro.cntech.inventory.mapper.OrderMapper;
import pro.cntech.inventory.util.PageHandler;
import pro.cntech.inventory.vo.OrderVO;
import pro.cntech.inventory.vo.UserPrincipalVO;

import java.util.List;

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

    public int myOrderCnt(String userSrl)
    {
        int MAX = 120;
        return (MAX - orderMapper.myOrderCnt(userSrl));
    }

    public void getOrderList(ModelMap map,int pageNum)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalVO userPrincipalVO = (UserPrincipalVO) auth.getPrincipal();
        String userSrl = userPrincipalVO.getUserSrl();

        int MAX = 10;
        int limitCount=((pageNum - 1 ) * MAX);
        int contentNum = MAX;
        int totalCnt = 0;

        OrderVO orderVO = new OrderVO();
        orderVO.setLimitcount(limitCount); orderVO.setContentnum(contentNum); orderVO.setUserSrl(userSrl);
        List<OrderVO> list = orderMapper.getOrderList(orderVO);
        totalCnt = orderMapper.getOrderListCnt(orderVO);
        PageHandler pageHandler = pageHandler(totalCnt,pageNum,contentNum);

        map.addAttribute("pageNum",pageNum);
        map.addAttribute("list",list);
        map.addAttribute("size",list.size());
        map.addAttribute("pageHandler",totalCnt);
    }

    private PageHandler pageHandler(int totalCount, int pageNum, int contentNum)
    {

        PageHandler pageHandler = new PageHandler();
        pageHandler.setTotalcount(totalCount);
        pageHandler.setPagenum(pageNum);
        pageHandler.setContentnum(contentNum);
        pageHandler.setCurrentblock(pageNum);
        pageHandler.setLastblock(pageHandler.getTotalcount());
        pageHandler.prevnext(pageNum);
        pageHandler.setStartPage(pageHandler.getCurrentblock());
        pageHandler.setEndPage(pageHandler.getLastblock(),pageHandler.getCurrentblock());

        return pageHandler;
    }

}
