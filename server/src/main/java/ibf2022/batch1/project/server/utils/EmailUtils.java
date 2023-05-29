package ibf2022.batch1.project.server.utils;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import ibf2022.batch1.project.server.JWT.JwtFilter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailUtils {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    JwtFilter jwtFilter;

    @Value("${spring.mail.username}")
    private String EMAIL_ADDRESS;

    @Value("${frontendUrl}")
    private String FRONTEND_URL;

    public void sendEmailToAllAdmin(String status, String userEmail, List<String> allAdminEmail)
            throws UnsupportedEncodingException, MessagingException {
        allAdminEmail.remove(jwtFilter.getCurrentUser());

        if (status != null && status.equalsIgnoreCase("active")) {

            MimeMessage msg = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom(new InternetAddress(EMAIL_ADDRESS, "Cafe Magmt Support"));
            helper.setTo(jwtFilter.getCurrentUser());
            helper.setSubject("Account Approved");

            String htmlMsgContent = "<p><b>USER: - </b>" + userEmail
                    + " <br>has been APPROVED by"
                    + " <br><b>ADMIN: - </b>" + jwtFilter.getCurrentUser() + "</p>";

            msg.setContent(htmlMsgContent, "text/html");

            if (allAdminEmail != null && allAdminEmail.size() > 0) {
                helper.setCc(getCcArray(allAdminEmail));
            }

            emailSender.send(msg);

        } else {

            MimeMessage msg = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom(new InternetAddress(EMAIL_ADDRESS, "Cafe Magmt Support"));
            helper.setTo(jwtFilter.getCurrentUser());
            helper.setSubject("Account Disabled");

            String htmlMsgContent = "<p><b>USER: - </b>" + userEmail
                    + " <br>has been DISABLED by"
                    + " <br><b>ADMIN: - </b>" + jwtFilter.getCurrentUser() + "</p>";

            msg.setContent(htmlMsgContent, "text/html");

            if (allAdminEmail != null && allAdminEmail.size() > 0) {
                helper.setCc(getCcArray(allAdminEmail));
            }

            emailSender.send(msg);
        }
    }

    private String[] getCcArray(List<String> ccList) {
        String[] cc = new String[ccList.size()];

        for (int i = 0; i < ccList.size(); i++) {
            cc[i] = ccList.get(i);
        }
        return cc;
    }

    public void resetPasswordMail(String to, String subject, String token)
            throws MessagingException, UnsupportedEncodingException {

        MimeMessage msg = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(msg, true);

        helper.setFrom(new InternetAddress(EMAIL_ADDRESS, "Cafe Magmt Support"));
        helper.setTo(to);
        helper.setSubject(subject);

        // TODO: note to change the anchor link for forgotPassword email
        String htmlMsgContent = "<p><b>You have requested to reset your password to Cafe Management System</b>"
                + "<p><a href=\"" + FRONTEND_URL + "/resetPassword?token=" + token
                + "\">Click here to reset your password</a></p>"
                + "<p>If you do remember your password or have not made this request, you can ignore this email.</p>";

        msg.setContent(htmlMsgContent, "text/html");

        emailSender.send(msg);

        log.info("Inside resetPasswordMail - msg: {}", msg);

    }
}
