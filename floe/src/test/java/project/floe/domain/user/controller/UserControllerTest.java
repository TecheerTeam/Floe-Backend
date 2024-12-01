package project.floe.domain.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import project.floe.domain.user.dto.request.UserUpdateRequest;
import project.floe.domain.user.dto.response.GetUserResponseDto;
import project.floe.domain.user.dto.response.UpdateUserResponseDto;
import project.floe.domain.user.service.UserService;
import project.floe.global.result.ResultCode;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
public class UserControllerTest {

    public static final String BASE_PATH = "/api/v1/users";

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
    public void 유저조회성공() throws Exception {
        // given
        GetUserResponseDto mockResponse = GetUserResponseDto.builder()
                .nickname("TestUser")
                .email("test@example.com")
                .build();
        when(userService.getUser(any(HttpServletRequest.class))).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.USER_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.USER_GET_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.nickname").value("TestUser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

        verify(userService).getUser(any(HttpServletRequest.class));
    }

    @Test
    void 유저삭제성공() throws Exception {
        // given
        doNothing().when(userService).deleteUser(any(HttpServletRequest.class));

        // when & then
        mockMvc.perform(delete(BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.USER_DELETE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.USER_DELETE_SUCCESS.getMessage()));

        verify(userService).deleteUser(any(HttpServletRequest.class));
    }

    @Test
    void 유저정보수정성공() throws Exception {
        // given
        UserUpdateRequest dto = UserUpdateRequest.builder()
                .nickname("newNickname")
                .build();
        UpdateUserResponseDto mockResponse = UpdateUserResponseDto.builder()
                .nickname("newNickname")
                .build();
        when(userService.updateUser(any(HttpServletRequest.class), any(UserUpdateRequest.class))).thenReturn(
                mockResponse);

        // when & then
        mockMvc.perform(patch(BASE_PATH + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.USER_UPDATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.USER_UPDATE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.nickname").value("newNickname"));

        verify(userService).updateUser(any(HttpServletRequest.class), any(UserUpdateRequest.class));
    }

    @Test
    void 유저프로필사진성공() throws Exception {
        // given
        MockMultipartFile profileImage = new MockMultipartFile("profileImage", "test.jpg", "image/jpeg",
                "testImage".getBytes());
        doNothing().when(userService).updateProfileImage(any(HttpServletRequest.class), any());

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_PATH + "/profile")
                        .file(profileImage)
                        .with(request -> {
                            request.setMethod("PUT"); // multipart에서는 기본이 POST이므로 PUT으로 변경
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.USER_PROFILE_IMAGE_UPDATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.USER_PROFILE_IMAGE_UPDATE_SUCCESS.getMessage()));

        verify(userService).updateProfileImage(any(HttpServletRequest.class), any());
    }
}
