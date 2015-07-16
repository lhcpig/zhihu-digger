package org.lhcpig;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 * Created by lhcpig on 2015/7/16.
 */
public class MailManager {

    public static Mail createMail(String subject, String content) {
        return new Mail(subject, content, ConfigManager.getHost(), ConfigManager.getFromAddress(), ConfigManager.getFromPassword(), ConfigManager.getToAddressList());
    }

    public static void sendMail(Mail mail) throws MessagingException {
        Properties props = new Properties();

        //设置发送邮件的邮件服务器的属性（这里使用网易的smtp服务器）
        props.put("mail.smtp.host", mail.host);
        //需要经过授权，也就是有户名和密码的校验，这样才能通过验证（一定要有这一条）
        props.put("mail.smtp.auth", "true");

        //用刚刚设置好的props对象构建一个session
        Session session = Session.getDefaultInstance(props);

        //有了这句便可以在发送邮件的过程中在console处显示过程信息，供调试使
        //用（你可以在控制台（console)上看到发送邮件的过程）
        session.setDebug(true);

        //用session为参数定义消息对象
        MimeMessage message = new MimeMessage(session);
        //加载发件人地址
        message.setFrom(new InternetAddress(mail.fromAddress));
        //加载收件人地址
        for (String to : mail.toAddressList) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        }
        //加载标题
        message.setSubject(mail.subject);
        // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
        Multipart multipart = new MimeMultipart();

        //   设置邮件的文本内容
        BodyPart contentPart = new MimeBodyPart();
        //contentPart.setText(this.text);
        contentPart.setContent(mail.content, "text/html;charset=utf-8");
        multipart.addBodyPart(contentPart);
        //将multipart对象放到message中
        message.setContent(multipart);
        //保存邮件
        message.saveChanges();
        //   发送邮件
        Transport transport = session.getTransport("smtp");
        //连接服务器的邮箱
        transport.connect(mail.host, mail.fromAddress, mail.fromPassword);
        //把邮件发送出去
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}
