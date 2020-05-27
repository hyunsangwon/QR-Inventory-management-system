package pro.cntech.inventory.mapper;

import org.apache.catalina.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import pro.cntech.inventory.vo.CertificateVO;
import pro.cntech.inventory.vo.MarkerVO;
import pro.cntech.inventory.vo.StatisticsVO;
import pro.cntech.inventory.vo.UserVO;

import java.util.List;

@Repository
public interface MainMapper
{
    UserVO getUserInfo(@Param("phone") String phone);
    StatisticsVO getMainStatistics(@Param("userSrl") String userSrl);
    List<MarkerVO> getCompanyGPS(UserVO userVO);
    int updateMyInfo(UserVO userVO);
    int setUserJoin(UserVO userVO);
    UserVO checkUser(UserVO userVO);
    int updateMasterSrl(UserVO userVO);
    int setCertificate(CertificateVO certificateVO);
    int getbusinessNumber(@Param("businessNumber") String businessNumber);
}
