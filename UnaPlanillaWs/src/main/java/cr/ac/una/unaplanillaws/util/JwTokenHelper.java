package cr.ac.una.unaplanillaws.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author cbcar
 */
public class JwTokenHelper {

    private static JwTokenHelper jwTokenHelper = null;
    // Tiempos de expiración (en minutos):
    private static final long EXPIRATION_LIMIT = 1;          // Token principal de acceso
    private static final long EXPIRATION_RENEWAL_LIMIT = 2;  // Token de renovación (rnt)
    private static final String AUTHENTICATION_SCHEME = "Bearer ";
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Clave secreta para firma HMAC

    private JwTokenHelper() {
    }

    public static JwTokenHelper getInstance() {
        if (jwTokenHelper == null) {
            jwTokenHelper = new JwTokenHelper();
        }
        return jwTokenHelper;
    }

    // Genera el JWT principal y embebe el token de renovación en el claim 'rnt'
    public String generatePrivateKey(String username) {
        return AUTHENTICATION_SCHEME + Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(getExpirationDate(false)) // Expiración corta (1 min)
                .claim("rnt", AUTHENTICATION_SCHEME + Jwts.builder()
                        .setSubject(username)
                        .setIssuedAt(new Date())
                        .setExpiration(getExpirationDate(true)) // Expiración larga (2 min)
                        .claim("rnw", true) // Marca que identifica este token como de renovación
                        .signWith(key)
                        .compact())
                .signWith(key)
                .compact();
    }
    private Date getExpirationDate(boolean renewal) {
        long currentTimeInMillis = System.currentTimeMillis();
        long expMilliSecond = TimeUnit.MINUTES.toMillis(EXPIRATION_LIMIT);
        if (renewal) {
            expMilliSecond = TimeUnit.MINUTES.toMillis(EXPIRATION_RENEWAL_LIMIT);
        }
        return new Date(currentTimeInMillis + expMilliSecond);
    }

    public Claims claimKey(String privateKey) throws ExpiredJwtException, MalformedJwtException {
        return Jwts.parser().setSigningKey(key)
                .parseClaimsJws(privateKey)
                .getBody();
    }

}
