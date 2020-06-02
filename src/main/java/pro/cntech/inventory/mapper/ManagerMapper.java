package pro.cntech.inventory.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import pro.cntech.inventory.vo.ObjListVO;
import pro.cntech.inventory.vo.UserVO;

import java.util.List;

@Repository
public interface ManagerMapper
{
    UserVO getUserInfo(@Param("userSrl") String userSrl,@Param("auth") String auth);
    List<UserVO> getMyMangerList (@Param("userSrl") String userSrl,@Param("sortName") String sortName);
    List<ObjListVO> getObjList(ObjListVO objListVO);
    int getObjListTotalCnt(ObjListVO objListVO);
    int checkManager(UserVO userVO);
    int setUserInfo(UserVO userVO);
    int updateUserInfo(UserVO userVO);
    List<ObjListVO>getAllAsset(ObjListVO objListVO);
    int getAllAssetTotalCnt(ObjListVO objListVO);
}
