package org.k11techlab.framework.selenium.coreframework.reporter;

import org.k11techlab.framework.selenium.coreframework.logger.Log;
import org.k11techlab.framework.selenium.coreframework.configManager.ConfigurationManager;
import org.k11techlab.framework.selenium.coreframework.enums.ApplicationProperties;
import org.k11techlab.framework.selenium.coreframework.commonUtil.*;

import org.testng.ISuite;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class EmailExecutionReport {

    public static void sendEmailWithAttachedExecutionReport(List<ISuite> suites, int pass, int fail, int skip) {
        String smtpHostServer = ConfigurationManager.getBundle().getPropertyValue("mail.smtp.host");
        String emailID = ConfigurationManager.getBundle().getPropertyValue("mail.from");
        String to[] = ConfigurationManager.getBundle().getPropertyValue("mail.to").split(",");//change accordingly
        String cc[] = ConfigurationManager.getBundle().getPropertyValue("mail.cc").split(",");
        String bcc[] = ConfigurationManager.getBundle().getPropertyValue("mail.bcc").split(",");
        String emailSubject = ConfigurationManager.getBundle().getPropertyValue("mail.subject");

        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHostServer);
        Session session = Session.getInstance(props, null);
        try {
            MimeMessage msg = new MimeMessage(session);
            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress(emailID, emailID));
            msg.setText("Test Automation Exceution Results.", "UTF-8");
            msg.setSubject("Test Automation " + System.getProperty("application") + " Test Automation Report " + getDate());
            StringBuilder messagtosend = new StringBuilder();
            messagtosend.append("Hi,"
                    + "<br><br>Please Find Below the Test Automation Execution Report Summary."
                    + "<br><br><b><u>Execution Summary</u></b>");
            for (ISuite suite : suites) {
                messagtosend.append(
                        "<br>&nbsp;&nbsp;Test Suite Name:" + " " + suite.getName()
                        + "<br>&nbsp;&nbsp;Application :" + " " + System.getProperty("application")
                        + "<br>&nbsp;&nbsp;Operating System:" + System.getProperty("os.name")
                        + "<br>&nbsp;&nbsp;Browser:" + System.getProperty("browser")
                        + "<br>&nbsp;&nbsp;Environment:" + System.getProperty("environment")
                        + "<br>&nbsp;&nbsp;TestData Filter (Issuer Code):" + System.getProperty("includePattern")
                        + "<br>&nbsp;&nbsp;Test Start Time: " + ReporterUtil.getTestSuiteSummary(suite).get("TestStartTime")
                        + "<br>&nbsp;&nbsp;Test End Time: " + ReporterUtil.getTestSuiteSummary(suite).get("TestEndTime")
                        + "<br>&nbsp;&nbsp;Test Execution Time (hh:mm:ss): " + ReporterUtil.getTestSuiteSummary(suite).get("TestExecutionTime")
                        + "<br><br><b><u>Report Summary</u></b>"
                        + "<br>&nbsp;&nbsp;Total number of Test Cases executed:" + (pass + fail + skip)
                        + "<br>&nbsp;&nbsp;Passed:" + pass
                        + "<br>&nbsp;&nbsp;Failed:" + fail
                        + "<br>&nbsp;&nbsp;Skipped:" + skip);

                messagtosend.append("<br><br><b>**Note</b>:PFA for Detailed Report.");
            }
            // Now set the actual message
            messageBodyPart.setContent(messagtosend.toString(), "text/html; charset=utf-8");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            String filename = ApplicationProperties.EMAIL_EXECUTION_REPORT_DIR.getStringVal();
            File reportHtml = new File(filename);
            addAttachment(multipart, reportHtml.getCanonicalPath());

            // Send the complete message parts
            msg.setContent(multipart);

            msg.setSentDate(new Date());
            // Set To: header field of the header.
            for (int i = 0; i < to.length; i++) {
                if (!to[i].equals("")) {
                    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
                }
            }

            for (int i = 0; i < cc.length; i++) {
                if (!cc[i].equals("")) {
                    msg.addRecipient(Message.RecipientType.CC, new InternetAddress(cc[i]));
                }
            }

            for (int i = 0; i < bcc.length; i++) {
                if (!bcc[i].equals("")) {
                    msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc[i]));
                }
            }
            Log.LOGGER.info("Email sent :" + msg);
            Transport.send(msg);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.LOGGER.info("Error : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void addAttachment(Multipart multipart, String filename) {
        DataSource source = new FileDataSource(filename);
        BodyPart messageBodyPart = new MimeBodyPart();
        try {
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(getDate() + "_" + filename);
            multipart.addBodyPart(messageBodyPart);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    //time for email if needed
    public static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }

    //to get date if needed
    public static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date date = new Date();
        String DateForReport = dateFormat.format(date);
        return DateForReport;
    }
}
