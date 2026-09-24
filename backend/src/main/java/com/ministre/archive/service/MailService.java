package com.ministre.archive.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MailService {

    private static final String RESEND_API_URL =
        "https://api.resend.com/emails";

    private final RestTemplate restTemplate =
            new RestTemplate();

    @Value("${RESEND_API_KEY}")
    private String resendApiKey;

    @Value("${RESEND_SENDER_EMAIL:onboarding@resend.dev}")
    private String senderEmail;

    public void envoyerCodeConfirmation(
            String email,
            String code) {

        String texte =
            "Bonjour,\n\n" +
            "Voici votre code de confirmation : " + code + "\n\n" +
            "Ce code expire dans 15 minutes.\n\n" +
            "Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.";

        envoyer(
            email,
            "Confirmation de votre compte - Archive Ministère",
            texte
        );
    }

    public void envoyerTokenReinitialisation(
            String email,
            String token) {

        String texte =
            "Bonjour,\n\n" +
            "Voici votre jeton de réinitialisation : " + token + "\n\n" +
            "Ce jeton expire dans 30 minutes.\n\n" +
            "Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.";

        envoyer(
            email,
            "Réinitialisation de mot de passe - Archive Ministère",
            texte
        );
    }

    private void envoyer(
            String destinataire,
            String sujet,
            String contenu) {

        HttpHeaders headers =
                new HttpHeaders();

        headers.setBearerAuth(resendApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body =
                new HashMap<>();

        body.put("from", senderEmail);
        body.put("to", new String[] { destinataire });
        body.put("subject", sujet);
        body.put("text", contenu);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        try {

            restTemplate.postForEntity(
                RESEND_API_URL,
                request,
                String.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                "Échec de l'envoi de l'email : "
                + e.getMessage()
            );
        }
    }
}