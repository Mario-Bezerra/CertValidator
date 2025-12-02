# CertValidator

**CertValidator** is a robust Java CLI tool designed to audit SSL/TLS certificates across large projects or file systems. It recursively scans directories for Keystores (`.jks`, `.p12`) and Certificates (`.cer`, `.crt`, `.pem`), verifies their validity, and generates a comprehensive HTML report. It also includes an encrypted vault for keystore passwords and email alerting capabilities.

## 🚀 Features

* **Recursive Scanning:** deeply scans directories to find certificate files.
* **Format Support:** Supports JKS, PKCS12, X.509 certificates.
* **Secure Vault:** Encrypts keystore passwords using AES-256 (GCM) using a master key.
* **HTML Reporting:** Generates a styled status report of all certificates found.
* **Email Alerts:** Sends notifications for expired or expiring certificates.

## 🛠️ Prerequisites

* Java 21 or higher
* Maven 3.x

## 🔐 Security Setup (The Vault)

This application uses a "Vault" mechanism to avoid keeping plain-text passwords in memory or code.

1.  **Prepare Passwords:** Create a file named `passwords.txt` in the root directory. Add your keystore passwords separated by commas.
    * *Example content:* `changeit, mysecretpass, 123456`
2.  **Set Master Key:** Define the `MASTER_KEY` environment variable (see configuration below).
3.  **First Run:** When the application runs, it will:
    * Read `passwords.txt`.
    * Encrypt the content using the `MASTER_KEY`.
    * Save the result into `secrets.dat`.
4.  **Cleanup:** After the `secrets.dat` is created, you can safely delete `passwords.txt`.

## ⚙️ Configuration (Environment Variables)

| Variable | Description | Default |
| :--- | :--- | :--- |
| `SCAN_PATH` | The root directory to start scanning. | `./` |
| `REPORT_PATH` | File path for the output HTML report. | `.cert_reporter.html` |
| `MASTER_KEY` | **Required.** The key used to encrypt/decrypt the password vault. | *(None)* |
| `WARNING_DAYS` | Threshold (in days) to mark a cert as "warning". | `30` |
| `SMTP_HOST` | SMTP Server for email alerts. | *(Empty)* |
| `SMTP_PORT` | SMTP Port. | `587` |
| `EMAIL_USER` | SMTP Username/Email address. | *(Empty)* |
| `EMAIL_PASS` | SMTP Password. | *(Empty)* |
| `EMAIL_TO` | Recipient email address for alerts. | *(Empty)* |

> **Note:** Email alerts are only enabled if `SMTP_HOST` and `EMAIL_USER` are set.

## 📦 Build and Run

1.  **Build the project:**
    ```bash
    mvn clean package
    ```

2.  **Run the application:**
    * *Linux/macOS:*
        ```bash
        export MASTER_KEY="YourSecretMasterKey"
        java -jar target/certValidator-0.0.1-SNAPSHOT.jar
        ```
    * *Windows (PowerShell):*
        ```powershell
        $env:MASTER_KEY="YourSecretMasterKey"
        java -jar target/certValidator-0.0.1-SNAPSHOT.jar
        ```

## 📊 Output

After execution, open the generated HTML file (default: `.cert_reporter.html`) in your browser to view the audit results.