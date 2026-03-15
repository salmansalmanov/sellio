package com.sellio.service.concrete;

import com.sellio.exception.custom.MailException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Async
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

    @Async
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

    @Async
    public void sendUpdateMail(String to) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/update.html");
            String htmlContent = Files.readString(Path.of(resource.getFile().getPath()), StandardCharsets.UTF_8);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Hesabınız yeniləndi");
            helper.setFrom(from);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new MailException("Mail exception: " + e.getMessage());
        }
    }

    @Async
    public void sendDeleteMail(String to) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/delete.html");
            String htmlContent = Files.readString(Path.of(resource.getFile().getPath()), StandardCharsets.UTF_8);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Hesabınız silindi");
            helper.setFrom(from);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new MailException("Mail exception: " + e.getMessage());
        }
    }

    @Async
    public void sendListingCreatedMail(String to) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/listing-create.html");
            String htmlContent = Files.readString(Path.of(resource.getFile().getPath()), StandardCharsets.UTF_8);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Elan yaradıldı");
            helper.setFrom(from);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new MailException("Mail exception: " + e.getMessage());
        }
    }

    @Async
    public void sendListingUpdatedMail(String to) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/listing-update.html");
            String htmlContent = Files.readString(Path.of(resource.getFile().getPath()), StandardCharsets.UTF_8);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Elan yeniləndi");
            helper.setFrom(from);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new MailException("Mail exception: " + e.getMessage());
        }
    }

    @Async
    public void sendListingExpiredMail(String to) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/listing-deactivate.html");
            String htmlContent = Files.readString(Path.of(resource.getFile().getPath()), StandardCharsets.UTF_8);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Elan deaktiv olundu");
            helper.setFrom(from);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new MailException("Mail exception: " + e.getMessage());
        }
    }
}
