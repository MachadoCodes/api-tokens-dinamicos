package com.GMR.api_tokens_dinamicos.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    // Inicia a conexão com o Twilio assim que o Spring Boot subir
    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    public void enviarSms(String numeroDestino, String mensagemCompleta) {
        // Lembre-se: Na conta Trial do Twilio, o numeroDestino precisa ser o SEU celular cadastrado lá
        Message message = Message.creator(
                new PhoneNumber(numeroDestino), // Para onde vai (ex: +5511999999999)
                new PhoneNumber(twilioPhoneNumber), // De onde vem (seu número do Twilio)
                mensagemCompleta // A mensagem agora vem 100% pronta do TokenService
        ).create();

        System.out.println("[TWILIO] - SMS enviado! SID: " + message.getSid());
    }
}