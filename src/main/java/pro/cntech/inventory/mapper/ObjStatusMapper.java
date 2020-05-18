package pro.cntech.inventory.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import pro.cntech.inventory.vo.LogVO;
import pro.cntech.inventory.vo.ObjDetailVO;
import pro.cntech.inventory.vo.ObjListVO;

import java.util.List;

@Repository
public interface ObjStatusMapper
{
    List<ObjListVO> getObjStatusList (ObjListVO objVO);
    int getObjStatusListTotalCnt(ObjListVO objVO);
    ObjDetailVO getObjDetail(@Param("qrSrl") String qrSrl);
    List<ObjDetailVO> getObjHistory(ObjDetailVO objDetailVO);
    int getObjHistoryCnt(ObjDetailVO objDetailVO);
    int deleteObj (@Param("qrSrl") String qrSrl);
    int updateObjStatus(ObjListVO objVO);
    void setObjLog(LogVO logVO);
    int updateObjInfo(ObjDetailVO objDetailVO);
}
