package pro.cntech.inventory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyInfoController
{
    private static final Logger logger = LoggerFactory.getLogger(MyInfoController.class);
    private static final String MASTER_VIEW_PREFIX = "cntech/";
    private static final String MANAGER_VIEW_PREFIX = "manager/";

    @GetMapping("/admin/myinfo")
    public String loadMyInfoPage()
    {
        return MANAGER_VIEW_PREFIX+"my_info";
    }

}
