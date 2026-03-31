package it.sara.demo.service.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Utility per la generazione e la validazione di JSON Web Token (JWT).
 *
 * <p>Questa classe fornisce metodi per:</p>
 * <ul>
 *     <li>Generare un token JWT firmato tramite secret key HMAC</li>
 *     <li>Validare e decodificare un token JWT</li>
 * </ul>
 *
 * <p>La configurazione viene fornita tramite properties Spring:</p>
 * <ul>
 *     <li><b>jwt.secret</b>: chiave segreta utilizzata per firmare il token</li>
 *     <li><b>jwt.expiration</b>: durata del token in minuti</li>
 *     <li><b>jwt.issuer</b>: identificatore dell’emittente del token</li>
 * </ul>
 *
 * <p>È annotata con {@code @Component} per permettere l’iniezione automatica nei servizi.</p>
 */

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final int expirationMinutes;
    private final String issuer;


    /**
     * Costruisce un'istanza di {@link JwtUtil} inizializzando i parametri
     * necessari alla generazione e validazione dei token JWT.
     *
     * @param secret la chiave segreta utilizzata per firmare i token
     * @param expirationMinutes la durata di validità dei token, espressa in minuti
     * @param issuer l'identificatore dell’emittente che sarà incluso e richiesto nei token
     */

    public JwtUtil(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration}") int expirationMinutes,
        @Value("${jwt.issuer}") String issuer
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMinutes = expirationMinutes;
        this.issuer = issuer;
    }


    /**
     * Genera un token JWT firmato che include:
     * <ul>
     *     <li>il nome utente come "subject"</li>
     *     <li>il ruolo dell’utente come claim aggiuntivo</li>
     *     <li>l’emittente definita nella configurazione</li>
     *     <li>la data di emissione</li>
     *     <li>la data di scadenza calcolata dinamicamente</li>
     * </ul>
     *
     * @param username il nome utente da inserire nel token
     * @param role il ruolo associato all’utente, salvato nel claim "role"
     * @return una stringa contenente il token JWT generato
     */

    public String generateToken(String username, String role) {

        Instant expiry = Instant.now().plus(Duration.ofMinutes(expirationMinutes));

        return Jwts.builder()
            .subject(username)
            .issuer(issuer)
            .issuedAt(Date.from(Instant.now()))
            .claim("role", role)
            .expiration(Date.from(expiry))
            .signWith(secretKey)
            .compact();
    }


    /**
     * Valida un token JWT e restituisce i claim contenuti al suo interno.
     *
     * <p>La validazione comprende:</p>
     * <ul>
     *     <li>verifica dell’integrità della firma tramite la secret key</li>
     *     <li>verifica dell’emittente</li>
     *     <li>verifica automatica della scadenza</li>
     * </ul>
     *
     * <p>Se il token non è valido, verrà sollevata un’eccezione da parte del parser JWT.</p>
     *
     * @param token il token JWT da validare
     * @return i {@link Claims} presenti nel token validato
     * @throws io.jsonwebtoken.JwtException se il token è scaduto, manomesso o non valido
     */

    public Claims validateToken(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .requireIssuer(issuer)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }


}

