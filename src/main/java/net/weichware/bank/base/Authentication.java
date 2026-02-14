package net.weichware.bank.base;

import net.weichware.bank.database.entities.User;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HexFormat;

public class Authentication {
    private final SecureRandom random = new SecureRandom();
    private static final HexFormat hexFormat = HexFormat.of();

    public static boolean isUserAuthenticated(User user, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = hexFormat.parseHex(user.salt());
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return hexFormat.formatHex(hash).equals(user.hash());
    }
}
