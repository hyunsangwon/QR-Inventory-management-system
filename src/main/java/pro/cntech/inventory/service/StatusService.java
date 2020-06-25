package pro.cntech.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import pro.cntech.inventory.mapper.ObjStatusMapper;
import pro.cntech.inventory.util.*;
import pro.cntech.inventory.vo.*;

import java.util.List;

@Service
public class StatusService
{

    @Autowired
    private ObjStatusMapper objStatusMapper;
    @Autowired
    private AwsService awsService;

    public void getObjStatusList(ModelMap map, ObjListVO vo) throws Exception
    {
        int MAX = 10;
        int limitCount=((vo.getPageNum() - 1 ) * MAX);
        int contentNum = MAX;
        int totalCnt = 0;
        UserPrincipalVO userInfo = getSecurityInfo();
        vo.setUserSrl(userInfo.getUserSrl()); vo.setLimitcount(limitCount); vo.setContentnum(contentNum);
        vo.setAuth(userInfo.getAuth());
        totalCnt = objStatusMapper.getObjStatusListTotalCnt(vo);
        PageHandler pageHandler = pageHandler(totalCnt,vo.getPageNum(),contentNum);
        List<ObjListVO> list = objStatusMapper.getObjStatusList(vo);
        for(ObjListVO objList : list)
        {
            if(objList.getModelName() == null)
            {
                String[] modelNameArr = objList.getModelImageName().split("/");
                String modelName = awsService.getConvertedText(modelNameArr[modelNameArr.length-1]).replace("\"","");
                objList.setModelName(modelName);
                if(modelName.equals(""))
                {
                    objList.setModelName("모델명 인식 실패");
                }
                if(modelName.equals("인식 실패"))
                {
                    objList.setModelName("모델명 인식 실패");
                }
            }

            if(!vo.getObjStatus().equals("release"))
            {
                if(objList.getSrlName() == null)
                {
                    String[] srlNameArr = objList.getSrlImageName().split("/");
                    String srlName = awsService.getConvertedText(srlNameArr[srlNameArr.length-1]).replace("\"","");
                    objList.setSrlName(srlName);
                    if(srlName.equals(""))
                    {
                        objList.setSrlName("시리얼명 인식 실패");
                    }
                    if(srlName.equals("인식 실패"))
                    {
                        objList.setSrlName("시리얼명 인식 실패");
                    }
                }
            }

        }
        map.addAttribute("pageNum",vo.getPageNum());
        map.addAttribute("objStatus",vo.getObjStatus());
        map.addAttribute("list",list);
        map.addAttribute("size",totalCnt);
        map.addAttribute("pageHandler",pageHandler);
    }

    /* 자산 상세보기 */
    public ObjDetailVO getObjDetail(String qrSrl) throws Exception
    {
        ObjDetailVO detailvo = objStatusMapper.getObjDetail(qrSrl);
        detailvo.setUserPhone(ContactFilter.getInstance().setPhoneNumber(detailvo.getUserPhone()));
        detailvo.setCompanyPhone(ContactFilter.getInstance().setPhoneNumber(detailvo.getCompanyPhone()));
        String[] SrlNameArr = detailvo.getObjSrlImage().split("/");
        String[] modelNameArr = detailvo.getObjModelImage().split("/");
        if(detailvo.getObjKinds() == null)
        {
            detailvo.setObjKinds("null");
        }
        if(detailvo.getSrlName() == null)
        {
            String ocrSrlName = awsService.getConvertedText(SrlNameArr[SrlNameArr.length-1]).replace("\"","");
            detailvo.setSrlName(ocrSrlName);
            if(ocrSrlName.equals(""))
            {
                detailvo.setSrlName("시리얼명 인식 실패");
            }
            if(ocrSrlName.equals("인식 실패"))
            {
                detailvo.setSrlName("시리얼명 인식 실패");
            }
        }
        if(detailvo.getModelName() == null)
        {
            String ocrModelName = awsService.getConvertedText(modelNameArr[modelNameArr.length-1]).replace("\"","");
            detailvo.setModelName(ocrModelName);
            if(ocrModelName.equals(""))
            {
                detailvo.setModelName("모델명 인식 실패");
            }
            if(ocrModelName.equals("인식 실패"))
            {
                detailvo.setModelName("모델명 인식 실패");
            }
        }

        if(detailvo.getAuth().equals("manager"))
        {
            detailvo.setAuth("자산 관리자");
        }
        if(detailvo.getAuth().equals("holder"))
        {
            detailvo.setAuth("자산 소유자");
        }

        DistanceCalculation distanceCalculation = new DistanceCalculation();
        double meter = distanceCalculation.distance(
                Double.parseDouble(detailvo.getLatitude()),
                Double.parseDouble(detailvo.getLongitude()),
                Double.parseDouble(detailvo.getCompanyLat()),
                Double.parseDouble(detailvo.getCompanyLon()),
                "meter"
                );
        detailvo.setRadiusDistance((int)meter);
        return detailvo;
    }

    public List<ObjDetailVO> getObjHistoryList(ObjDetailVO vo)
    {
        int MAX = 10;
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
                listVO.setObjStatus(ObjStatusCode.ASSET_DELETE);
                insertObjLog(listVO); //로그 저장
                String qrSrl = listVO.getQrSrl();
                rows = objStatusMapper.deleteObj(qrSrl);
            }
        }
        if("update".equals(flag))
        {
            for(ObjListVO listVO: objArrayVO.getParam())
            {
                listVO.setObjStatus(ObjStatusCode.WAREHOUSING); //출고 대기
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
        logVO.setUserSrl(userPrincipalVO.getUserSrl());
        logVO.setObjStatus(listVO.getObjStatus());
        logVO.setQrSrl(listVO.getQrSrl());
        logVO.setHolderName(holderName);
        objStatusMapper.setObjLog(logVO);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Boolean isDeleteObj(ObjDetailVO objDetailVO)
    {
        int rows = 0;
        String qrSrl = objDetailVO.getQrSrl();
        rows = objStatusMapper.deleteObj(qrSrl);
        //삭제 로그 추가 개발 예정
        if(rows > 0) return true;
        return  false;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Boolean isUpdateObjDetailInfo(ObjDetailVO objDetailVO,String flag) throws Exception
    {
        int rows = 0;
        if("obj".equals(flag))
        {
            String kinds= null;
            if(objDetailVO != null)
            {
                kinds = objDetailVO.getObjKinds();
                if("machine".equals(kinds))
                {
                    objDetailVO.setObjKinds("복합기");
                }
                if("crusher".equals(kinds))
                {
                    objDetailVO.setObjKinds("문서 파쇄기");
                }
                if("purifier".equals(kinds))
                {
                    objDetailVO.setObjKinds("공기 청정기");
                }
                if("ink".equals(kinds))
                {
                    objDetailVO.setObjKinds("무한 잉크 공급기");
                }
            }
            rows = objStatusMapper.updateObjInfo(objDetailVO);
        }
        if("company".equals(flag))
        {
            MapUtil mapUtil = new MapUtil();
            String[] gps = mapUtil.convertAddrToGPS(objDetailVO.getCompanyAddr()).split("/");
            objDetailVO.setLongitude(gps[0]);
            objDetailVO.setLatitude(gps[1]);
            rows = objStatusMapper.updateCompanyInfo(objDetailVO);
        }

        if(rows > 0) return true;
        return  false;
    }

    public PageHandler getpageHandler(ObjDetailVO vo)
    {
        int contentNum = 10;
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