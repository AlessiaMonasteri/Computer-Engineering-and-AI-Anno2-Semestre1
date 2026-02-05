package AlessiaMonasteri.BagECommerce.security;

import AlessiaMonasteri.BagECommerce.entities.User;
import AlessiaMonasteri.BagECommerce.exceptions.UnauthorizedException;
import AlessiaMonasteri.BagECommerce.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.UUID;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JWTTools jwtTools;
    @Autowired
    private UserService userService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws  ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            // Controllo del token
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException("Token missing or not in the correct format");
            }

            // Rimuovo il prefisso "Bearer " e ottengo solo il JWT
            String accessToken = authorizationHeader.replace("Bearer ", "");
            // Verifico la validità del token (firma, scadenza,...)
            jwtTools.verifyToken(accessToken);

            // AUTORIZZAZIONE
            // Estraggo l'id dal token
            UUID userId = jwtTools.getIDFromToken(accessToken);
            // Cerco l'utente nel db
            User found = this.userService.findById(userId);
            // Lo associo al SecurityContext
            Authentication authentication = new UsernamePasswordAuthenticationToken(found, null, found.getAuthorities());
            // Salvo l’Authentication nel SecurityContext corrente
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Passo la richiesta al successivo filtro / controller
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Problems with token");
        }
    }
    // Nessun filtro per requests come /auth/login o /auth/register
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return new AntPathMatcher().match("/auth/**", request.getServletPath());
    }
}
