package com.milkit.app.domain.user;

public enum UseYNEnum {

    YES("Y"),
    NO("N");

    private final String value;

    UseYNEnum(String value) {
		this.value = value;
	}
    
	public String getValue() {		
		return value;
	}
}
