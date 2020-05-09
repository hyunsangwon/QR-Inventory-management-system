package pro.cntech.inventory.conf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pro.cntech.inventory.util.Interceptor;

@Configuration
@MapperScan(value = {"pro.cntech.inventory.mapper"})
public class WebConfig implements WebMvcConfigurer
{
    @Autowired
    Interceptor interceptor;
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/main","/order/**","/obj/**","/master/**", "/admin/**")
                .excludePathPatterns("/login", "/login-fail", "/access-denied", "/ajax/**");
    }
}
