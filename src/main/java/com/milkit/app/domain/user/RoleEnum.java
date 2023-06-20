package com.milkit.app.domain.user;

public enum RoleEnum {

	ADMIN("ROLE_ADMIN"),
    MEMBER("ROLE_MEMBER");

    private final String value;
    

    RoleEnum(String value) {
		this.value = value;
	}
    
	public String getValue() {		
		return value;
	}
}
