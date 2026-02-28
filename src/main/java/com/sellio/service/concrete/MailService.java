package com.sellio.service.concrete;

import com.sellio.exception.custom.MailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendRegistrationMail(String to) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/registration.html");
            String htmlContent = Files.readString(Path.of(resource.getFile().getPath()), StandardCharsets.UTF_8);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Xoş gəlmisiniz");
            helper.setFrom(from);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new MailException("Mail exception: " + e.getMessage());
        }
    }

    public void sendAdminInvitationMail(String to, String token) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/admin-invitation.html");
            String htmlContent = Files.readString(Path.of(resource.getFile().getPath()), StandardCharsets.UTF_8);
            htmlContent = htmlContent.replace("${token}", token);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Admin Token");
            helper.setFrom(from);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new MailException("Mail exception: " + e.getMessage());
        }
    }
}
