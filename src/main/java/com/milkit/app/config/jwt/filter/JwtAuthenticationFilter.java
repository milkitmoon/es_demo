package com.milkit.app.config.jwt.filter;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milkit.app.common.ErrorCodeEnum;
import com.milkit.app.common.exception.ServiceException;
import com.milkit.app.common.response.GenericResponse;
import com.milkit.app.config.jwt.JwtToken;
import com.milkit.app.config.jwt.JwtTokenProvider;
import com.milkit.app.domain.user.User;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	@Autowired
    private JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    	UsernamePasswordAuthenticationToken authenticationToken;
    	
		try {
			User credentials = new ObjectMapper().readValue(request.getInputStream(), User.class);
	        authenticationToken = new UsernamePasswordAuthenticationToken(
	                credentials.getUsername(),
	                credentials.getPassword(),
	                new ArrayList<>()
	        );
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException(ErrorCodeEnum.AttemptAuthenticationException.getCode());
		}

        return super.getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
    	User principal = (User) authResult.getPrincipal();

    	writeAuthenticationBody(response, principal);
	}
	
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)	throws IOException, ServletException {
		super.unsuccessfulAuthentication(request, response, failed);

		throw new ServiceException(ErrorCodeEnum.AttemptAuthenticationException.getCode());
	}
    
    private void writeAuthenticationBody(HttpServletResponse response, User principal) throws IOException {
    	JwtToken jwtToken = jwtTokenProvider.createBody(principal);
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	response.getWriter().write( objectMapper.writeValueAsString(new GenericResponse<JwtToken>((jwtToken))) );
    }
}
