package com.carlosribeiro.apirestful.auth.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class MeuFiltro extends UsernamePasswordAuthenticationFilter {
    public MeuFiltro() {
        super();
        System.out.println("@@@@@@@@@@@@@@@@@@@@@ Executou construtor de MeuFiltro.");
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@ Entrou em attemptAuthentication() de meu filtro.");
        return super.attemptAuthentication(request, response);
    }
}
