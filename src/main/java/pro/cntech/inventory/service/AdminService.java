package pro.cntech.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import pro.cntech.inventory.mapper.ManagerMapper;
import pro.cntech.inventory.util.ObjStatusCode;
import pro.cntech.inventory.util.PageHandler;
import pro.cntech.inventory.vo.ObjListVO;
import pro.cntech.inventory.vo.UserPrincipalVO;
import pro.cntech.inventory.vo.UserVO;

import java.util.Arrays;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private ManagerMapper managerMapper;
    @Autowired
    private AwsService awsService;

    /*자산 담당자 기본 정보 세팅*/
    public void setMyInfo(ModelMap map,String sortName)
    {
        UserPrincipalVO user = getSecurityInfo();
        String userSrl = user.getUserSrl();
        String auth = user.getAuth();

        UserVO myInfo = managerMapper.getUserInfo(userSrl,auth);
        if(myInfo.getPhone() != null) myInfo.setPhone(setPhoneNumber(myInfo.getPhone()));

        List<UserVO> assetAdminList = managerMapper.getMyMangerList(userSrl,sortName);
        ObjListVO param = new ObjListVO();
        param.setUserSrl(userSrl); param.setLimitcount(0); param.setContentnum(20);
        param.setAuth(auth); param.setSortName("all");
        List<ObjListVO> assetList = managerMapper.getObjList(param);
        int totalCnt = managerMapper.getObjListTotalCnt(param);
        PageHandler pageHandler = pageHandler(totalCnt,1,20);

        map.addAttribute("sortName",sortName);
        map.addAttribute("myInfo",myInfo);
        map.addAttribute("assetManagerList",assetAdminList);
        map.addAttribute("assetManagerListSize",assetAdminList.size());
        map.addAttribute("assetList",assetList);
        map.addAttribute("assetListSize",assetList.size());
        map.addAttribute("pageHandler",pageHandler);
    }

    public UserVO getAdminInfo(UserVO userVO)
    {
        UserVO vo = managerMapper.getUserInfo(userVO.getUserSrl(),userVO.getAuth());
        vo.setPhone(setPhoneNumber(vo.getPhone()));
        return vo;
    }

    public String setPhoneNumber(String phone)
    {
        String firstNumber = null;
        String secondNumber = null;
        String lastNumber= null;

        if(phone.length() == 11)
        {
            firstNumber = phone.substring(0,3);
            secondNumber = phone.substring(3,7);
            lastNumber= phone.substring(7,11);
        }
        if(phone.length() == 9)
        {
            firstNumber = phone.substring(0,2);
            secondNumber = phone.substring(2,5);
            lastNumber= phone.substring(5,9);
        }
        if(phone.length() == 8)
        {
            firstNumber = phone.substring(0,4);
            secondNumber = phone.substring(4,8);
            return firstNumber+"-"+secondNumber;
        }
        if(phone.length() == 10)
        {
            firstNumber = phone.substring(0,3);
            secondNumber = phone.substring(3,6);
            lastNumber= phone.substring(6,10);
        }
        if(phone.length() > 11 || phone.length() < 8)
        {
            return phone;
        }

        return firstNumber+"-"+secondNumber+"-"+lastNumber;
    }

    public List<ObjListVO> getAdminAssetList(ObjListVO objListVO)
    {
        int MAX = 20;
        int limitCount=((objListVO.getPageNum() - 1 ) * MAX);
        int contentNum = MAX;

        objListVO.setLimitcount(limitCount); objListVO.setContentnum(contentNum);

        if("holder".equals(objListVO.getAuth()))
        {
            UserPrincipalVO userPrincipalVO = getSecurityInfo();
            String masterSrl = userPrincipalVO.getUserSrl();
            objListVO.setUserSrl(masterSrl);
        }
        List<ObjListVO> list = managerMapper.getObjList(objListVO);
        return list;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Boolean isControlUser(UserVO userVO,String flag)
    {
        int rows = 0;
        if("insert".equals(flag))
        {
            rows = managerMapper.checkManager(userVO);
            if(rows > 0)
            {
                return false;
            }
            UserPrincipalVO holder = getSecurityInfo();
            userVO.setAddr(holder.getAddr());
            rows = managerMapper.setUserInfo(userVO);
            if(rows > 0)
            {
              return  true;
            }
        }
        if("update".equals(flag))
        {
            rows = managerMapper.updateUserInfo(userVO);
            if(rows > 0)
            {
                return true;
            }
        }
        return false;
    }

    public PageHandler createPageHandler(ObjListVO listVO)
    {
        int contentNum = 20;
        int totalCnt = managerMapper.getObjListTotalCnt(listVO);
        PageHandler pageHandler = pageHandler(totalCnt,listVO.getPageNum(),contentNum);
        if(pageHandler == null) return null;
        return pageHandler;
    }

    public void getAssetAllList(int pageNum,String filterName,String sortName,ModelMap map) throws Exception
    {
        int MAX = 10;
        int limitCount=((pageNum - 1 ) * MAX);
        int contentNum = MAX;
        int totalCnt = 0;
        UserPrincipalVO userPrincipalVO = getSecurityInfo();
        String userSrl = userPrincipalVO.getUserSrl();

        ObjListVO objListVO = new ObjListVO();
        objListVO.setLimitcount(limitCount); objListVO.setContentnum(contentNum);
        objListVO.setFilterName(filterName); objListVO.setSortName(sortName); objListVO.setUserSrl(userSrl);

        List<ObjListVO> list = managerMapper.getAllAsset(objListVO);
        totalCnt = managerMapper.getAllAssetTotalCnt(objListVO);
        PageHandler pageHandler = pageHandler(totalCnt,pageNum,contentNum);

        for(ObjListVO objList : list)
        {
            String status = objList.getObjStatus();
            if(status.equals(ObjStatusCode.INNER_WAIT)) objList.setObjStatus("출고 대기");
            if(status.equals(ObjStatusCode.OUTER_WAIT)) objList.setObjStatus("출고 완료");
            if(status.equals(ObjStatusCode.RELEASE_FINISH)) objList.setObjStatus("출고 완료");
            if(status.equals(ObjStatusCode.RELEASE_START)) objList.setObjStatus("출고 시작");
            if(status.equals(ObjStatusCode.RETURN_START)) objList.setObjStatus("반납 시작");
            if(status.equals(ObjStatusCode.RETURN_WAIT)) objList.setObjStatus("반납 대기");
            if(status.equals(ObjStatusCode.RETURN_FINISH)) objList.setObjStatus("출고 대기");

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
        }

        if(sortName.equals("model"))
        {
            String[] modelName = new String[list.size()];
            for(int i =0; i <list.size(); i++)
            {
                modelName[i] = list.get(i).getModelName();
            }
            Arrays.sort(modelName);
            for(int i= 0; i < list.size(); i++)
            {
                for(int j=0; j < list.size(); j++)
                {
                    if(modelName[i].equals(list.get(j).getModelName())){
                        ObjListVO temp = list.get(j);
                        ObjListVO temp02 = list.get(i);
                        list.set(i,temp);
                        list.set(j,temp02);
                    }
                }
            }

        }

        map.addAttribute("sortName",sortName);
        map.addAttribute("filterName",filterName);
        map.addAttribute("pageNum",pageNum);
        map.addAttribute("list",list);
        map.addAttribute("size",list.size());
        map.addAttribute("pageHandler",pageHandler);
    }

    public UserPrincipalVO getSecurityInfo()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalVO userPrincipalVO = (UserPrincipalVO) auth.getPrincipal();
        return userPrincipalVO;
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
}
