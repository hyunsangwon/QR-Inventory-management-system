package pro.cntech.inventory.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import pro.cntech.inventory.service.AwsService;
import pro.cntech.inventory.service.MainService;
import pro.cntech.inventory.vo.MarkerVO;
import pro.cntech.inventory.vo.UserVO;

@Controller
public class MainContoller
{
    private static final Logger logger = LoggerFactory.getLogger(MainContoller.class);
    private static final String MASTER_VIEW_PREFIX = "cntech/";
    private static final String MANAGER_VIEW_PREFIX = "manager/";

    @Autowired
    private MainService mainService;
    @Autowired
    private AwsService awsService;
    
    @GetMapping("/login")
    public String loadLoginxPage(HttpServletRequest request)
    {
        logger.debug("[ Call /login - GET ]");
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) ip = request.getRemoteAddr();
        logger.debug("[Client IP] : "+ ip);

        return "login";
    }

    @GetMapping("/login-fail")
    public String loadLoginPagefail(ModelMap model, HttpServletRequest request)
    {
        logger.debug("[ Call /login-fail - GET ]");
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) ip = request.getRemoteAddr();
        logger.debug("/Client IP : "+ip+"/");
        model.addAttribute("error",true);
        return "login";
    }

    @GetMapping("/")
    public String redirectLoginPage()
    {
        logger.debug("[ Call / - GET ]");
        return "redirect:/login";
    }

    @GetMapping("/main")
    public String loadMainPage(ModelMap model)
    {
        logger.debug("[ Call /main - GET ]");
        model.addAttribute("statistics",mainService.getMainStatistics());
        return MANAGER_VIEW_PREFIX+"main";
    }

    @PostMapping("/ajax/call/kakaomap/marker")
    public @ResponseBody List<MarkerVO> ajaxCallMapMarker(@RequestBody UserVO userVO)
    {
        logger.debug("[ Call /ajax/call/kakaomap/marker- GET ]");
        logger.debug("Param : "+userVO.toString());
        return mainService.getCompanyGPS(userVO);
    }

    @GetMapping("/join")
    public String loadMapPage()
    {
        logger.debug("[ Call /join - GET ]");
        return "join";
    }

    @PostMapping("/join")
    public @ResponseBody UserVO userJoin(@RequestBody UserVO userVO) throws Exception
    {
        logger.debug("[ Call /join - POST ]");
        logger.debug("Param : "+userVO.toString());
        return mainService.userJoin(userVO);
    }

    @PostMapping("/img/upload")
    public @ResponseBody Boolean imageUpload(@RequestParam("files") MultipartFile[] file,
                                             @RequestParam("userSrl") String userSrl,
                                             @RequestParam("businessNumber") String businessNumber) throws Exception
    {
        logger.debug("[ Call /img/upload - POST ]");
        if(file.length == 0)
        {
            return false;
        }
        if(userSrl == null || businessNumber == null)
        {
            return false;
        }
        for(int i=0; i< file.length; i++) {
            logger.debug("====> Parameters      file name  : " + file[i].getName());
            logger.debug("====> Parameters      file size  : " + file[i].getSize());
        }
        mainService.uploadImageToAwsS3(file,userSrl,businessNumber);
        return true;
    }
    
    //사용자 매뉴얼, 브로셔 다운로드
    @GetMapping("/file/download/{type}")
    public ResponseEntity<ByteArrayResource> downloadFile(HttpServletResponse response, @PathVariable("type") String type) throws Exception 
    {
    	
    	 logger.debug("[ Call /file/download/"+type+" - GET ]");
    	 String FILE_MANUAL = "WEISER_QR_SERVICE_manual_ver1.4.pdf";
    	 String FILE_BROCHURE = "WEISER_QR_SERVICE_brochure_v1.2.pdf";
    	 String BUCKET_PATH = "/public-download";   		
    	 byte[] data = null;
    	 String keyName = null;
    	 if("manual".equals(type))
    	 {
    		 data = awsService.getObject(BUCKET_PATH, FILE_MANUAL);
    		 keyName = FILE_MANUAL;
    	 }
    	 if("brochure".equals(type))
    	 {
    		 data = awsService.getObject(BUCKET_PATH, FILE_BROCHURE);
    		 keyName = FILE_BROCHURE;
    	 }
    	 final ByteArrayResource resource = new ByteArrayResource(data);
    	 
    	 return ResponseEntity
                 .ok()
                 .contentLength(data.length)
                 .header("Content-type", "application/octet-stream")
                 .header("Content-disposition", "attachment; filename=\"" + keyName + "\"")
                 .body(resource);
    }

}