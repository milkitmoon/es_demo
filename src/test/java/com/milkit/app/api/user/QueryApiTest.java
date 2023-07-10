package com.milkit.app.api.user;

import com.milkit.app.common.AppCommon;
import com.milkit.app.common.ErrorCodeEnum;

import com.milkit.app.domain.user.UseYNEnum;
import com.milkit.app.domain.user.User;
import com.milkit.app.domain.user.service.UserHandlerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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

    private UserHandlerServiceImpl userHandlerService;

    Page<User> users;

    @BeforeEach
    public void setup() {
        List<User> userList = new ArrayList<>();

        User user1 = User.builder()
                .userId("member@milkit.com")
                .role("ROLE_MEMBER")
                .useYn(UseYNEnum.YES.getValue())
                .description("일반멤버 사용자 입니다")
                .build();

        User user2 = User.builder()
                .userId("admin@milkit.com")
                .role("ROLE_ADMIN")
                .useYn(UseYNEnum.YES.getValue())
                .description("관리자 사용자 입니다")
                .build();

        userList.add(user1);
        userList.add(user2);
        users = new PageImpl<>(userList);

        userHandlerService = mock(UserHandlerServiceImpl.class);
        userController.setUserHandlerService(userHandlerService);
    }

    @Test
    @DisplayName("1. 사용자 정보를 조회한다.")
    public void query_user_info_test() throws Exception {
        User userRequest = User.builder().description("사용자").build();
        PageRequest pageRequest = PageRequest.of(0, 5);
        when(userHandlerService.searchByCondition(userRequest, pageRequest)).thenReturn(users);

		String userJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9NRU1CRVIiLCJuYW1lIjoidGVzdEBtaWxraXQuY29tIiwiZXhwIjoxODAwMjg5MjU3LCJpYXQiOjE2MDkzMjkyNTd9.WQvrB3eQpUjI5p7_mNAiL_GrM_qfMwQHcB_ck4Zdwwk";

        MultiValueMap<String, String> requestParam = new LinkedMultiValueMap<>();
        requestParam.set("description", "사용자");
        requestParam.set("page", "0");
        requestParam.set("size", "5");

        mvc.perform(MockMvcRequestBuilders.get("/api/user").params(requestParam)
            .header(AppCommon.JWT_HEADER_STRING, AppCommon.JWT_TOKEN_PREFIX+userJwtToken)
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
    			.andDo(print())
    	        .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(ErrorCodeEnum.ok.getCode()))
                .andExpect(jsonPath("message").value("성공했습니다"))
                .andExpect(jsonPath("value").isNotEmpty())
                .andExpect(jsonPath("$.value.content").isArray());
    }
}
