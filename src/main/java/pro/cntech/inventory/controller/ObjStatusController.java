package pro.cntech.inventory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import pro.cntech.inventory.service.StatusService;
import pro.cntech.inventory.util.ObjStatusCode;
import pro.cntech.inventory.util.PageHandler;
import pro.cntech.inventory.vo.ObjArrayVO;
import pro.cntech.inventory.vo.ObjDetailVO;
import pro.cntech.inventory.vo.ObjListVO;

import java.util.List;

@Controller
public class ObjStatusController
{
    private static final Logger logger = LoggerFactory.getLogger(ObjStatusController.class);
    private static final String MASTER_VIEW_PREFIX = "cntech/";
    private static final String MANAGER_VIEW_PREFIX = "manager/";
    @Autowired
    private StatusService statusService;

    /*자산 리스트*/
    @GetMapping("/obj/status/{status}/list/{pageNum}")
    public String loadObjStatusList(ModelMap model
                                ,@PathVariable("status") String status
                                ,@PathVariable("pageNum") int pageNum)
    {
        logger.debug("[ Call /obj/status/"+status+"/list/"+pageNum+" - GET ]");
        logger.debug("Param : "+status);
        ObjListVO obj = new ObjListVO();
        obj.setPageNum(pageNum);
        obj.setObjStatus(status);
        statusService.getObjStatusList(model,obj);

        if(ObjStatusCode.RETURN_WAIT.equals(status) || ObjStatusCode.RETURN_FINISH.equals(status))
        {
            return MANAGER_VIEW_PREFIX+"internal_obj_list";
        }
        return MANAGER_VIEW_PREFIX+"external_obj_list";
    }

    /*자산 상세보기*/
    @GetMapping("/obj/detail/search/{qrSrl}")
    public String loadObjDetailPage(ModelMap model
            ,@PathVariable("qrSrl") String qrSrl)
    {
        logger.debug("[ Call /obj/search/"+qrSrl+ "- GET ]");
        logger.debug("Param : "+qrSrl);

        model.addAttribute("vo",statusService.getObjDetail(qrSrl));
        return MANAGER_VIEW_PREFIX+"detail_obj";
    }
    /* 자산 히스토리 리스트 불러오기 */
    @PostMapping("/ajax/obj/history/list")
    public @ResponseBody List<ObjDetailVO> callAjaxObjHistoryList(@RequestBody ObjDetailVO objDetailVO)
    {
        logger.debug("[ Call /ajax/obj/history/list - POST ]");
        logger.debug("Param : "+objDetailVO.toString());

        return statusService.getObjHistoryList(objDetailVO);
    }
    /*히스토리 paing 처리*/
    @PostMapping("/ajax/obj/history/page")
    public @ResponseBody PageHandler callAjaxPageHandler(@RequestBody ObjDetailVO objDetailVO)
    {
        logger.debug("[ Call /ajax/obj/history/list - POST ]");
        logger.debug("Param : "+objDetailVO.toString());

        return statusService.getpageHandler(objDetailVO);
    }
    /*출고대기 자산 삭제하기*/
    @PostMapping("/ajax/obj/release_wait/delete")
    public @ResponseBody  Boolean callAjaxObjDelete(@RequestBody ObjArrayVO objArrayVO) throws Exception
    {
        logger.debug("[ Call /ajax/obj/release_wait/delete - POST ]");
        logger.debug("Param : "+objArrayVO.toString());

        return statusService.controlObjStatus("delete",objArrayVO);
    }

    @PostMapping("/ajax/obj/return_finish/confirm")
    public @ResponseBody Boolean callAjaxObjConfirm(@RequestBody ObjArrayVO objArrayVO) throws Exception
    {
        logger.debug("[ Call /ajax/obj/return_finish/confirm - POST ]");
        logger.debug("Param : "+objArrayVO.toString());
        return statusService.controlObjStatus("update",objArrayVO);
    }

    @PostMapping("/ajax/obj/delete")
    public @ResponseBody Boolean callAjaxObjDelete(@RequestBody ObjDetailVO objDetailVO)
    {
        logger.debug("[ Call /ajax/obj/delete - POST ]");
        logger.debug("Param : "+objDetailVO.toString());
        return statusService.isDeleteObj(objDetailVO);
    }

    @PostMapping("/ajax/obj/update")
    public @ResponseBody Boolean callAjaxObjUpdate(@RequestBody ObjDetailVO objDetailVO)
    {
        logger.debug("[ Call /ajax/obj/update - POST ]");
        logger.debug("Param : "+objDetailVO.toString());
        return statusService.isUpdateObjInfo(objDetailVO);
    }
}