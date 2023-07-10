package com.milkit.app.common.response;

import java.io.Serializable;
import com.milkit.app.common.ErrorCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public class GenericResponse<T> implements Serializable {

	@Schema(description="결과코드", example="형식 (0:성공, others:실패)")
	private String code;
	@Schema(description="결과메시지")
	private String message;
	@Schema(description="결과값", example="형식 (template 으로 정의된 값)")
	private T value;

	public GenericResponse() {
		this(ErrorCodeEnum.ok.getCode(), "성공했습니다");
	}

	public GenericResponse(int code, String message) {
		this(Integer.toString(code), message);
	}

	public GenericResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public GenericResponse(T value) {
		this();
		this.value = value;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

    public static GenericResponse<?> success() {
        return new GenericResponse<>();
    }
}
