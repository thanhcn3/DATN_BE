package com.datn.datn_be.configuration;

import com.datn.datn_be.configuration.interceptor.RequestMappingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration to register interceptors and other web settings
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private RequestMappingInterceptor requestMappingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestMappingInterceptor)
                .addPathPatterns("/**")  // Apply to all paths
                .order(1);  // Set order of execution
    }
}

