package com.GMR.api_tokens_dinamicos.service;

import com.GMR.api_tokens_dinamicos.model.Token;
import org.springframework.stereotype.Service;

/**
 * Implementação simulada (Mock) do serviço de mensageria para o ambiente de desenvolvimento.
 * Imprime os templates de fraude adaptados para a solução no console da IDE.
 */
@Service
public class MockMensageriaService implements MensageriaService {

    // Cenários de fraude reais utilizados para as apresentações acadêmicas
    @Override
    public void enviarComunicacao(String destino, String token, Token.TipoComunicacao tipo) {

        System.out.println("\n=======================================================");
        System.out.println("SIMULADOR DE TELA - MENSAGEM RECEBIDA VIA " + tipo);
        System.out.println("Para: " + destino);
        System.out.println("=======================================================");
        System.out.println("TOKEN DE SEGURANÇA: [" + token + "]\n");
        System.out.println("BRADESCO: Compra em analise no valor de R$ 5.500,00");
        System.out.println("em MAGAZINE LUIZA. ");
        System.out.println("Caso nao reconheça, ligue agora para 0800 XXX XXXX.");
        System.out.println("=======================================================\n");

        // System.out.println("\n=======================================================");
//        System.out.println("SIMULADOR DE TELA - MENSAGEM RECEBIDA VIA " + tipo);
//        System.out.println("Para: " + destino);
//        System.out.println("=======================================================");
//        System.out.println("TOKEN DE SEGURANÇA: [" + token + "]\n");
//        System.out.println("Seus pontos Livelo no valor de R$ 1.840,00 expiram hoje.");
//        System.out.println("Para não perdê-los entre em contato através da nossa central");
//        System.out.println("pelo número 0800 XXX XXXX.");
//        System.out.println("=======================================================\n");
    }
}