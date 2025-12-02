# CertValidator

**CertValidator** é uma ferramenta de linha de comando (CLI) em Java desenvolvida para auditar certificados SSL/TLS em grandes projetos ou sistemas de arquivos. Ela varre diretórios recursivamente em busca de Keystores (`.jks`, `.p12`) e Certificados (`.cer`, `.crt`, `.pem`), verifica a validade e gera um relatório HTML detalhado. O projeto inclui um cofre criptografado para senhas e sistema de alerta por e-mail.

## 🚀 Funcionalidades

* **Varredura Recursiva:** Busca profunda em diretórios por arquivos de certificado.
* **Suporte a Formatos:** Compatível com JKS, PKCS12 e certificados X.509.
* **Cofre Seguro (Vault):** Criptografa senhas de keystores usando AES-256 (GCM) através de uma chave mestra.
* **Relatório HTML:** Gera um relatório visual com o status de todos os certificados (Válido, Expirando, Expirado).
* **Alertas por E-mail:** Notifica administradores sobre certificados críticos.

## 🛠️ Pré-requisitos

* Java 21 ou superior
* Maven 3.x

## 🔐 Configuração de Segurança (O Cofre)

Esta aplicação utiliza um mecanismo de "Vault" para evitar manter senhas de keystore em texto plano.

1.  **Preparar Senhas:** Crie um arquivo chamado `passwords.txt` na raiz do projeto. Adicione as senhas dos seus keystores separadas por vírgula.
    * *Exemplo de conteúdo:* `changeit, minhasenha, 123456`
2.  **Definir Chave Mestra:** Defina a variável de ambiente `MASTER_KEY` (veja configuração abaixo).
3.  **Primeira Execução:** Ao rodar a aplicação, ela irá:
    * Ler o arquivo `passwords.txt`.
    * Criptografar o conteúdo usando a `MASTER_KEY`.
    * Salvar o resultado seguro em `secrets.dat`.
4.  **Limpeza:** Após a criação do arquivo `secrets.dat`, você pode deletar o `passwords.txt` para maior segurança.

## ⚙️ Configuração (Variáveis de Ambiente)

| Variável | Descrição | Padrão |
| :--- | :--- | :--- |
| `SCAN_PATH` | Diretório raiz para iniciar a varredura. | `./` |
| `REPORT_PATH` | Caminho do arquivo para o relatório HTML gerado. | `.cert_reporter.html` |
| `MASTER_KEY` | **Obrigatório.** A chave usada para criptografar/descriptografar o cofre. | *(Nenhum)* |
| `WARNING_DAYS` | Limite (em dias) para marcar um certificado como "atenção". | `30` |
| `SMTP_HOST` | Servidor SMTP para alertas de e-mail. | *(Vazio)* |
| `SMTP_PORT` | Porta SMTP. | `587` |
| `EMAIL_USER` | Usuário SMTP/Endereço de e-mail. | *(Vazio)* |
| `EMAIL_PASS` | Senha do SMTP. | *(Vazio)* |
| `EMAIL_TO` | E-mail do destinatário dos alertas. | *(Vazio)* |

> **Nota:** O envio de e-mail só é ativado se `SMTP_HOST` e `EMAIL_USER` estiverem definidos.

## 📦 Compilação e Execução

1.  **Compilar o projeto:**
    ```bash
    mvn clean package
    ```

2.  **Executar a aplicação:**
    * *Linux/macOS:*
        ```bash
        export MASTER_KEY="SuaChaveMestraSegura"
        java -jar target/certValidator-0.0.1-SNAPSHOT.jar
        ```
    * *Windows (PowerShell):*
        ```powershell
        $env:MASTER_KEY="SuaChaveMestraSegura"
        java -jar target/certValidator-0.0.1-SNAPSHOT.jar
        ```

## 📊 Resultado

Após a execução, abra o arquivo HTML gerado (padrão: `.cert_reporter.html`) no seu navegador para visualizar os resultados da auditoria.