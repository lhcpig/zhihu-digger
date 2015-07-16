package org.lhcpig;

import java.util.List;

/**
 * Created by lhcpig on 2015/7/16.
 */
public class Mail {
    final String subject;
    final String content;
    final String host;
    final String fromAddress;
    final String fromPassword;
    final List<String> toAddressList;

    public Mail(String subject, String content, String host, String fromAddress, String fromPassword, List<String> toAddressList) {
        this.subject = subject;
        this.content = content;
        this.host = host;
        this.fromAddress = fromAddress;
        this.fromPassword = fromPassword;
        this.toAddressList = toAddressList;
    }
}
