package com.muema.oauth21.controllers;

import com.muema.oauth21.model.Client;
import com.muema.oauth21.services.ClientService;
import com.muema.oauth21.services.TOTPService;
import com.muema.oauth21.util.JwtTokenService;
import com.muema.oauth21.util.PKCEUtils;
import com.muema.oauth21.util.QRCodeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ClientService clientService;
    private final TOTPService totpService;
    private final JwtTokenService jwtTokenService;


    public AuthController(ClientService clientService, TOTPService totpService, JwtTokenService jwtTokenService) {
        this.clientService = clientService;
        this.totpService = totpService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<Client> register(@RequestBody Client client) {
        try{
        Client savedClient = clientService.saveClient(client);
        return ResponseEntity.ok(savedClient);
        }catch (NoSuchAlgorithmException e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String clientId, @RequestParam boolean enableMFA) {
        Optional<Client> clientOptional = clientService.findClientById(clientId);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();

            // Check if MFA is enabled and if mfaSecret is null
            if (enableMFA) {
                if (client.getMfaSecret() == null) {
                    // Set up MFA for the first time
                    return ResponseEntity.ok("MFA setup required. Please scan the QR code at /auth/setup-mfa?clientId=" + clientId);
                } else {
                    // Proceed to MFA verification
                    return ResponseEntity.ok("Login successful. Proceed to /auth/verify-mfa");
                }
            }
            // Normal login process (without MFA)
            return ResponseEntity.ok("Login successful. No MFA required.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
    }
    @GetMapping("/setup-mfa")
    public ResponseEntity<byte[]> setupMFA(@RequestParam String clientId) throws NoSuchAlgorithmException {
        Optional<Client> clientOptional = clientService.findClientById(clientId);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            String secret = totpService.generateSecret();
            client.setMfaSecret(secret);
            clientService.updateClient(client);

            String issuer = "PKCE + MFA";
            String otpAuthUrl = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                    issuer, clientId, secret, issuer);

            try {
                byte[] qrCode = QRCodeUtils.generateQRCode(otpAuthUrl);
                return ResponseEntity.ok()
                        .header("Content-Type", "image/png")
                        .body(qrCode);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/verify-mfa")
    public ResponseEntity<String> verifyMFA(@RequestParam String clientId, @RequestParam String code) throws Exception {
        Optional<Client> clientOptional = clientService.findClientById(clientId);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            boolean isValid = totpService.validateOTP(client.getMfaSecret(), code);
            if (isValid) {
                String authorizationCode = java.util.UUID.randomUUID().toString();
                client.setAuthorizationCode(authorizationCode);
                clientService.updateClient(client);
                return ResponseEntity.ok(authorizationCode);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid MFA code");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
    }

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestParam String clientId,
                                           @RequestParam String codeVerifier,
                                           @RequestParam String authorizationCode) throws NoSuchAlgorithmException {
        Optional<Client> clientOptional = clientService.findClientById(clientId);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            if (authorizationCode.equals(client.getAuthorizationCode())) {
                if (PKCEUtils.verifyCodeVerifier(client.getCodeChallenge(), codeVerifier)) {
                    return ResponseEntity.ok(jwtTokenService.generateToken(clientId));
                }
                return ResponseEntity.badRequest().body("Invalid code verifier");
            }
            return ResponseEntity.badRequest().body("Invalid authorization code");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
    }
}