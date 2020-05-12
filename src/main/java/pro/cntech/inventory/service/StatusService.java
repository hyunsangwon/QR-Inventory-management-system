package pro.cntech.inventory.service;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import pro.cntech.inventory.mapper.ObjStatusMapper;
import pro.cntech.inventory.util.ObjStatusCode;
import pro.cntech.inventory.util.PageHandler;
import pro.cntech.inventory.vo.ObjArrayVO;
import pro.cntech.inventory.vo.ObjDetailVO;
import pro.cntech.inventory.vo.ObjListVO;
import pro.cntech.inventory.vo.UserPrincipalVO;

import java.util.List;

@Service
public class StatusService
{

    @Autowired
    private ObjStatusMapper objStatusMapper;

    public void getObjStatusList(ModelMap map, ObjListVO vo)
    {
        int MAX = 15;
        int limitCount=((vo.getPageNum() - 1 ) * MAX);
        int contentNum = MAX;
        int totalCnt = 0;
        UserPrincipalVO userInfo = getSecurityInfo();
        vo.setUserSrl(userInfo.getUserSrl()); vo.setLimitcount(limitCount); vo.setContentnum(contentNum);
        vo.setAuth(userInfo.getAuth());
        totalCnt = objStatusMapper.getObjStatusListTotalCnt(vo);
        PageHandler pageHandler = pageHandler(totalCnt,vo.getPageNum(),contentNum);
        List<ObjListVO> list = objStatusMapper.getObjStatusList(vo);

        map.addAttribute("objStatus",vo.getObjStatus());
        map.addAttribute("list",list);
        map.addAttribute("size",list.size());
        map.addAttribute("pageHandler",pageHandler);
    }

    public ObjDetailVO getObjDetail(String qrSrl)
    {

        ObjDetailVO detailvo = objStatusMapper.getObjDetail(qrSrl);
        UserPrincipalVO userInfo = getSecurityInfo();
        if(detailvo.getSrlName() == null)
        {
            detailvo.setSrlName("데이터 없음");
        }
        if(detailvo.getModelName() == null)
        {
            detailvo.setModelName("데이터 없음");
        }
        if(userInfo.getAuth().equals("manager"))
        {
            detailvo.setAuth("자산관리자");
        }
        return detailvo;
    }

    public List<ObjDetailVO> getObjHistoryList(ObjDetailVO vo)
    {
        int MAX = 15;
        int limitCount=((vo.getPageNum() - 1 ) * MAX);
        int contentNum = MAX;
        vo.setLimitcount(limitCount); vo.setContentnum(contentNum);
        List<ObjDetailVO> list = objStatusMapper.getObjHistory(vo);
        return list;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Boolean controlObjStatus(String flag, ObjArrayVO objArrayVO) throws Exception
    {
        int rows = 0;
        if("delete".equals(flag))
        {
            for(ObjListVO listVO: objArrayVO.getParam())
            {
                String qrSrl = listVO.getQrSrl();
                rows = objStatusMapper.deleteObj(qrSrl);
            }
        }
        if("update".equals(flag))
        {
            for(ObjListVO listVO: objArrayVO.getParam())
            {
                listVO.setObjStatus(ObjStatusCode.RELEASE_WAIT); //출고 대기
                rows = objStatusMapper.updateObjStatus(listVO);
            }
        }
        if(rows == 0)
        {
            return false;
        }
        return true;
    }

    public PageHandler getpageHandler(ObjDetailVO vo)
    {
        int contentNum = 5;
        int totalCnt = objStatusMapper.getObjHistoryCnt(vo);
        PageHandler pageHandler = pageHandler(totalCnt,vo.getPageNum(),contentNum);
        return pageHandler;
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

    public UserPrincipalVO getSecurityInfo()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalVO userPrincipalVO = (UserPrincipalVO) auth.getPrincipal();
        return userPrincipalVO;
    }



}
