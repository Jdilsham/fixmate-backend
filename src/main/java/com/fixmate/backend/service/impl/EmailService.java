package com.fixmate.backend.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private static final String FROM_EMAIL = "FixMate <app.fixmate@gmail.com>";

    @Async("emailExecutor")
    protected void sendHtmlEmail(String to, String subject, String template, Context context){
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String html = templateEngine.process(template, context);

            helper.setFrom(FROM_EMAIL);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);

        }catch (MessagingException e) {
            log.error("Email sending failed to {} : {}", to, e.getMessage());
        }
    }
    //send verification code
    public void sendVerificationCode(String toEmail, String code) {
        Context ctx = new Context();
        ctx.setVariable("code", code);

        sendHtmlEmail(
                toEmail,
                "Fixmate - Email Verification Code",
                "verification-code",
                ctx
        );

    }
    //welcome email
    public void sendWelcomeEmail(String toEmail, String userName) {
        Context ctx = new Context();
        ctx.setVariable("name", userName);

        sendHtmlEmail(
                toEmail,
                "Welcome to FixMate 🎉",
                "welcome-email",
                ctx
        );
    }
}
