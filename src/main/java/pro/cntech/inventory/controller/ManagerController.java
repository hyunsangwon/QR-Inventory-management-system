package pro.cntech.inventory.controller;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import pro.cntech.inventory.service.AdminService;
import pro.cntech.inventory.util.PageHandler;
import pro.cntech.inventory.vo.ObjListVO;
import pro.cntech.inventory.vo.UserVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/*자산 관리자 컨트롤 */
@Controller
public class ManagerController
{
    private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);
    private static final String MASTER_VIEW_PREFIX = "cntech/";
    private static final String MANAGER_VIEW_PREFIX = "manager/";
    @Autowired
    private AdminService adminService;


    @GetMapping("/asset/search/list")
    public String loadAdminListPage(ModelMap model)
    {
        logger.debug("[ Call /asset/search/list - GET ]");
        adminService.setMyInfo(model,"none");
        return MANAGER_VIEW_PREFIX+"admin_asset_research";
    }

    @GetMapping("/asset/search/{searchName}/{searchValue}")
    public String loadSearchAdminListPage(ModelMap model,@PathVariable("searchName") String searchName,
                                    @PathVariable("searchValue") String searchValue)
    {
        logger.debug("[ Call /asset/search/"+searchName+"/"+searchValue+" - GET ]");
        adminService.setMyInfo(model,searchName);
        return MANAGER_VIEW_PREFIX+"admin_asset_research";
    }

    @PostMapping("/ajax/admin/info")
    public @ResponseBody UserVO ajaxCallAdminInfo(@RequestBody UserVO userVO)
    {
        logger.debug("[ Call /ajax/admin/info - POST ]");
        logger.debug(" /ajax/admin/info Param : "+userVO.toString());

        return adminService.getAdminInfo(userVO);
    }

    @PostMapping("/ajax/admin/asset/list")
    public @ResponseBody List<ObjListVO> ajaxCallAdminAssetInfo(@RequestBody ObjListVO listVO)
    {
        logger.debug("[ Call /ajax/admin/asset/list - POST ]");
        logger.debug("Param : "+listVO.toString());
        return adminService.getAdminAssetList(listVO);
    }

    @PostMapping("/ajax/paging")
    public @ResponseBody PageHandler callAjaxPageHandler(@RequestBody ObjListVO listVO)
    {
        logger.debug("[ Call /ajax/admin/paging - POST ]");
        logger.debug("Param : "+listVO.toString());
        return adminService.createPageHandler(listVO);
    }

    @PostMapping("/ajax/user/insert/info")
    public @ResponseBody Boolean callAjaxUserInsert(@RequestBody UserVO userVO)
    {
        logger.debug("[ Call /ajax/user/insert/info - POST ]");
        logger.debug("Param : "+userVO.toString());
        return adminService.isControlUser(userVO,"insert");
    }

    @PostMapping("/ajax/user/update/info")
    public @ResponseBody Boolean callAjaxUserUpdate(@RequestBody UserVO userVO)
    {
        logger.debug("[ Call /ajax/user/update/info - POST ]");
        logger.debug("Param : "+userVO.toString());
        return adminService.isControlUser(userVO,"update");
    }

    /*전체 자산 조회*/
    @GetMapping("/asset/list/sort/{nameSort}/type/{typeSort}/pageNum/{pageNum}")
    public String loadAssetAllList(ModelMap map,
                                   @PathVariable("nameSort") String nameSort,
                                   @PathVariable("typeSort") String typeSort,
                                   @PathVariable("pageNum") int pageNum) throws Exception
    {
        logger.debug("[ Call /asset/all/list/sort/"+nameSort+" - GET ]");
        logger.debug("Param : "+nameSort);
        logger.debug("Param : "+typeSort);
        logger.debug("Param : "+pageNum);
        String filterName = typeSort; String sortName = nameSort;
        if(filterName != null && sortName != null)
        {
            adminService.getAssetAllList(pageNum,filterName,sortName,map);
        }
        return MANAGER_VIEW_PREFIX+"all_obj_list";
    }

    /*전체 자산 조회 EXCEP download*/
    @GetMapping("/asset/list/excel/download")
    public void downloadExcelFile(HttpServletResponse response) throws Exception
    {
        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode("자산_리스트","UTF-8")+".xls");


        Workbook workBook = adminService.makeExcelForm();

        workBook.write(response.getOutputStream());
        workBook.close();

        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

}
