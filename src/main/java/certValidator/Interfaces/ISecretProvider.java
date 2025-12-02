package certValidator.Interfaces;

import java.util.List;

public interface ISecretProvider {
    void initialize();
    List<String> getPasswords();
}
