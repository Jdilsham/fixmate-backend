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

import java.time.LocalDateTime;


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

    // send otp to new email
    public void sendEmailChangeOtp(String toEmail, String code) {
        Context ctx = new Context();
        ctx.setVariable("code", code);

        sendHtmlEmail(
                toEmail,
                "FixMate – Verify Your New Email",
                "email-change-otp",
                ctx
        );
    }

    // security alert
    public void sendEmailChangeAlert(String toEmail) {
        Context ctx = new Context();

        sendHtmlEmail(
                toEmail,
                "FixMate – Email Address Changed",
                "email-change-alert",
                ctx
        );
    }

    public void sendPasswordResetOtp(String toEmail, String name, String code) {
        Context ctx = new Context();
        ctx.setVariable("name", name);
        ctx.setVariable("code", code);

        sendHtmlEmail(
                toEmail,
                "FixMate – Password Reset Request",
                "password-reset",
                ctx
        );
    }


    // Booking confirmation email
    public void sendBookingConfirmationEmail(
            String toEmail,
            String customerName,
            String serviceTitle,
            String bookingId,
            LocalDateTime scheduledTime,
            String providerName,
            String providerPhone,
            String address
    ) {

        Context ctx = new Context();
        ctx.setVariable("name", customerName);
        ctx.setVariable("serviceTitle", serviceTitle);
        ctx.setVariable("bookingId", bookingId);
        ctx.setVariable("scheduledTime", scheduledTime);
        ctx.setVariable("providerName", providerName);
        ctx.setVariable("providerPhone", providerPhone);
        ctx.setVariable("address", address);

        ctx.setVariable(
                "bookingLink",
                "https://fixmate.app/bookings/" + bookingId
        );

        sendHtmlEmail(
                toEmail,
                "FixMate - Booking Confirmed ✅",
                "booking-confirmation",
                ctx
        );
    }

    //Send email to provider when got a booking

    public void sendProviderNewBookingEmail(
            String providerEmail,
            String providerName,
            String customerName,
            String serviceTitle,
            String bookingId,
            LocalDateTime scheduledTime,
            String address,
            String customerPhone
    ) {

        Context ctx = new Context();
        ctx.setVariable("providerName", providerName);
        ctx.setVariable("customerName", customerName);
        ctx.setVariable("serviceTitle", serviceTitle);
        ctx.setVariable("bookingId", bookingId);
        ctx.setVariable("scheduledTime", scheduledTime);
        ctx.setVariable("address", address);
        ctx.setVariable("customerPhone", customerPhone);

        sendHtmlEmail(
                providerEmail,
                "FixMate - New Booking Request 🔔",
                "provider-new-booking",
                ctx
        );
    }

    //booking accept by provider

    public void sendBookingAcceptedEmail(
            String toEmail,
            String name,
            String serviceTitle,
            String bookingId
    ) {

        Context ctx = new Context();
        ctx.setVariable("name", name);
        ctx.setVariable("serviceTitle", serviceTitle);
        ctx.setVariable("bookingId", bookingId);

        sendHtmlEmail(
                toEmail,
                "FixMate - Booking Accepted ✅",
                "booking-accepted",
                ctx
        );
    }

    //when provider reject booking

    public void sendBookingRejectedEmail(
            String toEmail,
            String name,
            String serviceTitle,
            String reason
    ) {

        Context ctx = new Context();
        ctx.setVariable("name", name);
        ctx.setVariable("serviceTitle", serviceTitle);
        ctx.setVariable("reason", reason);

        sendHtmlEmail(
                toEmail,
                "FixMate - Booking Rejected",
                "booking-rejected",
                ctx
        );
    }

    //when service completed

    public void sendServiceCompletedEmail(
            String toEmail,
            String name,
            String serviceTitle,
            String bookingId
    ) {

        Context ctx = new Context();
        ctx.setVariable("name", name);
        ctx.setVariable("serviceTitle", serviceTitle);
        ctx.setVariable("bookingId", bookingId);

        sendHtmlEmail(
                toEmail,
                "FixMate - Service Completed ⭐",
                "service-completed",
                ctx
        );
    }

}
