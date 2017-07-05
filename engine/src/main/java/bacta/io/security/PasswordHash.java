package bacta.io.security;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface PasswordHash {

	String createHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException;

	boolean validatePassword(String password, String correctHash) throws NoSuchAlgorithmException, InvalidKeySpecException;
}
