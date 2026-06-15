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
        System.out.println("SIMULADOR DE TELA - MENSAGEM RECEBIDA VIA " + tipo.name());
        System.out.println("Para: " + destino);
        System.out.println("=======================================================");

        switch (tipo) {
            case SMS:
                System.out.println("TOKEN DE SEGURANÇA: [" + token + "]\n");
                System.out.println("BRADESCO S.A: Compra aprovada em seu cartão no valor de R$ 4.399,00 em MAGAZINE LUIZA.");
                System.out.println("Caso não reconheça essa transação, entre em contato agora com a nossa central de atendimento: 0800 XXX XXXX.");
                break;

            case EMAIL:
                System.out.println("TOKEN DE SEGURANÇA: [" + token + "]\n");
                System.out.println("Prezado(a) Cliente,");
                System.out.println("Identificamos uma tentativa de acesso suspeita à sua conta corrente realizada de um dispositivo não autorizado em Belo Horizonte - MG.");
                System.out.println("Para a sua proteção, nossa central de segurança efetuou o bloqueio preventivo temporário de suas movimentações bancárias (PIX, transferências e saques) e de seus cartões de crédito.");
                System.out.println("Para restabelecer o seu acesso, é obrigatório realizar a atualização do seu dispositivo de segurança e a sincronização do seu Token no link abaixo.");
                System.out.println("<a href=\"https://site-exemplo.com\">[CLIQUE AQUI PARA ATUALIZAR SUA CONTA AGORA]</a>");
                System.out.println("Atenção: O procedimento deve ser realizado até a data desta comunicação para evitar a restrição definitiva da sua conta e a aplicação de multas administrativas.");
                System.out.println("Caso não seja realizado o procedimento através do link, as restrições da conta só poderão ser removidas mediante requerimento presencial em sua agência de origem e pagamento das multas administrativas.");
                System.out.println("Em caso de dúvidas, entre em contato imediatamente com nossa central de atendimento pelo número 0800 XXX XXXX.");
                break;

            case LIGACAO:
                String tokenFalado = String.join(" ", token.split("")); // Separa os números com espaços: "3 5 7 1 4 8"
                System.out.println("🤖 [ÁUDIO - ROBÔ DE ATENDIMENTO INICIA A CHAMADA]:");
                System.out.println("\"Atenção, esta é uma chamada da nossa central de atendimento Bradesco.");
                System.out.println("Para garantir a origem da chamada e a sua segurança, anote o seu token de segurança e verifique-o através da seção Token da sua conta Bradesco.");
                System.out.println("O código do seu Token é:");
                System.out.println(tokenFalado + ".");
                System.out.println("Repetindo: " + tokenFalado + ".");
                System.out.println("Por favor, acesse sua conta agora e valide este código para garantir a legitimidade da chamada.");
                System.out.println("Caso o Token não tenha sido autenticado, desconsidere essa chamada e desligue imediatamente.\"");
                break;
        }

        System.out.println("=======================================================\n");
    }
}


/*
         System.out.println("\n=======================================================");
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
*/