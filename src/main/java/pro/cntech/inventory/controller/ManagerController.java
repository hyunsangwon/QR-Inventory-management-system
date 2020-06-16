package pro.cntech.inventory.controller;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import pro.cntech.inventory.service.AdminService;
import pro.cntech.inventory.util.PageHandler;
import pro.cntech.inventory.vo.MarkerVO;
import pro.cntech.inventory.vo.ObjListVO;
import pro.cntech.inventory.vo.UserPrincipalVO;
import pro.cntech.inventory.vo.UserVO;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    /*관리자 자산 조회*/
    @GetMapping("/asset/search/list/{nameSort}")
    public String loadAdminListPage(ModelMap model, @PathVariable("nameSort") String nameSort) throws Exception
    {
        logger.debug("[ Call /asset/search/list - GET ]");
        adminService.setMyInfo(model,nameSort);
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
    public @ResponseBody List<ObjListVO> ajaxCallAdminAssetInfo(@RequestBody ObjListVO listVO) throws Exception
    {
        logger.debug("[ Call /ajax/admin/asset/list - POST ]");
        logger.debug("Param : "+listVO.toString());
        return adminService.getAdminAssetList(listVO);
    }

    @PostMapping("/ajax/paging")
    public @ResponseBody PageHandler callAjaxPageHandler(@RequestBody ObjListVO listVO)
    {
        logger.debug("[ Call /ajax/paging - POST ]");
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
        logger.debug("[ Call /asset/list/sort/"+nameSort+"/type/"+typeSort+"/pageNum/"+pageNum+" - GET ]");
        logger.debug("Param : "+nameSort);
        logger.debug("Param : "+typeSort);
        logger.debug("Param : "+pageNum);
        String filterName = typeSort; String sortName = nameSort;
        if(filterName != null && sortName != null)
        {
            adminService.getAssetAllList(pageNum,filterName,sortName,"null",map);
        }
        return MANAGER_VIEW_PREFIX+"all_obj_list";
    }

    @GetMapping("/asset/list/search/{searchValue}/sort/{nameSort}/type/{typeSort}/pageNum/{pageNum}")
    public String loadSearchAssetList(ModelMap map,
                                      @PathVariable("searchValue") String searchValue,
                                      @PathVariable("nameSort") String nameSort,
                                      @PathVariable("typeSort") String typeSort,
                                      @PathVariable("pageNum") int pageNum)throws Exception
    {
        logger.debug("[ Call /asset/list/search/"+searchValue+"/sort/"+nameSort+"/type/"+typeSort+"/pageNum/"+pageNum+" - GET ]");
        logger.debug("Param : "+nameSort);
        logger.debug("Param : "+typeSort);
        logger.debug("Param : "+pageNum);
        logger.debug("Param : "+searchValue);
        if(searchValue != null || nameSort != null || typeSort != null)
        {
            adminService.getAssetAllList(pageNum,typeSort,nameSort,searchValue,map);
        }
        return MANAGER_VIEW_PREFIX+"all_obj_list";
    }


    /*전체 자산 조회 EXCEP download*/
    @PostMapping("/asset/list/excel/download")
    public void downloadExcelFile(HttpServletResponse response,
                                  @ModelAttribute("objListVO") ObjListVO objListVO) throws Exception
    {
        logger.debug("[ Call /asset/list/excel/download - POST ]");
        logger.debug("Param : "+objListVO.toString());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalVO userPrincipalVO = (UserPrincipalVO) auth.getPrincipal();
        String companyName = userPrincipalVO.getCompanyName();
        String today = new SimpleDateFormat( "yyMMdd").format(new Date());
        String title = "자산리스트";

        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(today+"_"+companyName+"_"+title,"UTF-8")+".xls");
        Workbook workBook = adminService.makeExcelForm(objListVO);

        workBook.write(response.getOutputStream());
        workBook.close();

        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    @PostMapping("/ajax/call/user/asset/marker")
    public @ResponseBody List<MarkerVO> ajaxCallMapMarker(@RequestBody UserVO userVO)
    {
        logger.debug("[ Call /ajax/call/kakaomap/marker- GET ]");
        logger.debug("Param : "+userVO.toString());
        return adminService.getCompanyGPS(userVO);
    }

}
