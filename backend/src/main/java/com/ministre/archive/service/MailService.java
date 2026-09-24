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

    private static final String BREVO_API_URL =
        "https://api.brevo.com/v3/smtp/email";

    private final RestTemplate restTemplate =
            new RestTemplate();

    @Value("${BREVO_API_KEY}")
    private String brevoApiKey;

    @Value("${BREVO_SENDER_EMAIL}")
    private String senderEmail;

    @Value("${BREVO_SENDER_NAME:Archive Ministere}")
    private String senderName;

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

        headers.set("api-key", brevoApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/json");

        Map<String, Object> sender =
                new HashMap<>();

        sender.put("email", senderEmail);
        sender.put("name", senderName);

        Map<String, Object> to =
                new HashMap<>();

        to.put("email", destinataire);

        Map<String, Object> body =
                new HashMap<>();

        body.put("sender", sender);
        body.put("to", new Object[] { to });
        body.put("subject", sujet);
        body.put("textContent", contenu);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        try {

            restTemplate.postForEntity(
                BREVO_API_URL,
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