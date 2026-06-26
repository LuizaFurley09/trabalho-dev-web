package com.carlosribeiro.apirestful.auth.service;

import com.carlosribeiro.apirestful.auth.model.Usuario;
import com.carlosribeiro.apirestful.auth.util.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    // A chave secreta fornecida no arquivo .env foi gerada com o url abaixo:
    // https://generate-random.org/

    // A chave secreta deve ser armazenada fora do código fonte.
    // Vamos salvá-la em um arquivo .env, na raiz do projeto.
    // Este arquivo não deve ser copiado para o repositório do projeto, logo o seu
    // nome (.env) deve ser adicionado a .gitignore para não ser salvo no repositório.

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${jwt.security.accessTokenExpiration}")
    private String accessTokenExpiration;

    public String generateAccessToken(Usuario usuario) {
        final long tokenExpiration = Long.parseLong(accessTokenExpiration); // 2 horas
        return generateToken(usuario, tokenExpiration);
    }

    private String generateToken(Usuario usuario, long tokenExpiration) {
        return Jwts.builder()
            // subject => algo que identifica unicamente um usuário
            .subject(usuario.getId().toString())
            .claim("name", usuario.getNome())    // Adicionando ao token o nome
            // .claim("email", usuario.getEmail())  // e role como Claims para
            .claim("role", usuario.getRole())    // não ser preciso acessar o BD.
            // Toda vez que o usuário acessa o servidor esse token precisa ser enviado,
            // logo, não é bom ter muitos Claims no token. Isso aumenta o tamanho do
            // token e prejudica o desempenho.
            .issuedAt(new Date())
            // expiração em milissegundos
            .expiration(new Date(System.currentTimeMillis() + tokenExpiration * 1000))
            // O token precisa ser assinado para que não possa ser corrompido.
            // - secret.getBytes() converte a chave (String) em um byte[]
            // - Keys.hmacShaKeyFor() cria uma instância da chave secreta compatível com
            //   algoritmos HMAC-SHA
            // - .signWith() assina o token usando um algoritmo simétrico HMAC. Se vc não
            //   especificar o algoritmo explicitamente, utiliza HS256.
            // A chave deve ter, no mínimo, 256 bits ou 32 caracteres
            .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
            .compact(); // compact() gera o token
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            boolean valido = claims.getExpiration().after(new Date());
            return valido;
        }
        catch(JwtException e) {
            // O parser gera uma exceção se o token não for válido
            return false;
        }
    }

    public Claims getClaims(String token) {
        // Primeiro é preciso ciar um parser para validar o token. O método parser() funciona como um builder.
        // Claims - reivindicações ou declarações - refere-se às informações contidas no payload. Existem
        // claims padrão como subject e iat, e claims definidos pelo usuário como: nome, role, etc
        // A interface Claims estende a interface Map.
        // Se o token for inválido esse método irá lançar uma exceção.
        Claims claims = Jwts.parser()
            // Para verificar se o token é válido:
            // - secret.getBytes() converte a chave (String) em um byte[]
            // - Keys.hmacShaKeyFor() cria uma instância da chave secreta compatível com
            //   algoritmos HMAC-SHA (algoritmos simétricos)
            // - .verifyWith() descriptografa o token usando um algoritmo simétrico HMAC.
            //   Por default utiliza o HS256.
            .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
            // retorna o parser JWT
            .build()
            // efetua o parse do token
            .parseSignedClaims(token)
            // recuperamos o payload que contém o subject e os demais claims
            .getPayload();
        return claims;
    }

    public Long getUserIdFromToken(String token) {
        // subject irá conter o id do usuário
        return Long.valueOf(getClaims(token).getSubject());
    }

    public Role getRoleFromToken(String token) {
        // claims irá conter um map com o role como um String.
        return Role.valueOf(getClaims(token).get("role", String.class));
    }
}
