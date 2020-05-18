package pro.cntech.inventory.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import pro.cntech.inventory.vo.ObjListVO;
import pro.cntech.inventory.vo.UserVO;

import java.util.List;

@Repository
public interface ManagerMapper
{
    UserVO getUserInfo(UserVO userVO);
    List<UserVO> getMyMangerList (@Param("userSrl") String userSrl);
    List<ObjListVO> getObjList(ObjListVO objListVO);
    int getObjListTotalCnt(ObjListVO objListVO);
    int setUserInfo(UserVO userVO);
    int updateUserInfo(UserVO userVO);
}