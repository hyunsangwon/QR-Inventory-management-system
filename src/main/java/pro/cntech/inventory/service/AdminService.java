package pro.cntech.inventory.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import pro.cntech.inventory.mapper.ManagerMapper;
import pro.cntech.inventory.util.ContactFilter;
import pro.cntech.inventory.util.ObjStatusCode;
import pro.cntech.inventory.util.PageHandler;
import pro.cntech.inventory.vo.ObjListVO;
import pro.cntech.inventory.vo.UserPrincipalVO;
import pro.cntech.inventory.vo.UserVO;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private ManagerMapper managerMapper;
    @Autowired
    private AwsService awsService;

    /*자산 담당자 기본 정보 세팅*/
    public void setMyInfo(ModelMap map,String sortName) throws Exception
    {
        UserPrincipalVO user = getSecurityInfo();
        String userSrl = user.getUserSrl();
        String auth = user.getAuth();

        UserVO myInfo = managerMapper.getUserInfo(userSrl,auth);
        if(myInfo.getPhone() != null) myInfo.setPhone(ContactFilter.getInstance().setPhoneNumber(myInfo.getPhone()));

        List<UserVO> adminList = managerMapper.getMyMangerList(userSrl,sortName);
        ObjListVO param = new ObjListVO();
        param.setUserSrl(userSrl); param.setLimitcount(0); param.setContentnum(20);
        param.setAuth(auth); param.setSortName("all");
        List<ObjListVO> assetList = managerMapper.getObjList(param);
        for(ObjListVO objList : assetList)
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
        }
        int totalCnt = managerMapper.getObjListTotalCnt(param);
        PageHandler pageHandler = pageHandler(totalCnt,1,20);

        map.addAttribute("sortName",sortName);
        map.addAttribute("myInfo",myInfo);
        map.addAttribute("assetManagerList",adminList);
        map.addAttribute("assetManagerListSize",adminList.size());
        map.addAttribute("assetList",assetList);
        map.addAttribute("assetListSize",assetList.size());
        map.addAttribute("pageHandler",pageHandler);
    }

    public UserVO getAdminInfo(UserVO userVO)
    {
        UserVO vo = managerMapper.getUserInfo(userVO.getUserSrl(),userVO.getAuth());
        vo.setPhone(ContactFilter.getInstance().setPhoneNumber(vo.getPhone()));
        return vo;
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

    public void getAssetAllList(int pageNum,String filterName,String sortName,String searchValue,ModelMap map) throws Exception
    {
        int MAX = 10;
        int limitCount=((pageNum - 1 ) * MAX);
        int contentNum = MAX;
        int totalCnt = 0;
        UserPrincipalVO userPrincipalVO = getSecurityInfo();
        String userSrl = userPrincipalVO.getUserSrl();

        ObjListVO objListVO = new ObjListVO();
        objListVO.setLimitcount(limitCount); objListVO.setContentnum(contentNum); objListVO.setSearchValue(searchValue);
        objListVO.setFilterName(filterName); objListVO.setSortName(sortName); objListVO.setUserSrl(userSrl);

        List<ObjListVO> list = managerMapper.getAllAsset(objListVO);
        totalCnt = managerMapper.getAllAssetTotalCnt(objListVO);
        PageHandler pageHandler = pageHandler(totalCnt,pageNum,contentNum);

        for(ObjListVO objList : list)
        {
            String status = objList.getObjStatus();
            if(status.equals(ObjStatusCode.RELEASE)) objList.setObjStatus("출고 완료");
            if(status.equals(ObjStatusCode.NEW_RELEASE)) objList.setObjStatus("출고 완료");
            if(status.equals(ObjStatusCode.WAREHOUSING_WAIT)) objList.setObjStatus("입고 대기");
            if(status.equals(ObjStatusCode.WAREHOUSING)) objList.setObjStatus("입고 완료");
            if(status.equals(ObjStatusCode.NEW_WAREHOUSING)) objList.setObjStatus("입고 완료");
            if(status.equals(ObjStatusCode.SHIPPING)) objList.setObjStatus("배송 중");

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
        map.addAttribute("searchValue",searchValue);
        map.addAttribute("sortName",sortName);
        map.addAttribute("filterName",filterName);
        map.addAttribute("pageNum",pageNum);
        map.addAttribute("list",list);
        map.addAttribute("size",list.size());
        map.addAttribute("pageHandler",pageHandler);
    }

    public Workbook makeExcelForm(ObjListVO objListVO) throws Exception
    {
        /*EXCEL SETTINGS*/
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("자산 목록"); //시트 생성
        Row row = null; //행
        Cell cell = null; //열
        int rowNo = 0; //열 번호

        CellStyle headStyle = excelStyle(workbook,"head");
        CellStyle bodyStyle = excelStyle(workbook,"body");

        row = sheet.createRow(rowNo++);

        cell = row.createCell(0);
        cell.setCellStyle(headStyle);
        cell.setCellValue("자산 상태");

        cell = row.createCell(1);
        cell.setCellStyle(headStyle);
        cell.setCellValue("모델명");

        cell = row.createCell(2);
        cell.setCellStyle(headStyle);
        cell.setCellValue("자산 종류");

        cell = row.createCell(3);
        cell.setCellStyle(headStyle);
        cell.setCellValue("관리자");

        cell = row.createCell(4);
        cell.setCellStyle(headStyle);
        cell.setCellValue("QR ID");

        cell = row.createCell(5);
        cell.setCellStyle(headStyle);
        cell.setCellValue("거래처 명");


        UserPrincipalVO userPrincipalVO = getSecurityInfo();
        String userSrl = userPrincipalVO.getUserSrl();
        objListVO.setUserSrl(userSrl);

        List<ObjListVO> list = managerMapper.getAssetExcelList(objListVO);

        for(ObjListVO objList : list)
        {
            row = sheet.createRow(rowNo++);
            String status = objList.getObjStatus();
            if(status.equals(ObjStatusCode.RELEASE)) objList.setObjStatus("출고 완료");
            if(status.equals(ObjStatusCode.NEW_RELEASE)) objList.setObjStatus("출고 완료");
            if(status.equals(ObjStatusCode.WAREHOUSING_WAIT)) objList.setObjStatus("입고 대기");
            if(status.equals(ObjStatusCode.WAREHOUSING)) objList.setObjStatus("입고 완료");
            if(status.equals(ObjStatusCode.NEW_WAREHOUSING)) objList.setObjStatus("입고 완료");
            if(status.equals(ObjStatusCode.SHIPPING)) objList.setObjStatus("배송 중");

            cell = row.createCell(0);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(""+objList.getObjStatus());

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
            cell = row.createCell(1);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(""+objList.getModelName());


            cell = row.createCell(2);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(""+objList.getObjKinds());

            cell = row.createCell(3);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(""+objList.getUserName());

            cell = row.createCell(4);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(""+objList.getQrSrl());

            cell = row.createCell(5);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(""+objList.getCompanyName());
        }
        return workbook;
    }

    public CellStyle excelStyle(Workbook workbook, String layout)
    {
        CellStyle cellStyle = null;

        if(layout.equals("head"))
        {
            cellStyle = workbook.createCellStyle();//테이블 헤더 스타일
            // 가는 경계선을 가집니다.
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            // 배경색은 노란색입니다.
            cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // 데이터는 가운데 정렬합니다.
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            return cellStyle;
        }
        else
        {
            cellStyle = workbook.createCellStyle();
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            return cellStyle;
        }
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
