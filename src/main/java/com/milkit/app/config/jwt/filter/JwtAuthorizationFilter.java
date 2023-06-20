package com.milkit.app.config.jwt.filter;

import java.io.IOException;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.milkit.app.common.AppCommon;
import com.milkit.app.config.jwt.JwtTokenProvider;
import com.milkit.app.domain.user.User;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
    	super(authenticationManager);
    }

	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(AppCommon.JWT_HEADER_STRING);

        if(header == null || !header.startsWith(AppCommon.JWT_TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        Authentication authentication = getUsernamePasswordAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }

    private Authentication getUsernamePasswordAuthentication(HttpServletRequest request) {
    	Authentication authentication = null;
        String token = request.getHeader(AppCommon.JWT_HEADER_STRING);
        if(token != null) {
        	Claims claims = jwtTokenProvider.getClaims(token.replace(AppCommon.JWT_TOKEN_PREFIX, ""));
        	
            if(claims != null) {
            	authentication = new UsernamePasswordAuthenticationToken(claims.get("name"), null, User.getAuthorities((String)claims.get("role")));
            }
        }
        
        return authentication;
    }
}
