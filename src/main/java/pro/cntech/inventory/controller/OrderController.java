package pro.cntech.inventory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pro.cntech.inventory.vo.OrderVO;
import pro.cntech.inventory.vo.UserPrincipalVO;

@Controller
public class OrderController
{
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private static final String MASTER_VIEW_PREFIX = "cntech/";
    private static final String MANAGER_VIEW_PREFIX = "manager/";

    @GetMapping("/order/qr")
    public String loadOrderPage(@ModelAttribute("orderVO") OrderVO orderVO,
                                ModelMap model)
    {
        logger.debug("[ Call /order/qr - GET ]");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalVO userPrincipalVO = (UserPrincipalVO) auth.getPrincipal();

        model.addAttribute("phone",userPrincipalVO.getName());
        model.addAttribute("name",userPrincipalVO.getUsername());
        model.addAttribute("addr",userPrincipalVO.getAddr());
        model.addAttribute("orderVO",orderVO);

        return MANAGER_VIEW_PREFIX+"order";
    }

    @PostMapping("/order/qr")
    public String qrOrder(@ModelAttribute("orderVO") OrderVO orderVO,
                          BindingResult bindingResult,
                          ModelMap model)
    {
        logger.debug("[ Call /order/qr - POST ]");
        logger.debug("Param : "+orderVO.toString());
        if (bindingResult.hasErrors())
        {

        }
        return MANAGER_VIEW_PREFIX+"order";
    }

}
