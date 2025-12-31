# CertValidator

**CertValidator** é uma ferramenta de linha de comando (CLI) em Java desenvolvida para auditar certificados SSL/TLS em grandes projetos ou sistemas de arquivos. Ela varre diretórios recursivamente em busca de Keystores (`.jks`, `.p12`) e Certificados (`.cer`, `.crt`, `.pem`), verifica a validade e gera um relatório HTML detalhado. O projeto inclui um cofre criptografado para senhas e sistema de alerta por e-mail.

## 🚀 Funcionalidades

* **Varredura Recursiva:** Busca profunda em diretórios por arquivos de certificado.
* **Suporte a Formatos:** Compatível com JKS, PKCS12 e certificados X.509.
* **Cofre Seguro (Vault):** Criptografa senhas de keystores usando AES-256 (GCM).
* **Fontes Flexíveis:** Carregue senhas de arquivos, variáveis de ambiente ou arquivos .properties.
* **Meta de Criptografia:** Comando Maven dedicado para criptografar senhas sem precisar escanear certificados.
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

## 🚀 Uso Avançado (Maven)

### Comando de Criptografia
Você pode gerar o arquivo `secrets.dat` isoladamente:
```bash
mvn certvalidator:encrypt -DMASTER_KEY=sua_chave
```

### Fontes de Senhas no POM
```xml
<configuration>
    <passwordsEnv>VARIAVEL_DE_AMB</passwordsEnv>
    <propertiesFile>caminho/para/arquivo.properties</propertiesFile>
    <directPasswords>
        <password>senha123</password>
    </directPasswords>
</configuration>
```

## ⚙️ Configuração (Variáveis de Ambiente)

| Variável | Descrição | Padrão |
| :--- | :--- | :--- |
| `MASTER_KEY` | **Obrigatório.** A chave para criptografar/descriptografar o cofre. | *(Nenhum)* |
| `SECRET_SOURCES` | Fontes de senhas (separadas por vírgula): `file,env,prop`. | `file` |
| `PASSWORDS_ENV` | Nome da variável de ambiente com senhas (se `env` ativo). | `CERT_PASSWORDS` |
| `PROPERTIES_FILE` | Caminho para arquivo .properties (se `prop` ativo). | *(Nenhum)* |
| `PROPERTIES_KEY` | Chave dentro do arquivo .properties. | `cert.passwords` |
| `SCAN_PATH` | Diretório raiz para iniciar a varredura. | `./` |
| `REPORT_PATH` | Caminho do arquivo para o relatório HTML gerado. | `.cert_reporter.html` |
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