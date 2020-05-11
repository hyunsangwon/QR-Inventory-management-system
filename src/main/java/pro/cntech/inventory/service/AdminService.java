package pro.cntech.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import pro.cntech.inventory.mapper.AdminMapper;
import pro.cntech.inventory.util.PageHandler;
import pro.cntech.inventory.vo.ObjListVO;
import pro.cntech.inventory.vo.UserPrincipalVO;
import pro.cntech.inventory.vo.UserVO;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    /*자산 담당자 기본 정보 세팅*/
    public void setMyInfo(ModelMap map)
    {
        UserPrincipalVO user = getSecurityInfo();
        String userSrl = user.getUserSrl();
        UserVO myInfo = adminMapper.getMyAssetInfo(userSrl);
        List<UserVO> assetAdminList = adminMapper.getMyAdminList(userSrl);
        ObjListVO param = new ObjListVO();
        param.setUserSrl(userSrl); param.setLimitcount(1); param.setContentnum(20);
        List<ObjListVO> assetList = adminMapper.getObjList(param);
        int totalCnt = adminMapper.getObjListTotalCnt(userSrl);
        PageHandler pageHandler = pageHandler(totalCnt,1,20);

        map.addAttribute("myInfo",myInfo);
        map.addAttribute("assetAdminList",assetAdminList);
        map.addAttribute("assetAdminListSize",assetAdminList.size());
        map.addAttribute("assetList",assetList);
        map.addAttribute("assetListSize",assetList.size());
        map.addAttribute("pageHandler",pageHandler);
    }

    public UserVO getAdminInfo(UserVO userVO)
    {
        return adminMapper.getMyAssetInfo(userVO.getUserSrl());
    }

    public List<ObjListVO> getAdminAssetList(ObjListVO objListVO)
    {
        int MAX = 15;
        int limitCount=((objListVO.getPageNum() - 1 ) * MAX);
        int contentNum = MAX;
        objListVO.setLimitcount(limitCount); objListVO.setContentnum(contentNum);
        return adminMapper.getObjList(objListVO);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Boolean isControlUser(UserVO userVO,String flag)
    {
        int rows = 0;
        if("insert".equals(flag))
        {
            rows = adminMapper.setUserInfo(userVO);
            if(rows > 0)
            {
              return  true;
            }
        }
        if("update".equals(flag))
        {
            rows = adminMapper.updateUserInfo(userVO);
            if(rows > 0)
            {
                return true;
            }
        }
        return false;
    }

    public PageHandler createPageHandler(ObjListVO listVO)
    {
        int contentNum = 15;
        int totalCnt = adminMapper.getObjListTotalCnt(listVO.getUserSrl());
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
