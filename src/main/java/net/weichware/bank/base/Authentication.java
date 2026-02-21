package net.weichware.bank.base;

import net.weichware.bank.database.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HexFormat;

public class Authentication {
    private static final Logger log = LoggerFactory.getLogger(Authentication.class);

    private static final HexFormat hexFormat = HexFormat.of();
    private final SecureRandom random = new SecureRandom();

    public static boolean isUserAuthenticated(User user, String password) {
        byte[] salt = hexFormat.parseHex(user.salt());
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return hexFormat.formatHex(hash).equals(user.hash());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Could not authenticate user {}", user, e);
        }
        return false;
    }

    public static HashAndSalt getHashAndSalt(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();

        return new HashAndSalt(HexFormat.of().formatHex(hash), HexFormat.of().formatHex(salt));
    }

    public record HashAndSalt(String hash, String salt) {
    }
}
