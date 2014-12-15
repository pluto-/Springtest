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
        Properties prop = new Properties();
        String propFileName = "player_resources/src/main/resources/playerresources.properties";

        InputStream inputStream = new FileInputStream(propFileName);

        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        return prop.getProperty("hosts.gamecontent");
    }
}
