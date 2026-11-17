# CertValidator — SSL/TLS certificate checks for Maven projects

Java 21 Maven plugin that recursively scans directories for X.509 certificate and keystore files, detects invalid or expiring certificates, produces an HTML report and optionally sends notifications.

**Status:** Portfolio/development project. Its security and operational behavior has not been independently validated for production use.

## Implemented capabilities

- Recursive directory scanning with build-directory exclusions
- Parsers for X.509 files and JKS/PKCS12 keystores
- Configurable warning threshold and HTML report
- Optional SMTP alerts
- Password sources from environment variables, properties, files or direct configuration
- AES-GCM vault with PBKDF2 key derivation
- JUnit and Mockito tests

## Requirements and local installation

- Java 21 and Maven
- Plugin is not documented as published to Maven Central: install locally before using it in another project.

```bash
git clone https://github.com/Mario-Bezerra/CertValidator.git
cd CertValidator
mvn clean install
```

The Maven coordinates are **`io.github.mario-bezerra:certValidator:0.0.1`**, as defined in [`pom.xml`](pom.xml).

## Configure your Maven project

```xml
<build>
  <plugins>
    <plugin>
      <groupId>io.github.mario-bezerra</groupId>
      <artifactId>certValidator</artifactId>
      <version>0.0.1</version>
      <executions>
        <execution>
          <phase>verify</phase>
          <goals><goal>scan</goal></goals>
        </execution>
      </executions>
      <configuration>
        <scanPath>${project.basedir}</scanPath>
        <reportPath>${project.build.directory}/cert-report.html</reportPath>
        <warningDays>30</warningDays>
      </configuration>
    </plugin>
  </plugins>
</build>
```

With the plugin locally installed, execute `mvn verify` on the consuming project, or invoke the goal directly:

```bash
mvn io.github.mario-bezerra:certValidator:0.0.1:scan
```

The current scan goal **reports warnings but does not automatically fail a build** when certificates are expiring. See [`ScanMojo`](src/main/java/certValidator/mojo/ScanMojo.java).

## Configuration

| Plugin configuration | Default | Purpose |
| --- | --- | --- |
| `scanPath` | `${project.basedir}` | Directory to scan |
| `reportPath` | `${project.build.directory}/cert-report.html` | HTML report path |
| `warningDays` | `30` | Expiration warning threshold |
| `passwordsFile` | `passwords.txt` | Optional password input |
| `secretsFile` | `secrets.dat` | AES-GCM vault file |
| `passwordsEnv` | `CERT_PASSWORDS` | Environment password source |
| `propertiesFile` | unset | Optional password properties file |
| `propertiesKey` | `cert.passwords` | Key in properties file |
| `directPasswords` | unset | Maven-configured list |

Set the `MASTER_KEY` environment variable to use the encrypted vault. See [`EncryptMojo`](src/main/java/certValidator/mojo/EncryptMojo.java) and [`AppConfig`](src/main/java/certValidator/Config/AppConfig.java) for encryption and SMTP configuration.

## Testing and security notes

```bash
mvn test
```

Tests exist for scanning, parsing, reporting, secret providers and Maven goals; test execution has **not been independently verified** as part of this README update.

The repository contains test password/keystore fixtures. **Verify that every committed fixture is synthetic** before treating the repository as suitable for public sharing. Rotate any authentic credentials exposed in Git history. Never commit real master keys, private keys or production keystores.

## Next improvements

1. Validate the clean Maven build and add GitHub Actions CI.
2. Audit all committed secret fixtures and tighten `.gitignore`.
3. Add fail-on-expiration mode and tests for malformed files and failure scenarios.
4. Normalize package naming and document a safe end-to-end sample.

[Versão em português](README_pt-BR.md)
