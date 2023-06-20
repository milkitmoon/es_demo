package com.milkit.app.config.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import com.milkit.app.common.AppCommon;

@Slf4j
public class JwtTokenProvider {

	public String createAccessToken(JwtDetails principal) {
		return createToken(principal, AppCommon.JWT_EXPIRATION_TIME);
	}
	
	public String createRefreshToken(JwtDetails principal) {
        return createToken(principal, AppCommon.JWT_REFRESH_EXPIRATION_TIME);
	}
	
	private String createToken(JwtDetails principal, long expirationTime) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);
        
		return Jwts.builder().setSubject(principal.getSubject())
				  .setClaims(principal.getClaims())
		          .setIssuedAt(now)
		          .setExpiration(validity)
		          .signWith(SignatureAlgorithm.HS256, AppCommon.JWT_SECRET_KEY)
		          .compact();
	}

	public Map<String, String> createHeader(JwtDetails principal) {
    	String token = createAccessToken(principal);
    	String refreshToken = createRefreshToken(principal);
    	
        Map header = new HashMap<String, String>();
        header.put(AppCommon.JWT_HEADER_STRING, token);
        header.put(AppCommon.JWT_REFRESH_HEADER_STRING, refreshToken);

        return header;
    }

	public JwtToken createBody(JwtDetails jwtDetails) {
    	String token = createAccessToken(jwtDetails);
    	String refreshToken = createRefreshToken(jwtDetails);
    	
    	return new JwtToken(token, refreshToken);
    }
    
    public Claims getClaims(String token) {
        try {
            Claims claims = parseClaims(token);

            log.debug("expireTime :" + claims.getExpiration());
            log.debug("userId :" + claims.get("name"));
            log.debug("role :" + claims.get("role"));
            
            return claims;

        } catch (ExpiredJwtException exception) {
            log.error("Token Expired");
            return null;
        } catch (JwtException exception) {
            log.error("Token Tampered");
            return null;
        } catch (NullPointerException exception) {
            log.error("Token is null");
            return null;
        }
    }

	public String getUsername(String token) throws JwtException {
		Claims claims = parseClaims(token);
	
		return (String) claims.get("name");
	}

    private Claims parseClaims(String token) {
        return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(AppCommon.JWT_SECRET_KEY))
                .parseClaimsJws(token).getBody();
    }
}
