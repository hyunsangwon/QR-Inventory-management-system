package pro.cntech.inventory.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import pro.cntech.inventory.vo.ObjListVO;
import pro.cntech.inventory.vo.UserVO;

import java.util.List;

@Repository
public interface AdminMapper
{
    UserVO getMyAssetInfo(@Param("userSrl") String userSrl);
    List<UserVO> getMyAdminList(@Param("userSrl") String userSrl);
    List<ObjListVO> getObjList(ObjListVO objListVO);
    int getObjListTotalCnt(@Param("userSrl") String userSrl);
    int setUserInfo(UserVO userVO);
    int updateUserInfo(UserVO userVO);
}
