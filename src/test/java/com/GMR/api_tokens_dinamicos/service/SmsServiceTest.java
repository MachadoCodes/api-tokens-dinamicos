package com.GMR.api_tokens_dinamicos.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmsServiceTest {

    @InjectMocks
    private SmsService smsService;

    @BeforeEach
    void setUp() {
        // Injeta as propriedades falsas do application.properties
        ReflectionTestUtils.setField(smsService, "accountSid", "FakeSid");
        ReflectionTestUtils.setField(smsService, "authToken", "FakeToken");
        ReflectionTestUtils.setField(smsService, "twilioPhoneNumber", "+123456789");
    }

    @Test
    @DisplayName("Deve inicializar a conexão estática do Twilio")
    void testInitTwilio() {
        try (MockedStatic<Twilio> mockedTwilio = Mockito.mockStatic(Twilio.class)) {
            smsService.initTwilio();
            mockedTwilio.verify(() -> Twilio.init("FakeSid", "FakeToken"));
        }
    }

    @Test
    @DisplayName("Deve simular o envio estático de SMS pelo Twilio")
    void testEnviarSms() {
        // Mocka a criação estática de mensagens do Twilio
        try (MockedStatic<Message> mockedMessage = Mockito.mockStatic(Message.class)) {
            MessageCreator creatorMock = mock(MessageCreator.class);
            Message messageMock = mock(Message.class);

            mockedMessage.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenReturn(creatorMock);
            when(creatorMock.create()).thenReturn(messageMock);
            when(messageMock.getSid()).thenReturn("SM_MOCK_SID_12345");

            smsService.enviarSms("+5511999999999", "Mensagem da A3");

            mockedMessage.verify(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()));
        }
    }
}