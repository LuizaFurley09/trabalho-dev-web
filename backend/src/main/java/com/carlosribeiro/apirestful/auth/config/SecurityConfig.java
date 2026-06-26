package com.carlosribeiro.apirestful.auth.config;

import com.carlosribeiro.apirestful.auth.filter.JwtAuthenticationFilter;
import com.carlosribeiro.apirestful.auth.service.UsuarioDetailsService;
import com.carlosribeiro.apirestful.auth.util.Role;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Configuration
// Para habilitar o uso de @PreAuthorize nos métodos de controladores ou serviço
@EnableMethodSecurity  // @EnableWebSecurity
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        // Permite que requisições de origens diferentes enviem credenciais.
        // Permite o envio de informações de autenticação, cookies, etc em requisições CORS.
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // / é a raiz do servidor e /** é um padrão de PathMatch que indica todas as rotas e subrotas.
        // Isto é, qq requisição será atendida por esta por esta configuração.
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        System.out.println("***************** Executou o método securityFilterChain de SecurityFilterChain");
        httpSecurity
            .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Cross Site Request Forgery - Um tipo de ataque utilizado em session based autentication
            // Em aplicações restful, como este tipo de ataque não acontece, deve ser desabilitado por questão
            // de desempenho. Na linha abaixo é possível mudar para method reference.
            .csrf(c -> c.disable())
            .cors(c -> c.configurationSource(corsConfigurationSource()))

            .authorizeHttpRequests(authorize -> authorize
               .requestMatchers(HttpMethod.GET, "/produtos/**").permitAll()

               .requestMatchers(HttpMethod.POST, "/produtos/**").hasAnyRole(Role.USER.name(), Role.ADMIN.name())
               .requestMatchers(HttpMethod.PUT, "/produtos/**").hasRole(Role.ADMIN.name())
               .requestMatchers(HttpMethod.DELETE, "/produtos/**").hasRole(Role.ADMIN.name())

                // qq usuário pode cadastrar um usuário
                .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                .requestMatchers(HttpMethod.GET, "/usuarios").permitAll()

                // qq usuário pode se logar
                .requestMatchers(HttpMethod.POST, "/autenticacao/login").permitAll()

                // Para acessar /favoritos é preciso estar logado
                //.requestMatchers(HttpMethod.GET,"/favoritos/**").authenticated()
                //.requestMatchers(HttpMethod.POST,"/favoritos/**").authenticated()
                //.requestMatchers(HttpMethod.DELETE,"/favoritos/**").authenticated())

                .anyRequest().authenticated())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> {
                // Quando o usuário não está logado, por default, é retornado o erro 403 - FORBIDDEN
                // Estamos mudando para 401 - UNAUTHORIZED
                ex.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write("""
                        {
                            "status": 401,
                            "message": "Necessario estar autenticado para acessar este recurso."
                        }
                        """);
                });

                // Quando o usuário está autenticado mas não possui o perfil (ROLE) necessário para
                // acessar o recurso, por default é retornado o erro 401 - UNAUTHORIZED.
                // Estamos mudando para 403 - FORBIDDEN
                ex.accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json");
                    response.getWriter().write("""
                        {
                            "status": 403,
                            "message": "Voce nao tem permissao para acessar este recurso."
                        }
                        """);
                });
            });

        return httpSecurity.build();  // Cria um objeto SecurityFilterChain.
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("1. ***** Executou o método passwordEncoder()");
        // BCryptPasswordEncoder é o algoritmo recomendado para efetuar o hash das senhas.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        System.out.println("3. ***** Executou authenticationProvider()");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Quando tentarmos efetuar um login através de AuthenticationController, o método
    // authenticate() de AuthenticationManager vai chamar o método authenticate() de um
    // AuthenticationProvider (no nosso caso, DaoAuthenticationProvider), que irá chamar
    // o método loadUserByUsename() de UsuarioService (a classe que implementa a interface
    // UserDetailsService - que possui o método loadUserByUsername()).

    // O DaoAuthenticationProvider irá chamar o método loadUserByUsername() de
    // usuarioService para recuperar do banco de dados a senha do usuário. Em seguida ele
    // usa o passwordEncoder para criptografar a senha recebida do usuário e a compra com
    // a senha recuperada do banco de dados.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
        throws Exception {
        System.out.println("2. ***** Executou authenticationManager()");
        return config.getAuthenticationManager();
    }
}
