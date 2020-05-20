package pro.cntech.inventory.service;


import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pro.cntech.inventory.mapper.MainMapper;
import pro.cntech.inventory.util.MapUtil;
import pro.cntech.inventory.vo.MarkerVO;
import pro.cntech.inventory.vo.StatisticsVO;
import pro.cntech.inventory.vo.UserPrincipalVO;
import pro.cntech.inventory.vo.UserVO;

import java.util.List;

@Service
public class MainService implements UserDetailsService
{

    @Autowired
    private MainMapper mainMapper;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
    public boolean isUserJoin(UserVO userVO) throws Exception
    {
        MapUtil util = new MapUtil();
        String gps[] = util.convertAddrToGPS(userVO.getAddr()).split("/");
        userVO.setLongitude(gps[0]);
        userVO.setLatitude(gps[1]);
        userVO.setPassword(makeHashedPassword(userVO.getPassword()));
        int rows = mainMapper.setUserJoin(userVO);
        if(rows > 0)
        {
            return true;
        }
        return false;
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

}
