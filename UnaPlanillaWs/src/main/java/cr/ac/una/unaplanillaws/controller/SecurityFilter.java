package cr.ac.una.unaplanillaws.controller;

import cr.ac.una.unaplanillaws.util.JwTokenHelper;
import cr.ac.una.unaplanillaws.util.Secure;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Principal;

/**
 *
 * @author cbcar 
 */
@Provider
@Secure
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter{
    
    private static final String AUTHORIZATION_SERVICE_PATH = "validarUsuario";
    private static final String RENEWAL_SERVICE_PATH = "renovarToken";
    private final JwTokenHelper jwTokenHelper = JwTokenHelper.getInstance();
    private static final String AUTHENTICATION_SCHEME = "Bearer ";

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        Method method = resourceInfo != null ? resourceInfo.getResourceMethod() : null;
        
        // 1. Exclusión: el endpoint de autenticación (login) es público y no requiere token
        if (method != null && method.getName().equalsIgnoreCase(AUTHORIZATION_SERVICE_PATH)) {
            return;
        }

        // 2. Extraer la cabecera Authorization enviada por el cliente
        String authorizationHeader = request.getHeaderString(HttpHeaders.AUTHORIZATION);

        // 3. Validar presencia y formato del esquema 'Bearer <token>'
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            abortWithUnauthorized(request, "Authorization is missing in header");
            return;
        }  else if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(request, "Invalid authorization");
            return;
        }
        
        // 4. Extraer el token puro retirando el prefijo 'Bearer '
        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

        try {
            // 5. Validar firma criptográfica y expiración con la clave secreta
            try {
                Claims claims = jwTokenHelper.claimKey(token);
                
                // Si el token es de renovación (rnw: true), SOLO puede consumir el endpoint 'renovarToken'
                if (claims.containsKey("rnw") && (Boolean)claims.get("rnw")== true && (method == null || !method.getName().equalsIgnoreCase(RENEWAL_SERVICE_PATH))){
                    abortWithUnauthorized(request, "Invalid authorization");
                    return;
                }
                
                // 6. Token válido: inyectar identidad (username) en el SecurityContext
                final SecurityContext currentSecurityContext = request.getSecurityContext();
                request.setSecurityContext(new SecurityContext() {

                    @Override
                    public Principal getUserPrincipal() {
                        return () -> claims.getSubject();
                    }

                    @Override
                    public boolean isUserInRole(String role) {
                        return true;
                    }

                    @Override
                    public boolean isSecure() {
                        return currentSecurityContext.isSecure();
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        return AUTHENTICATION_SCHEME;
                    }
                });
            } catch (ExpiredJwtException | MalformedJwtException e) {
                if (e instanceof ExpiredJwtException) {
                    abortWithUnauthorized(request, "Authorization is expired");
                    return;
                } else if (e instanceof MalformedJwtException) {
                    abortWithUnauthorized(request, "Authorization is not correct");
                    return;
                }
            }

        } catch (Exception e) {
            abortWithUnauthorized(request, "Invalid authorization");
            return;
        }
    }
    
    private boolean isTokenBasedAuthentication(String authorizationHeader) {

        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase());
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {

        // Abort the filter chain with a 401 status code response
        // The WWW-Authenticate header is sent along with the response
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED.getStatusCode(), message)
                        .header(HttpHeaders.WWW_AUTHENTICATE,
                                message)
                        .build());
    }
    
}