package org.sms.project.helper.mail;

/**
 * @author 刘振兴
 *
 */
public class McMailService {

    private String mailSmtpHost;
    private String sendMailUser;
    private String sendMailPassword;
    private String mailFrom;

    /**
     * @return the mailSmtpHost
     */
    public final String getMailSmtpHost() {
        return mailSmtpHost;
    }

    /**
     * @param mailSmtpHost
     *            the mailSmtpHost to set
     */
    public final void setMailSmtpHost(String mailSmtpHost) {
        this.mailSmtpHost = mailSmtpHost;
    }

    /**
     * @return the sendMailUser
     */
    public final String getSendMailUser() {
        return sendMailUser;
    }

    /**
     * @param sendMailUser
     *            the sendMailUser to set
     */
    public final void setSendMailUser(String sendMailUser) {
        this.sendMailUser = sendMailUser;
    }

    /**
     * @return the sendMailPassword
     */
    public final String getSendMailPassword() {
        return sendMailPassword;
    }

    /**
     * @param sendMailPassword
     *            the sendMailPassword to set
     */
    public final void setSendMailPassword(String sendMailPassword) {
        this.sendMailPassword = sendMailPassword;
    }

    /**
     * @return the mailFrom
     */
    public final String getMailFrom() {
        return mailFrom;
    }

    /**
     * @param mailFrom
     *            the mailFrom to set
     */
    public final void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public void send(String mailto, String subject, String bodyText, String bodyHtml, String[][] aAffix) {
        McMailHandler mcHandler = new McMailHandler(new String[] { mailSmtpHost, sendMailUser, sendMailPassword, mailFrom });
        mcHandler.send(mailto, subject, bodyText, bodyHtml, aAffix);
    }
}
