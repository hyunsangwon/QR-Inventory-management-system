package pro.cntech.inventory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pro.cntech.inventory.service.OrderService;
import pro.cntech.inventory.vo.OrderVO;
import pro.cntech.inventory.vo.UserPrincipalVO;

@Controller
public class OrderController
{
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private static final String MASTER_VIEW_PREFIX = "cntech/";
    private static final String MANAGER_VIEW_PREFIX = "manager/";
    @Autowired
    OrderService orderService;

    @GetMapping("/order/qr")
    public String loadOrderPage(@ModelAttribute("orderVO") OrderVO orderVO,
                                ModelMap model)
    {
        logger.debug("[ Call /order/qr - GET ]");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalVO userPrincipalVO = (UserPrincipalVO) auth.getPrincipal();

        model.addAttribute("cnt",orderService.myOrderCnt(userPrincipalVO.getUserSrl()));
        model.addAttribute("phone",userPrincipalVO.getName());
        model.addAttribute("name",userPrincipalVO.getUsername());
        model.addAttribute("addr",userPrincipalVO.getAddr());
        model.addAttribute("detailAddr",userPrincipalVO.getDetailAddr());
        model.addAttribute("orderVO",orderVO);

        return MANAGER_VIEW_PREFIX+"order";
    }

    /*최종 주문 완료*/
    @PostMapping("/order/qr")
    public String qrOrder(@ModelAttribute("orderVO") OrderVO orderVO, ModelMap model)
    {
        logger.debug("[ Call /order/qr - POST ]");
        logger.debug("Param : "+orderVO.toString());
        orderService.setOrder(orderVO);

        model.addAttribute("vo",orderVO);
        return MANAGER_VIEW_PREFIX+"order_confirm";
    }

    @GetMapping("/order/list/{pageNum}")
    public String loadOrderListPage(ModelMap model, @PathVariable("pageNum") int pageNum)
    {
        logger.debug("[ Call /order/list - GET ]");

        orderService.getOrderList(model,pageNum);
        return MANAGER_VIEW_PREFIX+"order_list";
    }

}
