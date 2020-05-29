package pro.cntech.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import pro.cntech.inventory.mapper.ManagerMapper;
import pro.cntech.inventory.util.PageHandler;
import pro.cntech.inventory.vo.ObjListVO;
import pro.cntech.inventory.vo.UserPrincipalVO;
import pro.cntech.inventory.vo.UserVO;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private ManagerMapper managerMapper;

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
        else
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
