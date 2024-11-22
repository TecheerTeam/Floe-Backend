package project.floe.domain.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import project.floe.domain.user.dto.request.UserSignUpRequest;
import project.floe.domain.user.dto.request.UserUpdateRequest;
import project.floe.domain.user.dto.response.GetUserResponseDto;
import project.floe.domain.user.dto.response.UpdateUserResponseDto;
import project.floe.domain.user.service.UserService;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.ErrorResponse;
import project.floe.global.error.GlobalExceptionHandler;
import project.floe.global.error.exception.UserServiceException;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void 유저조회실패_존재하지않는유저() throws Exception {
        String url = "/api/v1/users/{noExistId}";
        String pathVariable = "noExistId";
        ErrorResponse expectedResponse = ErrorResponse.of(ErrorCode.USER_NOT_FOUND_ERROR);
        doThrow(new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR)).when(userService).getUser(pathVariable);

        mockMvc.perform(
                        MockMvcRequestBuilders.get(url, pathVariable)
                )

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.businessCode").value(expectedResponse.getBusinessCode()))
                .andExpect(jsonPath("$.errorMessage").value(expectedResponse.getErrorMessage()));
    }

    @Test
    public void 유저조회성공() throws Exception {
        String url = "/api/v1/users/{userId}";
        String pathVariable = "userId";
        GetUserResponseDto expectedResponseDto = new GetUserResponseDto();
        ResultResponse expectedResultResponse = ResultResponse.of(ResultCode.USER_GET_SUCCESS);
        doReturn(expectedResponseDto).when(userService).getUser(pathVariable);

        mockMvc.perform(
                        MockMvcRequestBuilders.get(url, pathVariable)
                )

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(expectedResultResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResultResponse.getMessage()))
                .andExpect(jsonPath("$.data.userId").value(expectedResponseDto.getUserId()));
    }

    @Test
    public void 유저생성실패_이메일중복() throws Exception {
        String url = "/api/v1/users";
        ErrorResponse expectedResponse = ErrorResponse.of(ErrorCode.USER_EMAIL_DUPLICATION_ERROR);
        doThrow(new UserServiceException(ErrorCode.USER_EMAIL_DUPLICATION_ERROR)).when(userService)
                .signUp(any(UserSignUpRequest.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new UserSignUpRequest()))
                                .characterEncoding("UTF-8")
                )

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.businessCode").value(expectedResponse.getBusinessCode()))
                .andExpect(jsonPath("$.errorMessage").value(expectedResponse.getErrorMessage()));


    }

    @Test
    public void 유저생성실패_아이디중복() throws Exception {
        String url = "/api/v1/users";
        ErrorResponse expectedResponse = ErrorResponse.of(ErrorCode.USER_ID_DUPLICATION_ERROR);
        doThrow(new UserServiceException(ErrorCode.USER_ID_DUPLICATION_ERROR)).when(userService)
                .signUp(any(UserSignUpRequest.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new UserSignUpRequest()))
                                .characterEncoding("UTF-8")
                )

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.businessCode").value(expectedResponse.getBusinessCode()))
                .andExpect(jsonPath("$.errorMessage").value(expectedResponse.getErrorMessage()));
    }

    @Test
    public void 유저생성성공() throws Exception {
        String url = "/api/v1/users";
        ResultResponse expectedResultResponse = ResultResponse.of(ResultCode.USER_CREATE_SUCCESS);
        doNothing().when(userService).signUp(any(UserSignUpRequest.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new UserSignUpRequest()))
                                .characterEncoding("UTF-8")
                )

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(expectedResultResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResultResponse.getMessage()))
                .andReturn();
    }

    @Test
    public void 유저삭제성공() throws Exception {
        String url = "/api/v1/users/{userId}";
        String pathVariable = "userId";
        ResultResponse responseResultResponse = ResultResponse.of(ResultCode.USER_DELETE_SUCCESS);
        doNothing().when(userService).deleteUser("userId");

        mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, pathVariable)
                )

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(responseResultResponse.getCode()))
                .andExpect(jsonPath("$.message").value(responseResultResponse.getMessage()));
    }

    @Test
    public void 유저정보수정성공() throws Exception {
        String url = "/api/v1/users/{userId}";
        String pathVariable = "userId";
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        UpdateUserResponseDto updateUserResponseDto = new UpdateUserResponseDto();
        ResultResponse expectedResultResponse = ResultResponse.of(ResultCode.USER_UPDATE_SUCCESS);
        doReturn(updateUserResponseDto).when(userService).updateUser(eq(pathVariable), any(UserUpdateRequest.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.patch(url, pathVariable)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userUpdateRequest))
                                .characterEncoding("UTF-8"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(expectedResultResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResultResponse.getMessage()))
                .andExpect(jsonPath("$.data.name").value(updateUserResponseDto.getName()))
                .andExpect(jsonPath("$.data.email").value(updateUserResponseDto.getEmail()));
    }


}
