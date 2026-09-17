package com.ministre.archive.service.impl;

import com.ministre.archive.service.EmailService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl
        implements EmailService {

    private final JavaMailSender mailSender;

    private final String from;

    private final String frontendUrl;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            @Value("${MAIL_USERNAME:}")
            String from,
            @Value("${FRONTEND_URL:http://localhost:4200}")
            String frontendUrl) {

        this.mailSender = mailSender;
        this.from = from;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void sendConfirmationCode(
            String email,
            String code) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email obligatoire."
            );
        }

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "Code de confirmation obligatoire."
            );
        }

        SimpleMailMessage message =
                new SimpleMailMessage();

        if (from != null && !from.isBlank()) {
            message.setFrom(from);
        }

        message.setTo(email);

        message.setSubject(
                "Confirmation de votre compte"
        );

        message.setText(
                "Bonjour,\n\n"
                        + "Votre code de confirmation est :\n\n"
                        + code
                        + "\n\n"
                        + "Ce code est valable pendant 15 minutes.\n\n"
                        + "Si vous n'êtes pas à l'origine "
                        + "de cette demande, ignorez cet email.\n\n"
                        + "Cordialement,\n"
                        + "Ministère"
        );

        try {

            mailSender.send(message);

        } catch (MailException e) {

            throw new RuntimeException(
                    "Impossible d'envoyer "
                            + "l'email de confirmation.",
                    e
            );
        }
    }

    @Override
    public void sendPasswordReset(
            String email,
            String token) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email obligatoire."
            );
        }

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "Token obligatoire."
            );
        }

        String resetLink =
                frontendUrl
                        + "/reset-password?email="
                        + email
                        + "&token="
                        + token;

        SimpleMailMessage message =
                new SimpleMailMessage();

        if (from != null && !from.isBlank()) {
            message.setFrom(from);
        }

        message.setTo(email);

        message.setSubject(
                "Réinitialisation de votre mot de passe"
        );

        message.setText(
                "Bonjour,\n\n"
                        + "Une demande de réinitialisation "
                        + "de votre mot de passe a été effectuée.\n\n"
                        + "Cliquez sur le lien suivant :\n"
                        + resetLink
                        + "\n\n"
                        + "Ce lien est valable pendant 30 minutes.\n\n"
                        + "Si vous n'êtes pas à l'origine "
                        + "de cette demande, ignorez cet email.\n\n"
                        + "Cordialement,\n"
                        + "Ministère"
        );

        try {

            mailSender.send(message);

        } catch (MailException e) {

            throw new RuntimeException(
                    "Impossible d'envoyer "
                            + "l'email de réinitialisation.",
                    e
            );
        }
    }
}