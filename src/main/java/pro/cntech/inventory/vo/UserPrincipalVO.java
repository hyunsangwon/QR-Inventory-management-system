package pro.cntech.inventory.vo;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class UserPrincipalVO implements UserDetails
{
    //UID값을 명시 해주지 않으면 자바 컴파일러가 임시적인 값을 부여한다.
    private static final long serialVersionUID = 1L;
    private UserVO userVO;

    public UserPrincipalVO(UserVO userVO) {
        this.userVO = userVO;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(userVO.getAuth()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return userVO.getPassword();
    }
    @Override
    public String getUsername() {
        return userVO.getPhone();
    }

    public String getAuth() { return userVO.getAuth(); }

    public String getAddr() {
        return userVO.getAddr();
    }

    public String getName() {return userVO.getUserName(); }

    public String getUserSrl() { return userVO.getUserSrl(); }

    public String getCompanyName()
    {
        return userVO.getCompanyName();
    }

    public String getCompanyPhone()
    {
        return userVO.getCompanyPhone();
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() { return true;}
}
