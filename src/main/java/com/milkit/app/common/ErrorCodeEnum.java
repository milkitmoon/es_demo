package com.milkit.app.common;

import com.milkit.app.common.pattern.PatternMatcherServiceImpl;
import java.util.Objects;

public enum ErrorCodeEnum {
	
	ok("0", "성공했습니다."), 
	
	ValidateException("301", "검증오류가 발생하였습니다."),
	AttemptAuthenticationException("302", "인증오류가 발생하였습니다. 사용자 계정명과 비밀번호를 확인해 주세요."),
	NotExistAuthUserInfoException("303", "인증 사용자 정보가 존재하지 않습니다."),
	UserRoleException("304", "사용자 구분에 맞지 않는 요청을 하고 있습니다. 사용자구분:#{0}, 요청:#{1}"),
	InvalidSignUpParameterException("305", "회원가입 정보가 올바르지 않습니다. 입력정보를 확인해 주세요. 계정ID:#{0}, 패스워드:#{1}, 사용자구분:#{2}"),
	InvalidEmailFormException("306", "이메일 형식이 아닙니다. 입력정보를 확인해 주세요. 계정ID:#{0}"),
	InvalidRoleException("307", "사용자구분 값이 올바르지 않습니다. 입력정보를 확인해 주세요. 사용자구분:#{0}"),
	ExistUserException("308", "사용자 계정이 이미 존재합니다. 다른 계정명으로 사용해 주세요. 계정ID:#{0}"),
	NotExistAddressException("309", "주소정보가 존재하지 않습니다. 입력 주소정보를 확인해 주세요"),

	DatabaseException("881", "데이터베이스 오류입니다."),
	DuplicationException("883", "데이터베이스에 중복된 정보가 있습니다."),
	
	ServiceException("900", "서비스 오류입니다."),
	SystemError("999", "시스템오류가 발생했습니다.");

	private final String code;
    private final String message;

    ErrorCodeEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}
    
	public String getCode() {		
		return code;
	}
	
	public String getMessage() {	
		return message;
	}
	
	public String getMessage(String[] objs) {
		String remixMessage;
		try {
			remixMessage = PatternMatcherServiceImpl.getMatchingMessage(this.message, objs);
		} catch (Exception ex) {
			remixMessage = this.message;
		}
		
		return remixMessage;
	}
	
	public static String getMessage(String code) {
		for(ErrorCodeEnum t: ErrorCodeEnum.values()) {
			if(Objects.equals(t.getCode(), code)) {
				return t.getMessage();
			}
		}
		
		return "";
	}
	
	public static String getMessage(String code, String[] objs) {
		for(ErrorCodeEnum t: ErrorCodeEnum.values()) {
			if(Objects.equals(t.getCode(), code)) {
				return t.getMessage(objs);
			}
		}
		
		return null;
	}
}
