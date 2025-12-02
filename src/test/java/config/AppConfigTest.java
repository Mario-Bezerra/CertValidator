package config;

import certValidator.Config.AppConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {
    @Test
    void testDefaultValues() {
        AppConfig config = new AppConfig();
        assertEquals("./", config.getScanPath());
        assertEquals(".cert_reporter.html", config.getReportPath());
        assertEquals(30, config.getWarningDays());
        assertEquals(587, Integer.parseInt(config.getSmtpPort()));
    }

    @Test
    void testEmailEnabledLogic() {
        AppConfig config = new AppConfig();
        assertFalse(config.isEmailEnabled());
    }
    
    @Test
    void testAllGettersReturnDefaults() {
        AppConfig config = new AppConfig();
        
        assertEquals("./", config.getScanPath());
        assertEquals(".cert_reporter.html", config.getReportPath());
        assertEquals("", config.getMasterKey());
        assertEquals(30, config.getWarningDays());
        
        assertEquals("", config.getSmtpHost());
        assertEquals("587", config.getSmtpPort());
        assertEquals("", config.getEmailUser());
        assertEquals("", config.getEmailPass());
        assertEquals("", config.getEmailTo());
        
        assertFalse(config.isEmailEnabled());
    }
}