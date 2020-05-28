package pro.cntech.inventory.service;


import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.cntech.inventory.mapper.MainMapper;
import pro.cntech.inventory.util.MapUtil;
import pro.cntech.inventory.vo.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class MainService implements UserDetailsService
{

    @Autowired
    private MainMapper mainMapper;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private AwsService awsService;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException
    {
        UserVO userVO = mainMapper.getUserInfo(phone);
        return new UserPrincipalVO(userVO);
    }

    public List<MarkerVO> getCompanyGPS(UserVO userVO)
    {
        List<MarkerVO> list = mainMapper.getCompanyGPS(userVO);
        for(MarkerVO vo : list)
        {
           vo.setCompanyPhone(setCompanyPhoneNumber(vo.getCompanyPhone()));
        }

        return list;
    }

    public StatisticsVO getMainStatistics()
    {
        UserPrincipalVO userPrincipalVO = getSecurityInfo();
        return mainMapper.getMainStatistics(userPrincipalVO.getUserSrl());
    }

    public UserPrincipalVO getSecurityInfo()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalVO userPrincipalVO = (UserPrincipalVO) auth.getPrincipal();
        return userPrincipalVO;
    }

    public String setCompanyPhoneNumber(String phone)
    {
        String firstNumber = phone.substring(0,3);
        String secondNumber = phone.substring(3,6);
        String lastNumber= phone.substring(6,10);
        return firstNumber+"-"+secondNumber+"-"+lastNumber;
    }

    public boolean isUserinfoUpdate(UserVO userVO)
    {
        String password = userVO.getPassword();
        if(password != null)
        {
            password = bCryptPasswordEncoder.encode(password);
            userVO.setPassword(password);
        }
        int rows = mainMapper.updateMyInfo(userVO);
        if(rows > 0)
        {
            return true;
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public UserVO userJoin(UserVO userVO) throws Exception
    {
        int rows = 0;
        rows = mainMapper.getbusinessNumber(userVO.getBusinessNumber());
        if(rows > 0) //사업자 번호 확인
        {
            return null;
        }
        UserVO vo = mainMapper.checkUser(userVO);
        if(vo != null)
        {
            return null;
        }
        MapUtil util = new MapUtil();
        String gps[] = util.convertAddrToGPS(userVO.getDetailAddr()).split("/");
        userVO.setLongitude(gps[0]);
        userVO.setLatitude(gps[1]);
        userVO.setPassword(makeHashedPassword(userVO.getPassword()));
        rows = mainMapper.setUserJoin(userVO);
        if(rows > 0)
        {
            vo = new UserVO();
            vo = mainMapper.checkUser(userVO);
            mainMapper.updateMasterSrl(vo);
            return vo;
        }
        return null;
    }

    //단방향 암호
    public String makeHashedPassword(String password)
    {
        String hashedStr = null;
        if(password != null)
        {
            hashedStr = BCrypt.hashpw(password,BCrypt.gensalt());
        }
        return hashedStr;
    }

    public void uploadImageToAwsS3(MultipartFile[] file,String userSrl,String businessNumber) throws Exception
    {
        Random ran = new Random();
        String imageName = new SimpleDateFormat( "yyMMdd").format(new Date());
        String randomNumber = Integer.toString(ran.nextInt(500)+10);
        String awsS3Url = "https://qr-s3.s3.ap-northeast-2.amazonaws.com";
        String s3bucketPath = "/private/users/"+randomNumber+"/"+imageName;
        int fileSize = file.length;
        CertificateVO certificateVO = null;
        for(int i=0; i<fileSize; i++)
        {
            certificateVO = new CertificateVO();
            certificateVO.setBusinessNumber(businessNumber);
            certificateVO.setCertificateImage(awsS3Url+s3bucketPath+imageName+".jpg");
            certificateVO.setUserSrl(userSrl);
            mainMapper.setCertificate(certificateVO);

            awsService.uploadObject(file[i],s3bucketPath,imageName+".jpg");
        }
    }

}
