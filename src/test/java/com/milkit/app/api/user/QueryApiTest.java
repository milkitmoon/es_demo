package com.milkit.app.api.user;

import com.milkit.app.common.AppCommon;
import com.milkit.app.common.ErrorCodeEnum;

import com.milkit.app.domain.user.UseYNEnum;
import com.milkit.app.domain.user.User;
import com.milkit.app.domain.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class QueryApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserController userController;

    private UserServiceImpl userService;

    List<User> users;

    @BeforeEach
    public void setup() {
        users = new ArrayList<>();

        User user1 = User.builder()
                .userId("test1@milkit.com")
                .role("ROLE_MEMBER")
                .useYn(UseYNEnum.YES.getValue())
                .description("일반사용자 입니다")
                .build();

        User user2 = User.builder()
                .userId("test2@milkit.com")
                .role("ROLE_MEMBER")
                .useYn(UseYNEnum.YES.getValue())
                .description("일반사용자 입니다")
                .build();

        users.add(user1);
        users.add(user2);

        userService = mock(UserServiceImpl.class);
        userController.setUserService(userService);
    }

    @Test
    @DisplayName("1. 사용자 정보를 조회한다.")
    public void query_user_info_test() throws Exception {
        when(userService.selectAll()).thenReturn(users);

		String userJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9NRU1CRVIiLCJuYW1lIjoidGVzdEBtaWxraXQuY29tIiwiZXhwIjoxODAwMjg5MjU3LCJpYXQiOjE2MDkzMjkyNTd9.WQvrB3eQpUjI5p7_mNAiL_GrM_qfMwQHcB_ck4Zdwwk";

        mvc.perform(MockMvcRequestBuilders.get("/api/user")
            .header(AppCommon.JWT_HEADER_STRING, AppCommon.JWT_TOKEN_PREFIX+userJwtToken)
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
    			.andDo(print())
    	        .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(ErrorCodeEnum.ok.getCode()))
                .andExpect(jsonPath("message").value("성공했습니다"))
                .andExpect(jsonPath("value").isNotEmpty());
    }
}
