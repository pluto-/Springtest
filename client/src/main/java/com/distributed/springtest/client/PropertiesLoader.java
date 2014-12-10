package com.distributed.springtest.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Jonas on 2014-12-08.
 */
@Service
public class PropertiesLoader{


    public String getGameContentAddressAndPort(ServletContext servletContext) throws IOException {
        String result = "";
        Properties prop = new Properties();
        String propFileName = "admin.properties";

        System.out.println(servletContext.getRealPath(""));
        InputStream inputStream = servletContext.getResourceAsStream("WEB-INF/classes/admin.properties");

        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        // get the property value and print it out
        String address = prop.getProperty("game_content_server_address");
        String port = prop.getProperty("game_content_server_port");

        return "http://" + address + ":" + port;
    }
}
