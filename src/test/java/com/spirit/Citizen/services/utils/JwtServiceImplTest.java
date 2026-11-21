package com.spirit.Citizen.services.utils;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {

    private static final String SECRET = Base64.getEncoder().encodeToString(new byte[32]);
    private static final String OTHER_SECRET = Base64.getEncoder().encodeToString("une-autre-cle-de-32-octets-mini!".getBytes());

    private final UserDetails alice = new User("alice@example.com", "x", List.of(new SimpleGrantedAuthority("USER")));
    private final UserDetails bob = new User("bob@example.com", "x", List.of(new SimpleGrantedAuthority("USER")));

    @Test
    void generatedTokenIsValidForItsOwner() {
        JwtServiceImpl jwt = new JwtServiceImpl(SECRET);
        String token = jwt.generateToken(alice);

        assertEquals("alice@example.com", jwt.extractUserName(token));
        assertEquals("USER", jwt.extractRole(token));
        assertTrue(jwt.isTokenValid(token, alice));
        assertFalse(jwt.isTokenValid(token, bob));
    }

    @Test
    void tokenSignedWithAnotherKeyIsRejected() {
        String forged = new JwtServiceImpl(OTHER_SECRET).generateToken(alice);

        assertThrows(io.jsonwebtoken.security.SignatureException.class,
                () -> new JwtServiceImpl(SECRET).extractUserName(forged));
    }

    @Test
    void missingSecretFailsFast() {
        assertThrows(IllegalStateException.class, () -> new JwtServiceImpl(""));
        assertThrows(IllegalStateException.class, () -> new JwtServiceImpl(null));
    }
}
