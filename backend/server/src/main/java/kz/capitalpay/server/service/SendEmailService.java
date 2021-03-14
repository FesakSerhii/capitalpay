package kz.capitalpay.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

@Service
public class SendEmailService {

    Logger logger = LoggerFactory.getLogger(SendEmailService.class);
    @Value("${mail.imap.host}")
    String imapHost;
    @Value("${mail.imap.port}")
    Integer imapPort;
    @Value("${mail.user}")
    String user;
    @Value("${mail.password}")
    String password;
    @Value("${mail.smtp.starttls.enable}")
    String smtpTLSenable;
    @Value("${mail.smtp.host}")
    String smtpHost;
    @Value("${mail.smtp.port}")
    String smtpPort;

    Properties properties = new Properties();
    Store store = null;
    Folder inbox = null;

    @PostConstruct
    public void init() {
        try {
            logger.info("Init Mail Server...");
            properties.put("mail.imap.host", imapHost);
            properties.put("mail.imap.port", imapPort);
            properties.put("mail.store.protocol", "imaps");

            properties.put("mail.smtp.auth", true);
            properties.put("mail.smtp.starttls.enable", smtpTLSenable);
            properties.put("mail.smtp.host", smtpHost);
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.port", smtpPort);
            properties.put("mail.smtp.ssl.trust", smtpHost);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean sendMail(String email, String subj, String text) {
        try {
            logger.info("Send message...");

            logger.error("Email temporary disabled");
            logger.info(email);
            logger.info(subj);
            logger.info(text);

            /*

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(subj);


            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(text, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);

            logger.info("Message sent!");
            return true;
*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

}
