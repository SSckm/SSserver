package org.sms.project.helper.mail;

import java.io.IOException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * 
 * @author 刘振兴
 * 
 *         功能：邮件服务处理器
 *
 */
public class McMailHandler {
    public static String DEFAULT_MAIL_ENCODING = "UTF-8";

    private String mailSmtpHost;//
    private String sendMailUser;
    private String sendMailPassword;
    private String mailFrom;
    Transport transport = null;

    public McMailHandler(String[] mailConfig) {
        mailSmtpHost = mailConfig[0];
        sendMailUser = mailConfig[1];
        sendMailPassword = mailConfig[2];
        mailFrom = mailConfig[3];
    }

    public void send(String mailto, String subject, String bodyText, String bodyHtml, String[][] aAffix) {
        try {
            /**
             */
//            mcMailService.setMailFrom("soaer_service@163.com");
//            mcMailService.setMailSmtpHost("smtp.163.com");
//            mcMailService.setSendMailPassword("liuzhenxing@123");
//            mcMailService.setSendMailUser("soaer_service");
            String encoding = MimeUtility.mimeCharset(DEFAULT_MAIL_ENCODING);
            Properties props = new Properties();
            props.put("mail.smtp.host", mailSmtpHost);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.pass", sendMailPassword);
            props.put("mail.smtp.user", mailFrom);
            Session session = Session.getDefaultInstance(props);
            session.setDebug(true);
            /**
             */
            MimeMessage mes = new MimeMessage(session);
            mes.setFrom(new InternetAddress(mailFrom));

            mes.addRecipients(RecipientType.TO, InternetAddress.parse(mailto, false));
            mes.setSubject(subject, encoding);
            /**
             */
            {
                MimeMultipart multipart = new MimeMultipart();
                multipart.setSubType("mixed");

                if (bodyText != null && bodyText.length() > 0) {
                    MimeBodyPart txtbodyPart = new MimeBodyPart();
                    txtbodyPart.setText(bodyText, encoding);
                    multipart.addBodyPart(txtbodyPart);

                }
                if (bodyHtml != null && bodyHtml.length() > 0) {
                    MimeBodyPart htmlBodyPart = new MimeBodyPart();
                    htmlBodyPart.setContent(bodyHtml, "text/html;charset=" + encoding);
                    multipart.addBodyPart(htmlBodyPart);
                }

                if (aAffix != null && aAffix.length > 0) {
                    for (int i = 0; i < aAffix.length; i++) {
                        MimeBodyPart affixPart = new MimeBodyPart();
                        affixPart.setDataHandler(new DataHandler(new FileDataSource(aAffix[i][1])));
                        affixPart.setFileName(MimeUtility.encodeText(aAffix[i][0], encoding, "Q"));
                        multipart.addBodyPart(affixPart);
                    }
                }
                mes.setContent(multipart);
            }
            if (transport == null || !transport.isConnected()) {
                transport = session.getTransport("smtp");
                transport.connect(props.getProperty("mail.smtp.host"), sendMailUser, sendMailPassword);
            }
            transport.sendMessage(mes, mes.getAllRecipients());
        } catch (MessagingException e) {
            throw new RuntimeException("send mail:" + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("send mail:" + e.getMessage(), e);
        }
    }

    public void close() {
        if (transport == null) {
            return;
        }
        try {
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
