package com.muema.oauth21.services;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

import org.springframework.stereotype.Service;
import org.apache.commons.codec.binary.Base32;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;

@Service
public class TOTPService {

    private final TimeBasedOneTimePasswordGenerator totpGenerator; // TOTP generator instance
    private static final int SECRET_LENGTH = 16; // Length of the generated secret

    public TOTPService() throws NoSuchAlgorithmException {
        this.totpGenerator = new TimeBasedOneTimePasswordGenerator(); // Initialize the generator
    }

    /**
     * Generates a new Base32-encoded secret key for TOTP.
     *
     * @return Base32-encoded secret key
     */
    public String generateSecret() {
        SecureRandom random = new SecureRandom(); // Secure random number generator
        byte[] secret = new byte[SECRET_LENGTH]; // Array for secret
        random.nextBytes(secret); // Fill with random bytes
        Base32 base32 = new Base32(); // Base32 encoder
        return base32.encodeToString(secret); // Return encoded secret
    }

    /**
     * Generates a TOTP code for a given secret and time offset.
     *
     * @param base32Secret Base32-encoded secret key
     * @param timeOffset   Time offset in 30-second intervals
     * @return TOTP code as String
     * @throws Exception if TOTP code generation fails
     */
    public String getTOTPCode(String base32Secret, int timeOffset) throws Exception {
        Key key = createKeyFromSecret(base32Secret); // Create key from secret
        Instant instant = Instant.now().plusSeconds(timeOffset * 30); // Calculate time for TOTP
        return String.valueOf(totpGenerator.generateOneTimePassword(key, instant)); // Generate TOTP code
    }

    /**
     * Generates a TOTP code using the current time.
     *
     * @param base32Secret Base32-encoded secret key
     * @return TOTP code as String
     * @throws Exception if TOTP code generation fails
     */
    public String getTOTPCode(String base32Secret) throws Exception {
        return getTOTPCode(base32Secret, 0); // Use current time
    }

    /**
     * Validates the provided TOTP code against the expected code.
     *
     * @param base32Secret Base32-encoded secret key
     * @param code         TOTP code to validate
     * @return true if valid, false otherwise
     * @throws Exception if validation fails
     */
    public boolean validateOTP(String base32Secret, String code) throws Exception {
        // Check previous, current, and next TOTP codes for validity
        for (int i = -1; i <= 1; i++) {
            if (getTOTPCode(base32Secret, i).equals(code)) {
                return true; // Code is valid
            }
        }
        return false; // Code is invalid
    }

    /**
     * Creates a SecretKeySpec from the Base32-encoded secret.
     *
     * @param base32Secret Base32-encoded secret key
     * @return SecretKeySpec for HMAC-SHA1
     * @throws NoSuchAlgorithmException if the algorithm is not available
     */
    private SecretKeySpec createKeyFromSecret(String base32Secret) throws NoSuchAlgorithmException {
        Base32 base32 = new Base32(); // Base32 decoder
        byte[] secretBytes = base32.decode(base32Secret); // Decode the secret
        return new SecretKeySpec(secretBytes, "HmacSHA1"); // Return key spec
    }
}