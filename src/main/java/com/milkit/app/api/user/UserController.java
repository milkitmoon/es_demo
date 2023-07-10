package com.milkit.app.api.user;

import com.milkit.app.domain.user.service.UserHandlerServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.milkit.app.api.AbstractApiController;
import com.milkit.app.common.response.GenericResponse;
import com.milkit.app.domain.user.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Tag(description = "1. 사용자 정보", name = "UserController")
public class UserController extends AbstractApiController {

    private UserHandlerServiceImpl userHandlerService;

    @PostMapping("/api/user/signup")
    @Operation(summary = "1.1 회원가입", description = "회원정보를 등록한다.")
    public ResponseEntity<GenericResponse<User>> signup(
        @Parameter(name = "회원정보", required = true) @RequestBody final User request) throws Exception {
        return apiResponse(() -> userHandlerService.signup(request));
    }

    @GetMapping("/api/user")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER','ROLE_ADMIN')")
    @Operation(summary = "1.2 사용자정보 조건조회 ", description = "사용자정보 전체 목록을 조회한다.")
    public ResponseEntity<GenericResponse<Page<User>>> userinfo(
            User user, @PageableDefault(page = 0, size = 10) Pageable pageable) throws Exception {
        return apiResponse(() -> userHandlerService.searchByCondition(user, pageable));
    }

    @Autowired
    public void setUserHandlerService(UserHandlerServiceImpl userHandlerService) {
        this.userHandlerService = userHandlerService;
    }
}
