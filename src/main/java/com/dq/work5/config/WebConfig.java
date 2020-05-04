package com.dq.work5.config;

import com.dq.work5.filter.XssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean  xssFilter(){
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new XssFilter());
        bean.addUrlPatterns("/*");
        return bean;
    }
}
