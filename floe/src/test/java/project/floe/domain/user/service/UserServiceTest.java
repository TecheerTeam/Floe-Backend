package project.floe.domain.user.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import project.floe.domain.record.service.MediaService;
import project.floe.domain.user.dto.request.UserOAuthSignUpRequest;
import project.floe.domain.user.dto.request.UserSignUpRequest;
import project.floe.domain.user.dto.request.UserUpdateRequest;
import project.floe.domain.user.dto.response.GetUserResponseDto;
import project.floe.domain.user.dto.response.UpdateUserResponseDto;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.entity.UserRole;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.auth.jwt.service.JwtService;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.UserServiceException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private MediaService mediaService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void 유저조회실패_존재하지않는유저() {
        // given
        HttpServletRequest mockRequest = new MockHttpServletRequest();

        // when
        doReturn(Optional.empty()).when(jwtService).extractEmail(mockRequest);

        // then
        UserServiceException exception = assertThrows(UserServiceException.class,
                () -> userService.getUser(mockRequest));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOKEN_ACCESS_NOT_EXIST);
        verify(jwtService, times(1)).extractEmail(mockRequest);
        verify(userRepository, times(0)).findByEmail(anyString());
    }

    @Test
    public void 유저조회성공_getUser() {
        // given
        String email = "test@example.com";
        HttpServletRequest mockRequest = new MockHttpServletRequest();

        User mockUser = user();

        // when
        doReturn(Optional.of(email)).when(jwtService).extractEmail(mockRequest);
        doReturn(Optional.of(mockUser)).when(userRepository).findByEmail(email);

        GetUserResponseDto responseDto = userService.getUser(mockRequest);

        // then
        assertThat(responseDto.getEmail()).isEqualTo(mockUser.getEmail());
        assertThat(responseDto.getNickname()).isEqualTo(mockUser.getNickname());
        verify(jwtService, times(1)).extractEmail(mockRequest);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void 유저생성실패_아이디중복() {
        User user = user();

        UserSignUpRequest dto = UserSignUpRequest.builder()
                .password("1234")
                .nickname("testtest")
                .email(user.getEmail())
                .experience(3)
                .age(29)
                .field("backend")
                .build();

        doReturn(Optional.of(user)).when(userRepository).findByEmail(user.getEmail());

        UserServiceException response = assertThrows(UserServiceException.class,
                () -> userService.signUp(dto));

        assertThat(response.getErrorCode()).isEqualTo(ErrorCode.USER_EMAIL_DUPLICATION_ERROR);
    }

    @Test
    public void 유저생성성공() {
        User user = user();

        UserSignUpRequest dto = UserSignUpRequest.builder()
                .password("1234")
                .nickname("testtest")
                .email("test@example.com")
                .experience(3)
                .age(29)
                .field("backend")
                .build();

        doReturn(Optional.empty()).when(userRepository).findByEmail(user.getEmail());
        doReturn(user).when(userRepository).save(any(User.class));

        userService.signUp(dto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void 유저삭제성공() {
        // given
        String email = "test@example.com";
        HttpServletRequest mockRequest = new MockHttpServletRequest();

        User mockUser = user();

        // when
        doReturn(Optional.of(email)).when(jwtService).extractEmail(mockRequest);
        doReturn(Optional.of(mockUser)).when(userRepository).findByEmail(email);
        doNothing().when(userRepository).delete(mockUser);
        userService.deleteUser(mockRequest);

        // then
        verify(jwtService, times(1)).extractEmail(mockRequest);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    public void deleteUser_토큰에서이메일추출실패() {
        // given
        HttpServletRequest mockRequest = new MockHttpServletRequest();

        // when
        doReturn(Optional.empty()).when(jwtService).extractEmail(mockRequest);

        // then
        UserServiceException exception = assertThrows(UserServiceException.class,
                () -> userService.deleteUser(mockRequest));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOKEN_ACCESS_NOT_EXIST);
        verify(jwtService, times(1)).extractEmail(mockRequest);
        verify(userRepository, times(0)).findByEmail(anyString());
    }

    @Test
    public void deleteUser_유저조회실패() {
        // given
        String email = "test@example.com";
        HttpServletRequest mockRequest = new MockHttpServletRequest();

        // when
        doReturn(Optional.of(email)).when(jwtService).extractEmail(mockRequest);
        doReturn(Optional.empty()).when(userRepository).findByEmail(email);

        // then
        UserServiceException exception = assertThrows(UserServiceException.class,
                () -> userService.deleteUser(mockRequest));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND_ERROR);
        verify(jwtService, times(1)).extractEmail(mockRequest);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(0)).delete(any(User.class));
    }

    @Test
    public void 유저정보수정성공() {
        // given
        String email = "test@example.com";
        HttpServletRequest mockRequest = new MockHttpServletRequest();

        User mockUser = user();

        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .nickname("Updated User")
                .build();

        // when
        doReturn(Optional.of(email)).when(jwtService).extractEmail(mockRequest);
        doReturn(Optional.of(mockUser)).when(userRepository).findByEmail(email);
        doReturn(mockUser).when(userRepository).save(mockUser);
        UpdateUserResponseDto responseDto = userService.updateUser(mockRequest, updateRequest);


        // then
        assertThat(responseDto.getNickname()).isEqualTo("Updated User");
        verify(jwtService, times(1)).extractEmail(mockRequest);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    public void 프로필사진추가혹은업데이트(){
        // given
        String email = "test@example.com";
        HttpServletRequest mockRequest = new MockHttpServletRequest();
        MultipartFile mockFile = new MockMultipartFile("profileImage", new byte[]{});
        User mockUser = user();
        String newImageUrl = "https://s3.amazonaws.com/newImageUrl";

        // when
        doReturn(Optional.of(email)).when(jwtService).extractEmail(mockRequest);
        doReturn(Optional.of(mockUser)).when(userRepository).findByEmail(email);
        doReturn(newImageUrl).when(mediaService).uploadToS3(mockFile);
        userService.updateProfileImage(mockRequest, mockFile);

        // then
        // 검증
        verify(jwtService, times(1)).extractEmail(mockRequest);
        verify(userRepository, times(1)).findByEmail(email);
        verify(mediaService, times(1)).uploadToS3(mockFile);
        assertThat(mockUser.getProfileImage()).isEqualTo(newImageUrl);
    }

    @Test
    public void oAuthSignUp() {
        // Mock 데이터 준비
        String email = "test@example.com";
        HttpServletRequest mockRequest = new MockHttpServletRequest();
        UserOAuthSignUpRequest dto = UserOAuthSignUpRequest.builder()
                .age(20)
                .experience(2)
                .field("frontend")
                .nickname("TestUser")
                .build();
        User mockUser = User.builder()
                .email(email)
                .nickname(null) // 초기에는 닉네임이 null
                .build();

        // Mock 동작 정의
        doReturn(Optional.of(email)).when(jwtService).extractEmail(mockRequest);
        doReturn(Optional.of(mockUser)).when(userRepository).findByEmail(email);

        // 서비스 호출
        userService.oAuthSignUp(mockRequest, dto);

        // 검증
        verify(jwtService, times(1)).extractEmail(mockRequest);
        verify(userRepository, times(1)).findByEmail(email);
        assertThat(mockUser.getNickname()).isEqualTo("TestUser");
    }


    private User user() {
        return User.builder()
                .nickname("tester")
                .role(UserRole.USER)
                .email("test@example.com")
                .password("password")
                .age(23)
                .experience(1)
                .field("backend")
                .build();
    }


}
