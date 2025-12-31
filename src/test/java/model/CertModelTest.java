package model;

import org.junit.jupiter.api.Test;

import certValidator.model.CertModel;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class CertModelTest {

    @Test
    void testValidCertificate() {
        Date futureDate = Date.from(Instant.now().plus(100, ChronoUnit.DAYS));

        CertModel model = new CertModel("path/fake.jks", "alias", "Issuer", new Date(), futureDate, "checksum123");

        assertTrue(model.isValid(), "O certificado deveria ser válido");
        assertEquals(null, model.getError());
        assertTrue(model.getDaysRemaining() >= 99);
    }

    @Test
    void testExpiringSoonCertificate() {
        Date nearFutureDate = Date.from(Instant.now().plus(20, ChronoUnit.DAYS));

        CertModel model = new CertModel("path/fake.jks", "alias", "Issuer", new Date(), nearFutureDate, "checksum123");

        assertTrue(model.isValid(), "O certificado ainda é tecnicamente válido");
        assertTrue(model.getDaysRemaining() <= 20);
        assertTrue(model.getDaysRemaining() >= 19);
    }

    @Test
    void testExpiredCertificate() {
        Date pastDate = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));

        CertModel model = new CertModel("path/fake.jks", "alias", "Issuer", new Date(), pastDate, "checksum123");

        assertFalse(model.isValid(), "O certificado deveria ser inválido/expirado");
        assertTrue(model.getDaysRemaining() < 0);
    }

    @Test
    void testErrorCertificate() {
        CertModel model = new CertModel("path/locked.jks", "Senha Incorreta");

        assertFalse(model.isValid());
        assertEquals("ERRO", model.getAlias());
        assertNotNull(model.getError());
    }
}
