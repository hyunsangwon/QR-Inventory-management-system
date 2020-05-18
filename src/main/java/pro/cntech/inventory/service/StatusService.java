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
import pro.cntech.inventory.vo.*;

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
        if(detailvo.getAuth().equals("manager"))
        {
            detailvo.setAuth("자산 관리자");
        }
        if(detailvo.getAuth().equals("holder"))
        {
            detailvo.setAuth("자산 소유자");
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
    public Boolean isControlObjStatus(String flag, ObjArrayVO objArrayVO) throws Exception
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
                listVO.setObjStatus(ObjStatusCode.RETURN_FINISH); //출고 대기
                insertObjLog(listVO); //로그 저장
                rows = objStatusMapper.updateObjStatus(listVO);
            }
        }
        if(rows == 0)
        {
            return false;
        }
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void insertObjLog(ObjListVO listVO)
    {
        LogVO logVO = new LogVO();
        UserPrincipalVO userPrincipalVO = getSecurityInfo();
        String holderName = userPrincipalVO.getName();
        String objAddr = userPrincipalVO.getAddr();

        logVO.setObjStatus(listVO.getObjStatus());
        logVO.setQrSrl(listVO.getQrSrl());
        logVO.setObjAddr(objAddr);
        logVO.setHolderName(holderName);
        objStatusMapper.setObjLog(logVO);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Boolean isDeleteObj(ObjDetailVO objDetailVO)
    {
        int rows = 0;
        String qrSrl = objDetailVO.getQrSrl();
        rows = objStatusMapper.deleteObj(qrSrl);
        if(rows > 0) return true;
        return  false;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Boolean isUpdateObjInfo(ObjDetailVO objDetailVO)
    {
        int rows = 0;
        rows = objStatusMapper.updateObjInfo(objDetailVO);
        if(rows > 0) return true;
        return  false;
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