package com.flawlesscoders.ambigu.utils.email;

import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

        public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> templateModel) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            Context context = new Context();
            context.setVariables(templateModel);
            String htmlContent = templateEngine.process(templateName, context);
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            helper.addInline("logo", new ClassPathResource("static/images/ambigu-icon.png"));
            helper.addInline("clock", new ClassPathResource("static/images/clock.png"));
            
            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending HTML email", e);
        }
    }
}
