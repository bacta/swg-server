package io.bacta.engine.security;

import javax.crypto.KeyGenerator;
import java.security.GeneralSecurityException;

public final class CryptoUtil {
    /**
     * Simple wrapper around {@link KeyGenerator#getInstance(String)} to replace the checked exception with a runtime
     * exception.
     *
     * @param algorithmName The name of the algorithm of the key generator.
     * @return A KeyGenerator instance if it exists.
     * @throws IllegalArgumentException If the algorithm does not exist on the current provider.
     */
    public static KeyGenerator getKeyGenerator(final String algorithmName) {
        try {
            return KeyGenerator.getInstance(algorithmName);
        } catch (GeneralSecurityException ex) {
            throw new IllegalArgumentException("algorithmName");
        }
    }
}
