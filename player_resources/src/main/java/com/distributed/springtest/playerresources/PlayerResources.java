package com.distributed.springtest.playerresources;

import com.distributed.springtest.utils.security.DigestHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by Jonas on 2014-12-05.
 */
@ComponentScan
@EnableAutoConfiguration
@EnableWebMvc
@Configuration
public class PlayerResources extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {

        SpringApplication.run(PlayerResources.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new Interceptor());
    }
}
