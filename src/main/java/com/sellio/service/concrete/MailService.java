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
        sendMail("templates/registration.html", "Xoş gəlmisiniz", to);
    }

    @Async
    public void sendAdminInvitationMail(String to, String token) {
        sendMail("templates/admin-invitation.html", "Admin Token", to);
    }

    @Async
    public void sendUpdateMail(String to) {
        sendMail("templates/update.html", "Hesabınız yeniləndi", to);
    }

    @Async
    public void sendDeleteMail(String to) {
        sendMail("templates/delete.html", "Hesabınız silindi", to);
    }

    @Async
    public void sendListingCreatedMail(String to) {
        sendMail("templates/listing-create.html", "Elan yaradıldı", to);
    }

    @Async
    public void sendListingUpdatedMail(String to) {
        sendMail("templates/listing-update.html", "Elan yeniləndi", to);
    }

    @Async
    public void sendListingExpiredMail(String to) {
        sendMail("templates/listing-deactivate.html", "Elan deaktiv olundu", to);
    }

    @Async
    public void sendListingActivatedMail(String to) {
        sendMail("templates/listing-activate.html", "Elan aktiv olundu", to);
    }

    private void sendMail(String path, String subject, String to) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            String htmlContent = Files.readString(Path.of(resource.getFile().getPath()), StandardCharsets.UTF_8);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new MailException("Mail exception: " + e.getMessage());
        }
    }
}
