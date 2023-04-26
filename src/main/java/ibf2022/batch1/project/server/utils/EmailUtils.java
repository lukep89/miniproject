package ibf2022.batch1.project.server.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailUtils {

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessgae(String to, String subject, String text, List<String> list) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("${spring.mail.username}");
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);

        if (list != null && list.size() > 0) {
            msg.setCc(getCcArray(list));
        }

        log.info(">>>> Inside sendSimpleMessgae - msg: {}", msg);

        emailSender.send(msg);

    }

    private String[] getCcArray(List<String> ccList) {
        String[] cc = new String[ccList.size()];

        for (int i = 0; i < ccList.size(); i++) {
            cc[i] = ccList.get(i);
        }
        return cc;
    }

    public void forgotPasswordMail(String to, String subject, String password) throws MessagingException {

        MimeMessage msg = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(msg, true);

        helper.setFrom("${spring.mail.username}");
        helper.setTo(to);
        helper.setSubject(subject);

        String htmlMsgContent = "<p><b>Your Login details for Cafe Management System</b><br><b>Email: </b> " + to
                + " <br><b>Password: </b> " + password
                + "<br><a href=\"http://localhost:4200/\">Click here to login</a></p>";

        msg.setContent(htmlMsgContent, "text/html");

        emailSender.send(msg);

    }

}
