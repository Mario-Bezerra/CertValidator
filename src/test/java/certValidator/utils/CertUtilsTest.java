package certValidator.utils;

import certValidator.model.CertModel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.nio.file.Paths;
import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CertUtilsTest {

    @Test
    void testExtractMetadata() throws Exception {
        X509Certificate mockCert = Mockito.mock(X509Certificate.class);
        PublicKey mockKey = Mockito.mock(PublicKey.class);

        // CertUtils.extractMetadata uses getIssuerX500Principal().getName();
        when(mockCert.getIssuerX500Principal()).thenReturn(new X500Principal("CN=Issuer,OU=Test"));

        // It also calculates hash
        when(mockCert.getEncoded()).thenReturn(new byte[] { 1, 2, 3 });

        Date now = new Date();
        Date later = new Date(now.getTime() + 1000000000L);
        when(mockCert.getNotBefore()).thenReturn(now);
        when(mockCert.getNotAfter()).thenReturn(later);

        CertModel model = CertUtils.extractMetadata(mockCert, Paths.get("test.jks"), "alias1");

        assertNotNull(model);
        assertEquals("test.jks", model.getFilePath());
        assertEquals("alias1", model.getAlias());
        assertEquals("Issuer", model.getIssuer()); // Our mock helper splits by CN=
    }
}
