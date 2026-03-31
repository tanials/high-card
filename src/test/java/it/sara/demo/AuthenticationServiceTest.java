package it.sara.demo;

import it.sara.demo.exception.GenericException;
import it.sara.demo.service.auth.AuthenticationService;
import it.sara.demo.service.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authenticationService, "username", "admin");
        ReflectionTestUtils.setField(authenticationService, "password", "password");
        ReflectionTestUtils.setField(authenticationService, "role", "ADMIN");
    }

    @Test
    void shouldReturnTokenWhenCredentialsAreValid() {
        when(jwtUtil.generateToken("admin", "ADMIN"))
            .thenReturn("mocked-token");

        String token = authenticationService
            .authenticateAndGenerateToken("admin", "password");

        assertEquals("mocked-token", token);
        verify(jwtUtil).generateToken("admin", "ADMIN");
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsWrong() {
        assertThrows(GenericException.class,
            () -> authenticationService
                .authenticateAndGenerateToken("wrong", "password"));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsWrong() {
        assertThrows(GenericException.class,
            () -> authenticationService
                .authenticateAndGenerateToken("admin", "wrong"));
    }
}