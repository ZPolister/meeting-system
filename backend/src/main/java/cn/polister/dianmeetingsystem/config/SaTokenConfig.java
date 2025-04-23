package cn.polister.dianmeetingsystem.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册Sa-Token的拦截器
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**")
                .excludePathPatterns("/swagger-ui/**")
                .excludePathPatterns("/v3/api-docs/**");
    }

    // 跨域处理
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                .allowCredentials(true)
                .maxAge(3600);
    }
}