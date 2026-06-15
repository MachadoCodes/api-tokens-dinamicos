package com.GMR.api_tokens_dinamicos;

import com.GMR.api_tokens_dinamicos.repository.*;
import com.GMR.api_tokens_dinamicos.security.JwtUtil;
import com.GMR.api_tokens_dinamicos.service.EmailService;
import com.GMR.api_tokens_dinamicos.service.SmsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
		// Desliga a tentativa de conectar ao banco de dados real
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration, org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
class ApiTokensDinamicosApplicationTests {

	// 1. Mocks da camada de Banco de Dados
	@MockitoBean private ContaRepository contaRepository;
	@MockitoBean private UsuarioRepository usuarioRepository;
	@MockitoBean private TokenRepository tokenRepository;
	@MockitoBean private ComunicacaoRepository comunicacaoRepository;
	@MockitoBean private CredencialRepository credencialRepository;

	// 2. Mocks da camada Externa e Variáveis de Ambiente (A MÁGICA ACONTECE AQUI!)
	// O Spring não vai tentar ler o ${TWILIO_SID} ou o ${JWT_SECRET} porque os serviços que usam isso foram "dublados".
	@MockitoBean private SmsService smsService;
	@MockitoBean private EmailService emailService;
	@MockitoBean private JwtUtil jwtUtil;

	@Test
	void contextLoads() {
		// Sucesso absoluto! A árvore de dependências da API está impecável.
	}
}