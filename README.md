<a id="readme-top"></a>

<div align="center">
  
<h1>Trust Token</h1>
<h6>Autenticação de comunicações bancárias através de tokens dinâmicos</h6>
</div>

***

<div align="center">
  
  [![Java](https://img.shields.io/badge/Java-21-%23F29111?logo=openjdk&logoColor=%23F29111)](https://docs.oracle.com/en/java/javase/21/index.html)
  [![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.13-%236DB33F?logo=springboot&logoColor=%236DB33F)](https://spring.io/projects/spring-boot#overview)
  [![Maven](https://img.shields.io/badge/Maven-3.9.14-%23C71A36?logo=apachemaven&logoColor=%23C71A36)](https://img.shields.io/badge/Maven-3.9.14-%23C71A36?logo=apachemaven&logoColor=%23C71A36)
  [![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-%23336791?logo=postgresql&logoColor=%23336791)](https://www.postgresql.org/)
  [![Render](https://img.shields.io/badge/Render--%23FFFFFF?logo=render)](https://render.com/)
  
</div>
<hr>

<div align="center">
  <a href="https://github.com/MachadoCodes/api-tokens-dinamicos/new/main?filename=README.md#readme-top">
    <img src="img/TrustTokenLogoV2.png" alt="Logo" width="800" height="800">
  </a>
</div>

<br>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Guia de conteúdo</summary>
  <ol>
    <li>
      <a href="#sobre-o-projeto">Sobre o Projeto</a>
      <ul>
        <li><a href="#contextualização">Contextualização</a></li>
        <li><a href="#a-proposta">A Proposta</a></li>
      </ul>
    </li>
    <li><a href="#uso">Uso</a></li>
    <li>
      <a href="#getting-started">Getting Started</a>
    </li>
    <li><a href="#tecnologias-utilizadas">Tecnologias Utilizadas</a></li>
    <li><a href="#arquitetura-do-projeto">Arquitetura do projeto</a></li>
    <li><a href="#guia-de-endpoints">Guia de Endpoints</a></li>
    <li><a href="#agradecimentos">Agradecimentos</a></li>
    <li><a href="#contato">Contato</a></li>
  </ol>
</details>

<br>

## Sobre o Projeto
<p>
  O projeto TrustToken é uma API REST desenvolvida para geração, vinculação e armazenamento de tokens dinâmicos associados a comunicações realizadas por instituições financeiras. A solução tem como objetivo mitigar fraudes baseadas em engenharia social, especialmente golpes como o de falsa comunicação ou falso funcionário, permitindo que o cliente verifique a autenticidade das mensagens recebidas. Por meio da validação de tokens únicos vinculados a cada comunicação, o sistema adiciona uma camada extra de segurança, aumentando a confiabilidade das interações entre clientes e instituições financeiras.
</p>

### Contextualização
<p>
  O aumento de fraudes financeiras baseadas em engenharia social representa um dos principais desafios de segurança no setor bancário. Criminosos utilizando da engenharia social exploram fatores como urgência e autoridade para manipular vítimas e obter informações sensíveis.
</p>
<p> 
  Segundo a Federação Brasileira de Bancos (FEBRABAN):
</p>
<ul>
  <li>Em 2024, o golpe da falsa central destacou-se como o mais aplicado contra a população idosa.</li>
  <li>Cerca de 105 mil pessoas já foram vítimas dessa modalidade de fraude no Brasil.</li>
</ul>

### A Proposta
<p>Nossa arquitetura propõe um fluxo de segurança baseada na geração de tokens dinâmicos associados a cada comunicação realizada pela instituição financeira. Nesse modelo, sempre que uma comunicação é enviada ou um contato é iniciado com o cliente, um código único e aleatório é gerado, vinculado àquela interação e armazenado em um histórico para autenticação ativa que possibilita ao usuário confirmar a autenticidade da comunicação antes de tomar qualquer ação, reduzindo o risco de exposição de dados sensíveis a fraudes.</p>

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

## Uso
<p>
<ol>
<li>A instituição financeira entra em contato ou envia uma mensagem ao cliente.</li>
  <br>
<li>O sistema gera um token único e aleatório para cada comunicação realizada.</li>
  <br>
<li>O token é vinculado à mensagem, caso seja SMS ou e-mail, e é armazenado no banco de dados.
   *Obs.: Em versões futuras, para comunicações via ligação, o token será informado ao cliente por meio de um sistema de TTS (Text-to-Speech) logo no início da chamada.</li>
  <br>
<li>O cliente recebe a comunicação, que aparenta ser de uma instituição financeira.</li>
  <br>
<li>A comunicação deve conter o token em destaque no topo da mensagem. Caso a mensagem recebida não contenha um token em destaque, o cliente pode considerá-la falsa e deve desconsiderá-la.</li>
  <br>
<li>De posse do token, o cliente acessa sua conta por meio do aplicativo ou página web oficial da instituição e navega até a seção de verificação de tokens.</li>
  <br>
<li>
  A seção de tokens apresenta o histórico de comunicações, incluindo:
<ul>
  <br>
  <li>código do token vinculado à mensagem;</li>
  <li>data e hora em que foi gerado;</li>
  <li>tipo de comunicação (SMS, e-mail ou ligação).</li>
</ul>
</li>
  <br>
<li>
  O cliente insere o token recebido em um campo para validação. O sistema verifica sua existência, vínculo com a conta e validade, retornando um dos seguintes resultados:
<ul>
  <br>
  <li>Comunicação autêntica, quando o token é válido. Nesse caso, a comunicação é legítima e proveniente da instituição financeira;</li>
  <li>Mensagem fraudulenta, quando o token é inválido ou inexistente. Nesse caso, a comunicação não é legítima e tem origem em agentes maliciosos. O cliente deve desconsiderar a mensagem, evitando possíveis tentativas de obtenção de informações sensíveis ou a realização de transações fraudulentas.</li>
</ul>
</li>
</ol>
</p>

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

## Getting started
<p>Siga estas instruções para configurar e executar o projeto em sua máquina local para fins de desenvolvimento e teste.</p>
<p>
  
### Pré-requisitos

<p>Antes de começar, você precisará ter instalado:</p>
<ul>
<li>Java JDK 21: O projeto utiliza recursos da versão 21 mais recentes do Java.</li>

<li>Maven 3.9.14: Gerenciador de dependências.</li>

<li>PostgreSQL: Banco de dados relacional para persistência (Local ou acesso ao Render).</li>

<li>Postman: Para realizar testes das chamadas aos endpoints da API.</li>

<li>IDE: IDE para a linguagem Java. Recomendamos a IntelliJ IDEA.</li>

<li>Sistema operacional: Estar utilizando o windows versão 8 ou superior.</li>
</ul>
</p>

***

<div>
  
  | Java JDK 21 |
  | ----------- |
  
</div>

<p>Antes de instalar, verifique se você já possui a versão necessária do java rodando na sua máquina:</p>

<ol>
<li>Abra o Prompt de Comando no Windows. Para isso, utilize o atalho <code>win+r</code> para abrir a caixa de diálogo Executar (Run).</li>

<li>Na caixa de diálogo digite <code>cmd</code> e dê um enter.</li>

<li>No prompt de  comando (cmd) digite o seguinte comando e aperte a tecla enter:</li>

 ```sh
   java --version
   ```
</ol>

<ul>  
<li>Caso apareça algo como "java 21.x.x", você já está utilizando a versão 21.</li>
<li>Caso apareça "'java' não é reconhecido como um comando interno ou externo, programa operável ou arquivo em lote.", você não possui o Java instalado.</li>
<li>Caso esteja com uma versão antiga do java será necessário atualizá-la.</li>
</ul>

***

<div>
  
  | Maven 3.9.14 |
  | ------------ |
  
</div>

<p>Antes de instalar, verifique se você já possui a versão necessária do maven rodando na sua máquina:</p>

<ol>
<li>Abra o Prompt de Comando no Windows. Para isso, utilize o atalho <code>win+r</code> para abrir a caixa de diálogo Executar (Run).</li>

<li>Na caixa de diálogo digite <code>cmd</code> e dê um enter.</li>

<li>No prompt de  comando (cmd) digite o seguinte comando e aperte a tecla enter:</li>

 ```sh
   mvn --version
   ```
</ol>

<ul>  
<li>Caso apareça algo como "Apache Maven 3.9.x", você já está utilizando a versão necessária.</li>
<li>Caso apareça "'Maven' não é reconhecido como um comando interno ou externo, programa operável ou arquivo em lote.", você não possui o Java instalado.</li>
<li>Caso esteja com uma versão antiga do Maven será necessário atualizá-la.</li>
</ul>


<p>
  Clone o repositório:
git clone https://github.com/MachadoCodes/api-tokens-dinamicos.git

Configure as credenciais do banco de dados no arquivo application.properties.

Execute o comando:
mvn spring-boot:run
</p>

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

## Tecnologias utilizadas

<div align="center">
  
| Tecnologia | Versão | Função |
| ---------- | ------ | ------ |
| Java | 21 | Linguagem Base |
| Spring Boot | 3.5.13 | Framework Backend |
| PostgreSQL | 18 | Banco de Dados |
| Maven  | 3.9.14 | Gerenciador de dependências e build |
| JPA / Hibernate | - | Persistência de dados e mapeamento objeto-relacional |

</div>

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

## Arquitetura do projeto

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

## Guia de Endpoints

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

## Agradecimentos

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

## Contato

<table align="center">
  <tr>
    <td align="center" valign="top">
      <a href="https://github.com/MachadoCodes">
        <img src="https://avatars.githubusercontent.com/u/142549072" width="200px;" alt="Renato Gonçalves Machado"/>
      </a>
      <br />
      <p><b>Renato Gonçalves Machado</b></p>
      <p>LinkedIn | <a href="https://github.com/MachadoCodes">GitHub</a></p>
    </td>
    <td align="center" valign="top">
      <a href="https://github.com/MiguelRebequi">
        <img src="https://avatars.githubusercontent.com/u/130229587" width="200px;" alt="Miguel Martinho Rebequi"/>
      </a>
      <br />
      <p><b>Miguel Martinho Rebequi</b></p>
      <p>LinkedIn | <a href="https://github.com/MiguelRebequi">GitHub</a></p>
    </td>
    <td align="center" valign="top">
      <a href="https://github.com/GuilhermeChiuchi">
        <img src="https://avatars.githubusercontent.com/u/136472706" width="200px;" alt="Guilherme Chiuchi Pereira"/>
      </a>
      <br />
      <p><b>Guilherme Chiuchi Pereira</b></p>
      <p>LinkedIn | <a href="https://github.com/GuilhermeChiuchi">GitHub</a></p>
    </td>
  </tr>
</table>

<br>

<p align="right">(<a href="#readme-top"> ▲ voltar ao topo ▲ </a>)</p>

