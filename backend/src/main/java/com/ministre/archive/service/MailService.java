package com.ministre.archive.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void envoyerCodeConfirmation(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(email);
        message.setSubject("Confirmation de votre compte - Archive Ministère");
        message.setText(
            "Bonjour,\n\n" +
            "Voici votre code de confirmation : " + code + "\n\n" +
            "Ce code expire dans 15 minutes.\n\n" +
            "Si vous n'êtes pas à l'origine de cette demande, ignorez cet email."
        );
        mailSender.send(message);
    }

    public void envoyerTokenReinitialisation(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(email);
        message.setSubject("Réinitialisation de mot de passe - Archive Ministère");
        message.setText(
            "Bonjour,\n\n" +
            "Voici votre jeton de réinitialisation : " + token + "\n\n" +
            "Ce jeton expire dans 30 minutes.\n\n" +
            "Si vous n'êtes pas à l'origine de cette demande, ignorez cet email."
        );
        mailSender.send(message);
    }
}