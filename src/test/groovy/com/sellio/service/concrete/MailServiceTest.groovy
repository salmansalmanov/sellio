package com.sellio.service.concrete

import com.sellio.exception.custom.MailException
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import spock.lang.Specification
import spock.lang.Subject

class MailServiceTest extends Specification {

    def mailSender = Mock(JavaMailSender)

    @Subject
    def mailService = new MailService(mailSender)

    def setup() {
        mailService.from = "noreply@sellio.com"
    }

    def "should send registration mail successfully"() {
        given:
        def to = "user@example.com"
        def mimeMessage = Mock(MimeMessage)

        when:
        mailService.sendRegistrationMail(to)

        then:
        1 * mailSender.createMimeMessage() >> mimeMessage
        1 * mailSender.send(mimeMessage)
        notThrown(MailException)
    }

    def "should send admin invitation mail successfully"() {
        given:
        def to = "admin@sellio.com"
        def token = "secret-token"
        def mimeMessage = Mock(MimeMessage)

        when:
        mailService.sendAdminInvitationMail(to, token)

        then:
        1 * mailSender.createMimeMessage() >> mimeMessage
        1 * mailSender.send(mimeMessage)
        notThrown(MailException)
    }

    def "should throw MailException when email sending fails"() {
        given:
        def to = "fail@example.com"

        when:
        mailService.sendDeleteMail(to)

        then:
        1 * mailSender.createMimeMessage() >> { throw new RuntimeException("SMTP Server Down") }

        thrown(MailException)
    }

    def "should send listing status update mails"() {
        given:
        def to = "owner@example.com"
        def mimeMessage = Mock(MimeMessage)

        when:
        mailService.sendListingCreatedMail(to)
        mailService.sendListingExpiredMail(to)

        then:
        2 * mailSender.createMimeMessage() >> mimeMessage
        2 * mailSender.send(mimeMessage)
    }
}