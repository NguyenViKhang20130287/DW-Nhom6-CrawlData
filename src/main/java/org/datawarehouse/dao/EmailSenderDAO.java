package org.datawarehouse.dao;

import org.datawarehouse.ultils.EmailConfig;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSenderDAO {
    public void sendEmail(String content) {
        // Get properties object
        Properties props = new Properties();
        props.put("mail.smtp.host", EmailConfig.HOST_NAME);
        props.put("mail.smtp.port", EmailConfig.TSL_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.port", EmailConfig.TSL_PORT);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        // get Session
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailConfig.APP_EMAIL, EmailConfig.APP_PASSWORD);
            }
        });

        // compose message
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EmailConfig.APP_EMAIL));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(EmailConfig.TO_EMAIL));
            message.setSubject("Báo cáo lỗi");
            message.setText(content);

            // send message
            Transport.send(message);

            System.out.println("Message sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
