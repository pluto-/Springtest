package com.distributed.springtest.playerresources;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Jonas on 2014-12-08.
 */
public class PropertiesLoader {

    public static String getAddressAndPort() throws IOException {
        String result = "";
        Properties prop = new Properties();
        String propFileName = "player_resources/src/main/resources/playerresources.properties";

        InputStream inputStream = new FileInputStream(propFileName);

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
