package it.sara.demo.service.auth;


import it.sara.demo.exception.GenericException;
import it.sara.demo.service.util.JwtUtil;
import it.sara.demo.web.auth.dto.AuthenticationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servizio responsabile dell'autenticazione degli utenti e della generazione del token JWT.
 *
 * <p>Questa classe gestisce un semplice meccanismo di autenticazione basato su credenziali
 * configurate tramite properties Spring (username, password e ruolo). In caso di credenziali
 * valide, genera un token JWT tramite {@link JwtUtil}.</p>
 *
 * <p>Le configurazioni utilizzate sono:</p>
 * <ul>
 *     <li><b>login.username</b>: username autorizzato</li>
 *     <li><b>login.password</b>: password associata</li>
 *     <li><b>login.role</b>: ruolo assegnato all’utente autenticato</li>
 * </ul>
 *
 * <p>Il servizio è annotato con {@code @Service} per permettere l’iniezione automatica
 * nei controller che gestiscono l’autenticazione.</p>
 */
@Slf4j
@Service
public class AuthenticationService {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${login.username}")
    private String username;

    @Value("${login.password}")
    private String password;

    @Value("${login.role}")
    private String role;

    /**
     * Autentica un utente basandosi sulle credenziali fornite e genera un token JWT
     * in caso di autenticazione riuscita.
     *
     * <p>Il metodo esegue le seguenti operazioni:</p>
     * <ul>
     *     <li>Confronta le credenziali ricevute con quelle configurate nel sistema</li>
     *     <li>Se non corrispondono, lancia una {@link GenericException} con codice 401</li>
     *     <li>Se valide, crea un {@link AuthenticationResult}</li>
     *     <li>Genera un token JWT tramite {@link JwtUtil#generateToken(String, String)}</li>
     * </ul>
     *
     * @param username username fornito dal client
     * @param password password fornita dal client
     * @return il token JWT generato per l’utente autenticato
     * @throws GenericException se le credenziali non sono valide (HTTP 401)
     */
    public String authenticateAndGenerateToken(String username, String password) {

        if (!username.equals(this.username) || !password.equals(this.password)) {
            log.error("Invalid credentials");
            throw new GenericException(GenericException.UNAUTHORIZED);
        }

        AuthenticationResult result = new AuthenticationResult(username, role);

        return jwtUtil.generateToken(
            result.getUsername(),
            result.getRole()
        );
    }
}

