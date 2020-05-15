package pro.cntech.inventory.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pro.cntech.inventory.mapper.MainMapper;
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
        return mainMapper.getCompanyGPS(userVO);
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

    public boolean isUserinfoUpdate(UserVO userVO)
    {
        String password = userVO.getPassword();
        password = bCryptPasswordEncoder.encode(password);
        userVO.setPassword(password);
        int rows = mainMapper.updateMyInfo(userVO);
        if(rows > 0)
        {
            return true;
        }
        return false;
    }
}
