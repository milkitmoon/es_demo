package com.milkit.app.api.user;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.milkit.app.api.AbstractApiController;
import com.milkit.app.common.response.GenericResponse;
import com.milkit.app.domain.user.User;
import com.milkit.app.domain.user.service.UserServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Tag(description = "2. 사용자 정보", name = "UserController")
public class UserController extends AbstractApiController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/api/user/signup")
    @Operation(summary = "2.1 회원가입", description = "회원정보를 등록한다.")
    public ResponseEntity<GenericResponse<User>> signup(
        @Parameter(name = "회원정보", required = true) @RequestBody final User request) throws Exception {
        User newUser = userService.signup(request);
        return apiResponse(() -> newUser);
    }

    @GetMapping("/api/user")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER','ROLE_ADMIN')")
    @Operation(summary = "2.2 사용자정보 전체조회 ", description = "사용자정보 전체 목록을 조회한다.")
    public ResponseEntity<GenericResponse<List<User>>> userinfo() throws Exception {
        return apiResponse(() -> userService.selectAll());
    }

    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }
}
