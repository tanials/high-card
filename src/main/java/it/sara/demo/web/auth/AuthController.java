package it.sara.demo.web.auth;


import it.sara.demo.service.auth.AuthenticationService;
import it.sara.demo.web.auth.dto.LoginRequest;
import it.sara.demo.web.auth.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST per la gestione dell'autenticazione.
 * Espone un endpoint per il login, che accetta le credenziali dell'utente e restituisce un token di autenticazione se le credenziali sono valide.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Endpoint per il login dell'utente.
     *
     * @param request La richiesta contenente le credenziali dell'utente (username e password).
     * @return Una risposta con un token di autenticazione se le credenziali sono valide, altrimenti una risposta di errore.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        String token = authenticationService.authenticateAndGenerateToken(
            request.getUsername(),
            request.getPassword()
        );

        return ResponseEntity.ok(new LoginResponse(token));
    }
}
