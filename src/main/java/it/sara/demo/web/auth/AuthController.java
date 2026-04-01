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
 * Controller REST responsabile della gestione delle operazioni di autenticazione.
 *
 * <p>Espone endpoint relativi all’autenticazione utente, tra cui:</p>
 * <ul>
 *     <li>Login con username e password</li>
 *     <li>Generazione di un token JWT tramite {@link AuthenticationService}</li>
 * </ul>
 *
 * <p>Gli endpoint sono prefissati con <b>/auth</b>.</p>
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Endpoint REST per l’autenticazione di un utente tramite username e password.
     *
     * <p>Il metodo esegue le seguenti operazioni:</p>
     * <ul>
     *     <li>Riceve una richiesta {@link LoginRequest} contenente le credenziali</li>
     *     <li>Invoca {@link AuthenticationService#authenticateAndGenerateToken(String, String)}
     *         per validare le credenziali e generare un token JWT</li>
     *     <li>Restituisce il token all'interno di un {@link LoginResponse}</li>
     * </ul>
     *
     * @param request le credenziali dell’utente (username e password)
     * @return {@link ResponseEntity} contenente il token JWT in caso di autenticazione riuscita
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
