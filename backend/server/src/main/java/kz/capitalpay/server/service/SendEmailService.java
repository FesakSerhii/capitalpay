package kz.capitalpay.server.service;

import kz.capitalpay.server.files.model.FileStorage;
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
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Service
public class SendEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailService.class);
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

    @Value("${filestorage.path}")
    private String serverFilePath;

    Properties properties = new Properties();
    Store store = null;
    Folder inbox = null;

    @PostConstruct
    public void init() {
        try {
            LOGGER.info("Init Mail Server...");
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

    private final Session session = Session.getInstance(properties, new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(user, password);
        }
    });


    public boolean sendMail(String email, String subj, String text) {
        try {
            LOGGER.info("Send message...");
//            logger.error("Email temporary disabled");
            LOGGER.info(email);
            LOGGER.info(subj);
            LOGGER.info(text);

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
            LOGGER.info("Message sent!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public void sendEmailWithFiles(String email, String subject, String text, List<FileStorage> files) {
        try {
            LOGGER.info("Send message: \nEmail: {}", email);

            session.setDebug(true);
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(subject);

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(text, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            files.forEach(x -> addFile(multipart, serverFilePath + "/" + x.getPath()));

            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);
            Transport.send(message);
            LOGGER.info("Message sent!");
        } catch (Exception e) {
            LOGGER.info("Message send error", e);
        }
    }

    private void addFile(Multipart multipart, String filePath) {
        try {
            MimeBodyPart imagePart = new MimeBodyPart();
            imagePart.setDisposition(MimeBodyPart.INLINE);
            try {
                imagePart.attachFile(new File(filePath));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            multipart.addBodyPart(imagePart);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
