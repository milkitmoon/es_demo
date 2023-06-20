package com.milkit.app.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.milkit.app.common.ErrorCodeEnum;
import com.milkit.app.domain.user.User;
import com.milkit.app.domain.user.service.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import lombok.extern.slf4j.Slf4j;
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

    private UserServiceImpl userService;

    @Test
    @DisplayName("1. 사용자가 회원가입을 요청한다")
    public void request_test() throws Exception {
        User newUser = User.builder()
                .userId("testUser@abc.com")
                .password("test123")
                .role("ROLE_MEMBER")
                .description("신규가입자 입니다")
                .build();

        userService = mock(UserServiceImpl.class);
        userController.setUserService(userService);
        when(userService.signup(newUser)).thenReturn(newUser);

        mvc.perform(MockMvcRequestBuilders.post("/api/user/signup")
            .content(objectMapper.writeValueAsString(newUser))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(ErrorCodeEnum.ok.getCode()))
                .andExpect(jsonPath("message").value("성공했습니다"))
                .andExpect(jsonPath("value").isNotEmpty())
    			;
    }
}
