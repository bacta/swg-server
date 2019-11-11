package io.bacta.engine.security;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import javax.crypto.KeyGenerator;
import java.io.IOException;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.Key;

public final class CryptoUtil {

    private CryptoUtil() {}

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

    /**
     * Helper method for converting a key to the PEM format for easy display.
     *
     * @param key The key to convert.
     * @return A string in the PEM format.
     * @throws IOException
     */
    public static String toPem(Key key) throws IOException {
        final StringWriter writer = new StringWriter();
        final JcaPEMWriter pemWriter = new JcaPEMWriter(writer);

        try {
            pemWriter.writeObject(key);

        } finally {
            pemWriter.close();
            writer.close();
        }

        return writer.toString();
    }
}
