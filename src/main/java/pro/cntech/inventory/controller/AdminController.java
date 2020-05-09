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
import pro.cntech.inventory.service.AdminService;
import pro.cntech.inventory.vo.ObjListVO;
import pro.cntech.inventory.vo.UserVO;

import java.util.List;

/*자산 관리자 컨트롤 */
@Controller
public class AdminController
{
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private static final String MASTER_VIEW_PREFIX = "cntech/";
    private static final String MANAGER_VIEW_PREFIX = "manager/";
    @Autowired
    private AdminService adminService;

    @GetMapping("/admin/asset/research")
    public String loadAdminListPage(ModelMap model)
    {
        logger.debug("[ Call /admin/asset/research - GET ]");
        adminService.setMyInfo(model);
        return MANAGER_VIEW_PREFIX+"admin_asset_research";
    }

    @PostMapping("/ajax/admin/info")
    public @ResponseBody UserVO ajaxCallAdminInfo(@RequestBody UserVO userVO)
    {
        logger.debug("[ Call /ajax/admin/info - GET ]");
        logger.debug("Param : "+userVO.toString());

        return adminService.getAdminInfo(userVO);
    }

    @PostMapping("/ajax/admin/asset/list")
    public @ResponseBody List<ObjListVO> ajaxCallAdminAssetInfo(@RequestBody ObjListVO listVO)
    {
        logger.debug("[ Call /ajax/admin/asset/list - GET ]");
        logger.debug("Param : "+listVO.toString());
        return adminService.getAdminAssetList(listVO);
    }

}
