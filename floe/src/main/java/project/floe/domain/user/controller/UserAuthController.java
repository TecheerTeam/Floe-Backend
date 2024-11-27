package project.floe.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.floe.domain.user.dto.request.UserOAuthSignUpRequest;
import project.floe.domain.user.dto.request.UserSignUpRequest;
import project.floe.domain.user.service.UserService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserService userService;

    // 소셜 로그인 최초 시 추가 정보 입력 페이지로 이동
    // 해당 페이지에서 입력한 추가정보를 처리하는 컨트롤러
    @PostMapping("/oauth/sign-up")
    public ResponseEntity<ResultResponse> oauthSignUp(
            HttpServletRequest request,
            @Valid @RequestBody UserOAuthSignUpRequest dto) {
        userService.oAuthSignUp(request, dto);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.USER_OAUTH_SIGNUP_SUCCESS));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ResultResponse> signUp(
            @Valid @RequestBody UserSignUpRequest dto
    ) {
        userService.signUp(dto);
        ResultResponse response = ResultResponse.of(ResultCode.USER_CREATE_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/login/error")
    public ResponseEntity<ResultResponse> loginError(
            @RequestParam(value = "message", required = false) String message) {
        log.error("로그인 오류 발생 : {}", message);
        return ResponseEntity.badRequest()
                .body(ResultResponse.of(ResultCode.USER_LOGIN_FAIL));

    }
}
