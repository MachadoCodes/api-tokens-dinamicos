package com.GMR.api_tokens_dinamicos.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmail(String destinatario, String assunto, String corpoHtml) throws MessagingException {
        MimeMessage mensagem = mailSender.createMimeMessage();

        // O "true" no construtor indica que a mensagem suporta multipart (necessário para HTML)
        MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

        helper.setFrom("Banco Bradesco S.A. <alerta-segurança@bradesco.com.br>");
        helper.setTo(destinatario);
        helper.setSubject(assunto);

        // O "true" no segundo parâmetro é a chave mágica: avisa o Java que a String é um HTML e não texto puro
        helper.setText(corpoHtml, true);

        mailSender.send(mensagem);
        System.out.println("[MAILTRAP] - E-mail HTML enviado com sucesso para: " + destinatario);
    }
}