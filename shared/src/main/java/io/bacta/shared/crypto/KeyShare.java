package io.bacta.shared.crypto;

import com.google.common.collect.EvictingQueue;
import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.ByteBufferWritable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * The KeyShare is a shared library that manages creating random keys, storing those keys, and encrypting/decrypting
 * special tokens of data.
 * <p>
 * Although using a static initialization vector with AES-128/CBC could lead to an attacker potentially being able to
 * guess at the contents of the encrypted data, that is of no consequence here. That is because we aren't encrypting
 * the contents to keep them secret. The process of being able to decrypt them on the receiving end indicates that the
 * required key is in possession. This means that the receiver can validate that the message came from a trusted source
 * as only a trusted source would also have the key to encrypt the details.
 * <p>
 * Do NOT use this library to encrypt secrets that you want to remain safe. It is not meant for that purpose.
 */
//TODO: Allow people to specify their own algorithms.
@Slf4j
public final class KeyShare {
    private static final int DEFAULT_KEY_COUNT = 2;

    private static final MessageDigest messageDigest;
    private static final KeyGenerator keyGenerator;
    private static final Cipher encryptCipher;
    private static final Cipher decryptCipher;
    private static final IvParameterSpec initializationVector = new IvParameterSpec(new byte[]{
            (byte) 0x10, (byte) 0xaf, (byte) 0x01, (byte) 0x8b,
            (byte) 0x88, (byte) 0x26, (byte) 0x65, (byte) 0x43,
            (byte) 0x1c, (byte) 0xc5, (byte) 0xf4, (byte) 0x8e,
            (byte) 0x15, (byte) 0x57, (byte) 0x9c, (byte) 0x9b
    });

    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            keyGenerator = KeyGenerator.getInstance("AES");
            encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (GeneralSecurityException ex) {
            LOGGER.error("Unable to initialize key share.", ex);
            throw new IllegalStateException("Unable to initialize.");
        }
    }

    private final EvictingQueue<Key> keys;

    public KeyShare() {
        this(DEFAULT_KEY_COUNT);
    }

    public KeyShare(int keyCount) {
        if (keyCount < 1)
            throw new IllegalArgumentException("KeyShare must be able to store at least one key.");

        this.keys = EvictingQueue.create(keyCount);
    }

    /**
     * Adds a new key to the front of the key collection. If the size of the collection is at maximum capacity, then
     * the last key will drop off.
     *
     * @param key The key to add to the collection of keys.
     */
    public void addKey(final Key key) {
        this.keys.add(key);
    }

    public Key getKey(int index) {
        int i = 0;
        for (final Key key : keys) {
            if (i == index)
                return key;
            ++i;
        }

        throw new IndexOutOfBoundsException();
    }

    public void clearKeys() {
        this.keys.clear();
    }

    public static Key generateKey() {
        final SecretKey secretKey = keyGenerator.generateKey();
        return new Key(secretKey.getEncoded());
    }

    /**
     * Deciphers a token into a byte representation of the original data.
     *
     * @param token The token to decipher.
     * @return A deciphered byte array.
     */
    public byte[] decipherToken(final Token token) {
        for (final Key key : keys) {
            try {
                final byte[] keyBytes = key.value;
                final SecretKey secretKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, keyGenerator.getAlgorithm());

                decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, initializationVector);
                final byte[] data = decryptCipher.doFinal(token.cipherData);

                final byte[] digest = messageDigest.digest(data);

                //If the digests are equal, then return the data because it must be right.
                if (Arrays.equals(digest, token.digest))
                    return data;

            } catch (GeneralSecurityException ex) {
                LOGGER.trace("Key did not produce a deciphered token.");
            }
        }

        throw new IllegalArgumentException("Unable to decipher key.");
    }

    /**
     * Ciphers data into a token that can be transmitted across the game network.
     *
     * @param data The data to cipher.
     * @return A ciphered token.
     */
    public Token cipherToken(final byte[] data) {
        try {
            if (keys.size() == 0)
                throw new IllegalStateException("No keys are stored in this key share. Cannot create a token.");

            final Key key = keys.peek(); //Get the first key on the stack.
            final byte[] keyBytes = key.value;

            //First, create a digest of the data.
            final byte[] digest = messageDigest.digest(data);

            final SecretKey secretKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, keyGenerator.getAlgorithm());

            //Next, we encrypt the data. Init the cipher first!
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, initializationVector);
            final byte[] cipherData = encryptCipher.doFinal(data);

            //Finally, produce a token, and return.
            return new Token(cipherData, digest);

        } catch (InvalidKeyException ex) {
            LOGGER.error("Encryption key was invalid.", ex);
            throw new IllegalArgumentException("encyrptionKey");
        } catch (GeneralSecurityException ex) {
            LOGGER.error("Data invalid.", ex);
            throw new IllegalArgumentException("data");
        }
    }

    /**
     * Immutable container for an encryption key's bytes that we can serialize to a message.
     */
    public static final class Key implements ByteBufferWritable {
        private final byte[] value;

        public Key(byte[] value) {
            this.value = value;
        }

        public Key(ByteBuffer buffer) {
            this.value = BufferUtil.getByteArray(buffer);
        }

        @Override
        public void writeToBuffer(ByteBuffer buffer) {
            BufferUtil.putByteArray(buffer, value);
        }

        public byte at(int i) {
            return value[i];
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static final class Token implements ByteBufferWritable {
        /**
         * The encrypted data.
         */
        private final byte[] cipherData;
        /**
         * The hash of the data before encryption. Used to validate the encryption succeeded.
         */
        private final byte[] digest;

        public Token(ByteBuffer buffer) {
            this.cipherData = BufferUtil.getByteArray(buffer);
            this.digest = BufferUtil.getByteArray(buffer);
        }

        @Override
        public void writeToBuffer(ByteBuffer buffer) {
            BufferUtil.putByteArray(buffer, cipherData);
            BufferUtil.putByteArray(buffer, digest);
        }
    }
}
