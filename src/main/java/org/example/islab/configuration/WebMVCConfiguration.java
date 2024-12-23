package org.example.islab.configuration;

import org.example.islab.configuration.Interceptors.RequireRoleInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebMVCConfiguration implements WebMvcConfigurer {
    private final RequireRoleInterceptor requireRoleInterceptor;

    public WebMVCConfiguration(RequireRoleInterceptor requireRoleInterceptor){
        this.requireRoleInterceptor = requireRoleInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requireRoleInterceptor).addPathPatterns("/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
