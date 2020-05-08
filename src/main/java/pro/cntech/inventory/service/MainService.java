package pro.cntech.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pro.cntech.inventory.mapper.MainMapper;
import pro.cntech.inventory.vo.UserPrincipalVO;
import pro.cntech.inventory.vo.UserVO;

@Service
public class MainService implements UserDetailsService
{

    @Autowired
    private MainMapper mainMapper;
    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException
    {
        UserVO userVO = mainMapper.getUserInfo(phone);
        return new UserPrincipalVO(userVO);
    }


}
