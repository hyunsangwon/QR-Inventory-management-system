package pro.cntech.inventory.controller;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.cntech.inventory.service.MainService;
import pro.cntech.inventory.vo.UserPrincipalVO;
import pro.cntech.inventory.vo.UserVO;

@Controller
public class MyInfoController
{
    private static final Logger logger = LoggerFactory.getLogger(MyInfoController.class);
    private static final String MASTER_VIEW_PREFIX = "cntech/";
    private static final String MANAGER_VIEW_PREFIX = "manager/";
    @Autowired
    private MainService mainService;

    @GetMapping("/holder/myinfo")
    public String loadMyInfoPage(ModelMap model)
    {
        logger.debug("[ Call /manager/myinfo - GET ]");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalVO userPrincipalVO = (UserPrincipalVO) auth.getPrincipal();
        String phone = userPrincipalVO.getUsername();
        model.addAttribute("phone",phone);

        return MANAGER_VIEW_PREFIX+"my_info";
    }

    @PostMapping("/ajax/holder/myinfo")
    public @ResponseBody Boolean updateMyInfo(@RequestBody UserVO userVO)
    {
        logger.debug("[ Call /ajax/holder/myinfo - POST ]");
        logger.debug("Param : "+userVO.toString());
        if(userVO == null) return false;

        return mainService.isUserinfoUpdate(userVO);
    }

    @PostMapping("/ajax/manager/password/compare")
    public @ResponseBody Boolean callAjaxPasswordCompare(@RequestBody UserVO userVO)
    {
        logger.debug("[ Call /manager/password/compare - GET ]");
        logger.debug("Param : "+userVO.toString());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalVO userPrincipalVO = (UserPrincipalVO) auth.getPrincipal();
        return BCrypt.checkpw(userVO.getPassword(),userPrincipalVO.getPassword());
    }

    @GetMapping("/holder/myinfo/detail")
    public String loadMyInfoDetailPage(ModelMap model)
    {
        logger.debug("[ Call /manager/myinfo/detail - GET ]");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalVO userPrincipalVO = (UserPrincipalVO) auth.getPrincipal();
        model.addAttribute("userSrl",userPrincipalVO.getUserSrl());
        model.addAttribute("name",userPrincipalVO.getName());
        model.addAttribute("phone",userPrincipalVO.getUsername());
        model.addAttribute("companyName",userPrincipalVO.getCompanyName());
        model.addAttribute("companyPhone",userPrincipalVO.getCompanyPhone());
        model.addAttribute("companyAddr",userPrincipalVO.getAddr());
        model.addAttribute("companyDetailAddr",userPrincipalVO.getDetailAddr());
        return MANAGER_VIEW_PREFIX+"my_info_edit";
    }

}
