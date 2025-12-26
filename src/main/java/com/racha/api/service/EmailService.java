package com.racha.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPasswordResetEmail(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Código de Recuperação de Senha - Rachai");
            message.setText(buildEmailBody(code));

            mailSender.send(message);
            log.info("Email de recuperação enviado para: {}", toEmail);
        } catch (Exception e) {
            log.error("Erro ao enviar email de recuperação para: {}", toEmail, e);
            throw new RuntimeException("Erro ao enviar email", e);
        }
    }

    private String buildEmailBody(String code) {
        return """
                Olá,
                
                Recebemos uma solicitação para redefinir sua senha no Rachai.
                
                Seu código de verificação é:
                
                %s
                
                Este código expira em 5 minutos.
                
                Se você não solicitou a redefinição de senha, ignore este email.
                
                Atenciosamente,
                Equipe Rachai
                """.formatted(code);
    }
}
