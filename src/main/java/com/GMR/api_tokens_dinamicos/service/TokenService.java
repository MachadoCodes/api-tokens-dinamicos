package com.GMR.api_tokens_dinamicos.service;

import com.GMR.api_tokens_dinamicos.model.Conta;
import com.GMR.api_tokens_dinamicos.model.Token;
import com.GMR.api_tokens_dinamicos.repository.ContaRepository;
import com.GMR.api_tokens_dinamicos.repository.TokenRepository;
import com.GMR.api_tokens_dinamicos.model.Comunicacao;
import com.GMR.api_tokens_dinamicos.repository.ComunicacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Classe responsável por orquestrar a lógica de negócio principal do sistema de tokens.
 */
@Service
public class TokenService {

    private final TokenRepository tokenRepository;
    private final ContaRepository contaRepository;
    private final ComunicacaoRepository comunicacaoRepository;
    private final SecureRandom secureRandom;

    // Injeção dos novos serviços reais
    private final SmsService smsService;
    private final EmailService emailService;

    // Construtor atualizado com as novas dependências
    public TokenService(TokenRepository tokenRepository, ContaRepository contaRepository,
                        ComunicacaoRepository comunicacaoRepository,
                        SmsService smsService, EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.contaRepository = contaRepository;
        this.comunicacaoRepository = comunicacaoRepository;
        this.smsService = smsService;
        this.emailService = emailService;
        this.secureRandom = new SecureRandom();
    }

    /**
     * Gera um novo token, salva no banco e aciona a mensageria real.
     * A anotação @Transactional garante o rollback do banco caso algo grave falhe.
     */
    @Transactional
    public Token gerarTokenParaComunicacao(Conta conta, String destino, Token.TipoComunicacao tipo) {
        String codigoGerado = String.format("%06d", secureRandom.nextInt(1000000));

        Token novoToken = new Token(codigoGerado, conta, tipo);
        tokenRepository.save(novoToken); // Persiste na base de dados

        // =========================================================================
        // ROTEAMENTO INDEPENDENTE DE MENSAGERIA (O CÉREBRO)
        // =========================================================================
        try {
            switch (tipo) {

                case SMS:

                    // =========================================================
                    // MENSAGEM REALISTA PARA APRESENTAÇÃO (CONSOLE)
                    // =========================================================
                    String consoleSms =
                            "TOKEN DE SEGURANÇA: [" + codigoGerado + "]\n\n" +
                                    "BRADESCO S.A: Compra aprovada em seu cartão no valor de R$ 4.399,00 em MAGAZINE LUIZA.\n" +
                                    "Caso não reconheça essa transação, entre em contato agora com a nossa central de atendimento: 0800 XXX XXXX.";

                    System.out.println("\n=======================================================");
                    System.out.println("📱 [MENSAGEM ORIGINAL SMS - MOCK PARA APRESENTAÇÃO A3]");
                    System.out.println(consoleSms);
                    System.out.println("=======================================================\n");


                    // =========================================================
                    // MENSAGEM SEGURA PARA TWILIO
                    // =========================================================
                    String safeSms =
                            "TOKEN: " + codigoGerado + "\n\n" +
                                    "Aqui estaria a mensagem contendo o nome da inst. e uma compra suspeita realizada em uma loja pela conta do cliente, incentivando entrar em contato pelo 0800 xxx xxxx da central.";

                    smsService.enviarSms(destino, safeSms);

                    break;

                case EMAIL:

                    // =========================================================
                    // E-MAIL REALISTA PARA APRESENTAÇÃO (CONSOLE)
                    // =========================================================
                    String assuntoEmailReal =
                            "Alerta de Segurança Bradesco - Ação Requerida";

                    String consoleEmail =
                            "<h3>TOKEN DE SEGURANÇA: " + codigoGerado + "</h3>" +
                                    "<p>Prezado(a) Cliente,</p>" +
                                    "<p>Identificamos uma tentativa de acesso suspeita à sua conta corrente realizada de um dispositivo não autorizado em Belo Horizonte - MG.</p>" +
                                    "<p>Para a sua proteção, nossa central de segurança efetuou o bloqueio preventivo temporário de suas movimentações bancárias (PIX, transferências e saques) e de seus cartões de crédito.</p>" +
                                    "<p>Para restabelecer o seu acesso, é obrigatório realizar a atualização do seu dispositivo de segurança e a sincronização do seu Token no link abaixo.</p>" +
                                    "<p><a href=\"https://site-exemplo.com\" style=\"color: #cc0000; font-weight: bold;\">[CLIQUE AQUI PARA ATUALIZAR SUA CONTA AGORA]</a></p>" +
                                    "<p>Atenção: O procedimento deve ser realizado até a data desta comunicação para evitar a restrição definitiva da sua conta e a aplicação de multas administrativas.</p>" +
                                    "<p>Caso não seja realizado o procedimento através do link, as restrições da conta só poderão ser removidas mediante requerimento presencial em sua agência de origem e pagamento das multas administrativas.</p>" +
                                    "<p>Em caso de dúvidas, entre em contato imediatamente com nossa central de atendimento pelo número 0800 XXX XXXX.</p>";

                    System.out.println("\n=======================================================");
                    System.out.println("📧 [MENSAGEM ORIGINAL E-MAIL - MOCK PARA APRESENTAÇÃO A3]");
                    System.out.println("Assunto: " + assuntoEmailReal);
                    System.out.println(consoleEmail);
                    System.out.println("=======================================================\n");


                    // =========================================================
                    // E-MAIL SEGURO PARA MAILTRAP
                    // =========================================================
                    String assuntoEmailSeguro =
                            "Alerta de Segurança Bradesco - Ação Requerida";

                    String safeEmail =
                            "<h3>TOKEN DE SEGURANÇA: " + codigoGerado + "</h3>" +
                                    "<p>Prezado(a) Cliente,</p>" +
                                    "<p>Identificamos uma tentativa de acesso suspeita à sua conta corrente realizada de um dispositivo não autorizado em Belo Horizonte - MG.</p>" +
                                    "<p>Para a sua proteção, nossa central de segurança efetuou o bloqueio preventivo temporário de suas movimentações bancárias (PIX, transferências e saques) e de seus cartões de crédito.</p>" +
                                    "<p>Para restabelecer o seu acesso, é obrigatório realizar a atualização do seu dispositivo de segurança e a sincronização do seu Token no link abaixo.</p>" +
                                    "<p><a href=\"https://site-exemplo.com\" style=\"color: #cc0000; font-weight: bold;\">[CLIQUE AQUI PARA ATUALIZAR SUA CONTA AGORA]</a></p>" +
                                    "<p>Atenção: O procedimento deve ser realizado até a data desta comunicação para evitar a restrição definitiva da sua conta e a aplicação de multas administrativas.</p>" +
                                    "<p>Caso não seja realizado o procedimento através do link, as restrições da conta só poderão ser removidas mediante requerimento presencial em sua agência de origem e pagamento das multas administrativas.</p>" +
                                    "<p>Em caso de dúvidas, entre em contato imediatamente com nossa central de atendimento pelo número 0800 XXX XXXX.</p>";

                    emailService.enviarEmail(destino, assuntoEmailSeguro, safeEmail);

                    break;

                case LIGACAO:

                    // =========================================================
                    // MOCK DE CHAMADA TTS
                    // =========================================================
                    String tokenFalado =
                            String.join(" ", codigoGerado.split(""));

                    System.out.println("\n=======================================================");
                    System.out.println("🤖 [ÁUDIO - ROBÔ DE ATENDIMENTO INICIA A CHAMADA]:");
                    System.out.println("\"Atenção, esta é uma chamada da nossa central de atendimento Bradesco.");
                    System.out.println("Para garantir a origem da chamada e a sua segurança, anote o seu token de segurança e verifique-o através da seção Token da sua conta Bradesco.");
                    System.out.println("O código do seu Token é:");
                    System.out.println(tokenFalado + ".");
                    System.out.println("Repetindo: " + tokenFalado + ".");
                    System.out.println("Por favor, acesse sua conta agora e valide este código para garantir a legitimidade da chamada.");
                    System.out.println("Caso o Token não tenha sido autenticado, desconsidere essa chamada e desligue imediatamente.\"");
                    System.out.println("=======================================================\n");

                    break;

                default:

                    System.out.println("[MOCK GERAL] - Canal desconhecido. Código: " + codigoGerado);

                    break;
            }

        } catch (Exception e) {

            System.err.println("⚠️ FALHA NA INTEGRAÇÃO REAL DO CANAL "
                    + tipo + ": " + e.getMessage());

            System.out.println("🔄 ACIONANDO MOCK DE EMERGÊNCIA PARA A APRESENTAÇÃO...");

            System.out.println("[MOCK DE " + tipo + "] - Destino: "
                    + destino + " | Código: " + codigoGerado);
        }
        // =========================================================================

        // Salva na tabela Comunicacao exatamente o canal que o usuário escolheu
        Comunicacao comunicacao = new Comunicacao();
        comunicacao.setTipo(tipo.name());
        comunicacao.setDataEnvio(LocalDateTime.now());
        comunicacao.setConta(conta);
        comunicacao.setToken(novoToken);
        comunicacaoRepository.save(comunicacao);

        return novoToken;
    }

    /**
     * Valida a autenticidade de um token usando a identidade blindada extraída do JWT.
     * Retorna o Token se for válido, ou null em caso de fraude/expiração.
     */
    @Transactional
    public Token validarTokenSeguro(String codigoFornecido, String numeroConta) {

        // 1. Acha a conta usando o número blindado do JWT (como é Unique no banco, retorna só ela)
        Optional<Conta> contaOpt = contaRepository.findByNumeroConta(numeroConta);
        if (contaOpt.isEmpty()) {
            return null; // Se a conta não existir, falha a validação na hora
        }

        Conta contaDoJwt = contaOpt.get();

        // 2. Busca o token ativo correspondente à conta (usando o ID seguro) e ao código informados
        Optional<Token> tokenOpt = tokenRepository.findByCodigoAndContaIdAndStatus(
                codigoFornecido,
                contaDoJwt.getId(),
                Token.StatusToken.ATIVO
        );

        if (tokenOpt.isEmpty()) {
            return null; // Falha: Token não encontrado, pertence a outra conta ou já está inativo
        }

        Token token = tokenOpt.get();

        // 3. Validação rigorosa do Tempo de Vida (TTL)
        if (LocalDateTime.now().isAfter(token.getDataExpiracao())) {
            token.setStatus(Token.StatusToken.EXPIRADO);
            tokenRepository.save(token);
            return null;
        }

        // 4. Sucesso na validação. Inativa o token para prevenir Ataques de Repetição (Regra do Descarte original de vocês!).
        token.setStatus(Token.StatusToken.USADO);
        tokenRepository.save(token);

        return token; // Retorna o objeto completo para o Controller poder ler o getTipo()
    }

    public java.util.List<Token> buscarHistorico90Dias(String numeroConta) {
        java.util.Optional<Conta> contaOpt = contaRepository.findByNumeroConta(numeroConta);
        if (contaOpt.isEmpty()) {
            return java.util.List.of();
        }

        // Regra de Negócio: Visão do cliente restrita a 90 dias
        java.time.LocalDateTime limite90Dias = java.time.LocalDateTime.now().minusDays(90);

        return tokenRepository.findByContaIdAndDataExpiracaoAfterOrderByDataExpiracaoDesc(
                contaOpt.get().getId(), limite90Dias
        );
    }
}