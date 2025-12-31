package certValidator.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CertModelTest {

    @Test
    void testCertModel() {
        java.util.Date future = new java.util.Date(System.currentTimeMillis() + 1000000000L);
        CertModel model = new CertModel("path/file", "alias", "issuer", new java.util.Date(), future, "checksum");

        assertEquals("path/file", model.getFilePath());
        assertEquals("alias", model.getAlias());
        assertEquals("issuer", model.getIssuer());
        assertEquals("checksum", model.getChecksum());
        assertNotNull(model.getNotBefore());
        assertNotNull(model.getNotAfter());
        assertTrue(model.isValid());
    }

    @Test
    void testCertModelErrorConstructor() {
        CertModel model = new CertModel("path/to/bad", "ERROR: Invalid");
        assertEquals("path/to/bad", model.getFilePath());
        assertEquals("ERROR: Invalid", model.getError());
        assertEquals("ERRO", model.getAlias()); // Constructor sets alias to "ERRO"
        assertFalse(model.isValid());
    }

}
