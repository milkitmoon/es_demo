package com.milkit.app.api.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.milkit.app.common.ErrorCodeEnum;
import com.milkit.app.common.exception.ServiceException;
import com.milkit.app.domain.user.User;
import com.milkit.app.domain.user.service.UserHandlerServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class SignUpApiTest {

    @Autowired
    private MockMvc mvc;

	@Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserController userController;

    private UserHandlerServiceImpl userHandlerService;

    @Test
    @DisplayName("1. 사용자가 회원가입을 요청한다")
    public void request_test() throws Exception {
        User newUser = User.builder()
                .userId("testUser@abc.com")
                .password("test123")
                .role("ROLE_MEMBER")
                .description("신규가입자 입니다")
                .build();

        userHandlerService = mock(UserHandlerServiceImpl.class);
        userController.setUserHandlerService(userHandlerService);
        when(userHandlerService.signup(newUser)).thenReturn(newUser);

        mvc.perform(MockMvcRequestBuilders.post("/api/user/signup")
            .content(getSerializedBody(newUser))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(ErrorCodeEnum.ok.getCode()))
                .andExpect(jsonPath("message").value("성공했습니다"))
                .andExpect(jsonPath("value").isNotEmpty())
    			;
    }

    @Test
    @DisplayName("2. 사용자가 잘못된 정보로 회원가입을 요청한다")
    public void request_wrong_info_test() throws Exception {
        User newUser = User.builder()
                .userId("testUser")
                .password("test123")
                .role("ROLE_MEMBER")
                .description("신규가입자 입니다")
                .build();

        userHandlerService = mock(UserHandlerServiceImpl.class);
        userController.setUserHandlerService(userHandlerService);
        when(userHandlerService.signup(newUser))
                .thenThrow(new ServiceException(ErrorCodeEnum.InvalidEmailFormException.getCode(), new String[]{newUser.getUserId()}));

        mvc.perform(MockMvcRequestBuilders.post("/api/user/signup")
                        .content(getSerializedBody(newUser))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")).andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("code").value(ErrorCodeEnum.InvalidEmailFormException.getCode()))
                .andExpect(jsonPath("message").value("이메일 형식이 아닙니다. 입력정보를 확인해 주세요. 계정ID:"+newUser.getUserId()))
                .andExpect(jsonPath("value").isEmpty())
        ;
    }

    private String getSerializedBody(User user) throws JsonProcessingException {
        Map<String, Object> properties = objectMapper.convertValue(user, Map.class);
        properties.put("password", user.getPassword());

        return objectMapper.writeValueAsString(properties);
    }
}
