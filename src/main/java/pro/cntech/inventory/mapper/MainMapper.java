package pro.cntech.inventory.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import pro.cntech.inventory.vo.UserVO;

@Repository
public interface MainMapper
{
    UserVO getUserInfo(@Param("phone") String phone);
}
