package com.distributed.springtest.playerresources;

import com.distributed.springtest.utils.security.DigestHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by Jonas on 2014-12-05.
 */
@ComponentScan
@EnableAutoConfiguration
@PropertySource("classpath:playerresources.properties")
public class PlayerResources {

    public static void main(String[] args) {

        SpringApplication.run(PlayerResources.class, args);
    }
}
