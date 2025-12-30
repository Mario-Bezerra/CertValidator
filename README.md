# CertValidator Maven Plugin

**CertValidator** is a Maven Plugin designed to audit SSL/TLS certificates within your project structure. It recursively scans directories for Keystores (`.jks`, `.p12`) and Certificates (`.cer`, `.crt`, `.pem`), verifies their validity, and generates a comprehensive HTML report. It helps prevent production outages caused by expired certificates.

## 🚀 Features

* **Recursive Scanning:** Smartly scans directories, automatically ignoring standard framework folders (`node_modules`, `target`, `.git`, etc.).
* **Format Support:** Supports JKS, PKCS12, X.509 certificates.
* **Secure Vault:** Encrypts keystore passwords using AES-256 (GCM).
* **HTML Reporting:** Generates a modern, styled audit report.
* **Email Alerts:** Can send notifications for expired or expiring certificates (configurable).

## 🔐 Installation (Local)

Since this plugin is not yet published to Maven Central, you must install it to your local repository:

```bash
git clone https://github.com/Mario-Bezerra/CertValidator.git
cd CertValidator
mvn clean install
```

## Usage

### 1. As a Maven Plugin (Recommended)

Add the plugin to your project's `pom.xml` in the `<build><plugins>` section.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>cert.validator</groupId>
            <artifactId>certValidator</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <executions>
                <execution>
                    <phase>verify</phase> <!-- Runs automatically during verify phase -->
                    <goals>
                        <goal>scan</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <!-- Optional Configuration -->
                <scanPath>${project.basedir}</scanPath>
                <reportPath>${project.build.directory}/cert-report.html</reportPath>
                <warningDays>30</warningDays>
                <passwordsFile>secrets/passwords.txt</passwordsFile>
                
                <!-- Simple Email Alerting (Optional) -->
                <!-- Ideally configured via Environment Variables for security -->
            </configuration>
        </plugin>
    </plugins>
</build>
```

Run it manually:
```bash
mvn certvalidator:scan
```

### 2. As a Dependency (Java Library)

If you want to use the scanning logic programmatically in your own Java application:

```xml
<dependency>
    <groupId>cert.validator</groupId>
    <artifactId>certValidator</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

**Example:**
```java
import certValidator.Scanner.ScannerService;
import certValidator.Model.CertModel;
import java.util.List;

// ...
ScannerService scanner = new ScannerService(passwords, parsers);
List<CertModel> results = scanner.scan("/path/to/scan");
```

## 🔐 Security & Configuration

### Environment Variables
For security (especially for Vault Master Key and Email Credentials), use environment variables:

| Variable | Description | Default |
| :--- | :--- | :--- |
| `MASTER_KEY` | **Required** for Vault. The key used to encrypt/decrypt passwords. | *(None)* |
| `SMTP_HOST` | SMTP Server for alerts. | *(Empty)* |
| `SMTP_PORT` | SMTP Port. | `587` |
| `EMAIL_USER` | SMTP Username. | *(Empty)* |
| `EMAIL_PASS` | SMTP Password. | *(Empty)* |
| `EMAIL_TO` | Recipient email. | *(Empty)* |

### Password Vault
1.  Create a `passwords.txt` with comma-separated passwords: `changeit, secret123`
2.  Run the plugin. It will encrypt these into `secrets.dat` using `MASTER_KEY`.
3.  Delete `passwords.txt` after `secrets.dat` is generated.
