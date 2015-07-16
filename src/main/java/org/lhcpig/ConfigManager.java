package org.lhcpig;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by lhcpig on 2015/7/16.
 */
public class ConfigManager {
    private static Properties p = new Properties();

    static {
        try {
            p.load(WebMain.class.getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPeople() {
        return p.getProperty("people");
    }

    public static String getHost() {
        return p.getProperty("host");
    }

    public static String getFromAddress() {
        return p.getProperty("fromAddress");
    }

    public static String getFromPassword() {
        return p.getProperty("fromPassword");
    }

    public static List<String> getToAddressList() {
        return Arrays.asList(p.getProperty("toAddressList").split("[ ,;]"));
    }

}
