package com.distributed.springtest.playerresources;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Jonas on 2014-12-05.
 */
@ComponentScan
@EnableAutoConfiguration
@EnableWebMvc
@Configuration
public class Application extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new Interceptor());
    }
}
