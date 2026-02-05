package AlessiaMonasteri.BagECommerce.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JWTAuthFilter jwtAuthFilter;

    public SecurityConfig(JWTAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // Disabilita il form di login
        httpSecurity.formLogin(formLogin -> formLogin.disable());
        // Configura la gestione delle sessioni
        httpSecurity.sessionManagement(sessions ->
                // Imposta policy STATELESS
                sessions.sessionCreationPolicy((SessionCreationPolicy.STATELESS))
        );
        // Disabilita CSRF
        httpSecurity.csrf(csrf -> csrf.disable());

        httpSecurity.authorizeHttpRequests(req -> req
                .requestMatchers("/auth/**").permitAll()  // login/register pubblici
                .anyRequest().authenticated()                      // tutto il resto richiede JWT valido
        );
        // Abilita CORS
        httpSecurity.cors(Customizer.withDefaults());

        httpSecurity.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        // Costruisce e ritorna la SecurityFilterChain finale
        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        // Permette le richieste solo da questa origin
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        // Metodi HTTP consentiti dall'origin
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        // Consente tutti gli header in ingresso
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Applica questa config a tutte le rotte dell'app
        source.registerCorsConfiguration("/**", configuration);
        // Ritorna la sorgente che Spring Security userà per la gestione CORS
        return source;
    }
}
