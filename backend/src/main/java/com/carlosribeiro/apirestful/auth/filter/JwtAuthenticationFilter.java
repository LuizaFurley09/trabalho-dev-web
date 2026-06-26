package com.carlosribeiro.apirestful.auth.filter;

import com.carlosribeiro.apirestful.auth.service.JwtService;
import com.carlosribeiro.apirestful.auth.util.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Component
// Esse filter é executado para cada requisição que chega no servidor
// Ele fica posicionado na frente de todos os controllers e valida o
// token (caso exista) para cada requisição.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Esse serviço valida o token
    private final JwtService jwtService;

    // Esse é o único método que precisa ser implementado
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
        throws ServletException, IOException {

        // Extrai o header de Authorization se ele vier na requisição

        var authHeader = request.getHeader("Authorization");

        // Se não veio header de Authorization, segue em frente, isto é, a requisição
        // segue para o controller. Mas antes de chegar no controller será verificado
        // se para acessar o recurso requisitado o usuário precisa estar logado ou
        // necessita de alguma autorização (perfil).
        // Em ambos os casos ocorrerá o erro 401 - UNAUTHORIZED (veja em SecurityConfig)

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            // return para que nenhuma linha de código abaixo seja executada após o
            // controller concluir sua execução.
            return;
        }

        // Tokens JWT de Authorização devem ser prefixados com a palavra Bearer
        // Logo, para validar o token é preciso remover esse prefixo e recuperar
        // o token sem esse prefixo.

        var token = authHeader.replace("Bearer ", "");   // recupera o token sem o prefixo.
        if (!jwtService.validateToken(token)) {

            // Se o token não for válido a gente passa a requisição a frente e deixa
            // o spring security decidir o que deve fazer com esta requisição.
            // Repare que o token não será salvo (veja abaixo) no contexto da aplicação,
            // logo, o spring security saberá disso e dará erro se ele for necessário.

            filterChain.doFilter(request, response);
            return;
        }

        // Se chegou aqui, então significa que o token é válido.
        // Vamos extrair do token o id do usuário e o role para que não seja preciso acessar o banco de dados
        // Quando criamos o token, armazenamos nele, como Claims o nome, email e o Role do usuário.
        // E como subject foi armazenado o id.

        long id_usuario = jwtService.getUserIdFromToken(token);
        Role role = jwtService.getRoleFromToken(token);

        // Aqui, através da classe UsernamePasswordAuthenticationToken, a gente cria um objeto
        // de autenticação, isto é, um objeto que irá conter o id do usuário e seu perfil (role) .
        // Esse construtor de UsernamePasswordAuthenticationToken espera receber um object (que
        // pode ser o objeto usuário completo ou apenas o id do usuário), as credenciais e os
        // perfis. Se vc quiser passar o objeto usuário completo, você terá que recuperá-lo do
        // banco de dados e isso será custoso para cada requisição.

        var authenticationToken = new UsernamePasswordAuthenticationToken(
            id_usuario,
            null, // não precisa da senha
            // Roles devem ser prefixados com ROLE_ e no BD temos apenas o String
            // USER ou ADMIN.
            List.of(new SimpleGrantedAuthority("ROLE_" + role)));

        // Com a linha de código abaixo estamos adicionando ao authenticationToken mais algumas
        // informações que estão no objeto request, como endereço ip e a sessão do usuário
        // se aplicável. No nosso caso, como JWT é stateless não há sessão de usuário.

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Abaixo estamos salvando no SecurityContextHolder informações sobre o usuário autenticado
        // para que estas informações possam ser recuperadas antes de um controller ser acessado para
        // verificar se o usuário possui o role necessário para acessar o recurso.

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // Passa a requisição para o próximo filter ou para o controller (se não houver um próximo filter)

        filterChain.doFilter(request, response);

        // Se houver uma linha de código aqui ela será executada após o controller ser executado.
        // Aqui poderíamos, por exemplo, modificar o objeto response.
    }
}
