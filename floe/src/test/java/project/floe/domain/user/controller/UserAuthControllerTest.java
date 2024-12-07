package project.floe.domain.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import project.floe.domain.user.dto.request.UserOAuthSignUpRequest;
import project.floe.domain.user.dto.request.UserSignUpRequest;
import project.floe.domain.user.service.UserService;
import project.floe.global.config.TestSecurityConfig;
import project.floe.global.result.ResultCode;

@WebMvcTest(UserAuthController.class)
@Import(TestSecurityConfig.class)
public class UserAuthControllerTest {
    public static final String BASE_PATH = "/api/v1/auth";

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext applicationContext) {
        mockMvc =
                MockMvcBuilders.webAppContextSetup(applicationContext)
                        .addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
                        .build();
    }

    @Test
    public void oauth_회원가입() throws Exception {
        // given
        UserOAuthSignUpRequest dto = UserOAuthSignUpRequest.builder()
                .nickname("TestUser")
                .build();
        doNothing().when(userService).oAuthSignUp(any(HttpServletRequest.class), argThat(argument ->
                argument.getNickname().equals(dto.getNickname())));

        // when, then
        mockMvc.perform(post(BASE_PATH + "/oauth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.USER_OAUTH_SIGNUP_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.USER_OAUTH_SIGNUP_SUCCESS.getMessage()));

        verify(userService, times(1)).oAuthSignUp(any(HttpServletRequest.class), argThat(argument ->
                argument.getNickname().equals(dto.getNickname())));
    }

    @Test
    public void 자체_회원가입() throws Exception {
        // given
        UserSignUpRequest dto = UserSignUpRequest.builder()
                .email("test@example.com")
                .password("password123")
                .nickname("TestUser")
                .build();

        doNothing().when(userService).signUp(argThat(argument ->
                argument.getNickname().equals(dto.getNickname())));

        // when,then
        mockMvc.perform(post(BASE_PATH + "/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ResultCode.USER_CREATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.USER_CREATE_SUCCESS.getMessage()));

        verify(userService, times(1)).signUp(argThat(argument ->
                argument.getNickname().equals(dto.getNickname())));
    }

    @Test
    public void loginError_성공() throws Exception {
        // given
        String errorMessage = "Invalid credentials";

        // when, then
        mockMvc.perform(get(BASE_PATH + "/login/error")
                        .param("message", errorMessage))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResultCode.USER_LOGIN_FAIL.getCode()))
                .andExpect(jsonPath("$.message").value("로그인 실패"));
    }
}