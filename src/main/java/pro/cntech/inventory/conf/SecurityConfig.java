package pro.cntech.inventory.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pro.cntech.inventory.service.MainService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    @Autowired
    private MainService mainService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public DaoAuthenticationProvider authenticationProvider(MainService mainService)
    {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(mainService);
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return authenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
    {
        auth.authenticationProvider(authenticationProvider(mainService));
    }

    @Override
    public void configure(WebSecurity web)
    {
        web.ignoring().antMatchers("/css/**", "/script/**", "/image/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
                .authorizeRequests()
                .antMatchers("/","/login","/login-fail").permitAll()//모두 허용
                .anyRequest().authenticated()
                .and()
                .csrf().ignoringAntMatchers("/*")//해당 URL CSRF 무시
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("phone")
                .passwordParameter("password")
                .defaultSuccessUrl("/main") //로그인 성공시 이동할 URL
                .failureUrl("/login-fail")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .deleteCookies("JSESSIONID") //쿠키 제거
                .invalidateHttpSession(true) //로그아웃시 세션 제거
                .clearAuthentication(true) //권한정보 제거
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/access-denied") //권한없는 URL 접속시
                .and()
                .sessionManagement()
                .maximumSessions(5)
                .expiredUrl("/login");//세션 아웃되면 이동할 url
                /*
                .maxSessionsPreventsLogin(true); //False :신규 로그인은 허용 기존 사용자는 세션 아웃  True: 이미 로그인한 세션이있으면 로그인 불가*/
    }
}
