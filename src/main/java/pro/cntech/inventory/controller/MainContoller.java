package pro.cntech.inventory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.cntech.inventory.service.MainService;
import pro.cntech.inventory.vo.CompanyVO;
import pro.cntech.inventory.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MainContoller
{
    private static final Logger logger = LoggerFactory.getLogger(MainContoller.class);
    private static final String MASTER_VIEW_PREFIX = "cntech/";
    private static final String MANAGER_VIEW_PREFIX = "manager/";

    @Autowired
    private MainService mainService;

    @GetMapping("/login")
    public String loadLoginxPage(HttpServletRequest request)
    {
        logger.debug("[ Call /login - GET ]");
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) ip = request.getRemoteAddr();
        logger.debug("[Client IP] : "+ ip);

        return "login";
    }

    @GetMapping("/login-fail")
    public String loadLoginPagefail(ModelMap model, HttpServletRequest request)
    {
        logger.debug("[ Call /login-fail - GET ]");
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) ip = request.getRemoteAddr();
        logger.debug("/Client IP : "+ip+"/");
        model.addAttribute("error",true);
        return "login";
    }

    @GetMapping("/")
    public String redirectLoginPage()
    {
        logger.debug("[ Call / - GET ]");
        return "redirect:/login";
    }

    @GetMapping("/main")
    public String loadMainPage(ModelMap model)
    {
        logger.debug("[ Call /main - GET ]");
        model.addAttribute("statistics",mainService.getMainStatistics());
        return MANAGER_VIEW_PREFIX+"main";
    }

    @PostMapping("/ajax/call/kakaomap/marker")
    public @ResponseBody CompanyVO ajaxCallMapMarker(@RequestBody UserVO userVO)
    {
        logger.debug("[ Call /ajax/call/kakaomap/marker- GET ]");
        logger.debug("Param : "+userVO.toString());
        return mainService.getCompanyGPS(userVO);
    }

    @GetMapping("/map")
    public String loadMapPage()
    {
        logger.debug("[ Call /map - GET ]");
        return "map";
    }
}
