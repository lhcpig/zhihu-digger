package org.lhcpig;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by lhcpig on 2015/7/11.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Properties p = new Properties();
        p.load(Main.class.getResourceAsStream("/config.properties"));
        String authorization = p.getProperty("Authorization");
        String people = p.getProperty("people");
        String doc = Jsoup.connect("http://api.zhihu.com/people/" + people + "/activities")
                .ignoreContentType(true)
                .header("Authorization", authorization)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .timeout(5000)
                .execute()
                .body();
        System.out.println(doc);


    }
}
