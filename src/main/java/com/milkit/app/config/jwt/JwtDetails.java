package com.milkit.app.config.jwt;

import java.util.Map;

public interface JwtDetails {
	
	String getSubject();
	Map<String, Object> getClaims();

}
