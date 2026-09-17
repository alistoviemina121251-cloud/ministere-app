package com.ministre.archive.service;

public interface EmailService {

    void sendConfirmationCode(
            String email,
            String code
    );

    void sendPasswordReset(
            String email,
            String token
    );
}