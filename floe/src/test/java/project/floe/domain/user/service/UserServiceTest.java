package project.floe.domain.user.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.floe.domain.user.dto.request.SignUpRequestDto;
import project.floe.domain.user.dto.request.UpdateUserRequestDto;
import project.floe.domain.user.dto.response.GetUserResponseDto;
import project.floe.domain.user.entity.UserEntity;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.UserServiceException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Test
    public void 유저조회실패_존재하지않는유저(){
        String noExistUserId = "userId";
        doReturn(null).when(userRepository).findByUserId(noExistUserId);

        UserServiceException response = assertThrows(UserServiceException.class,
                () -> userService.getUser(noExistUserId));

        assertThat(response.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND_ERROR);
    }

    @Test
    public void 유저조회성공_getUser(){
        UserEntity user = user();
        doReturn(user).when(userRepository).findByUserId(user.getUserId());

        GetUserResponseDto responseDto = userService.getUser(user.getUserId());

        assertThat(responseDto.getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    public void 유저생성실패_아이디중복(){
        String duplicateUserId = "userId";
        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setUserId(duplicateUserId);
        doReturn(user()).when(userRepository).findByUserId(duplicateUserId);

        UserServiceException response = assertThrows(UserServiceException.class,
                () -> userService.signUp(dto));

        assertThat(response.getErrorCode()).isEqualTo(ErrorCode.USER_ID_DUPLICATION_ERROR);
    }

    @Test
    public void 유저생성실패_이메일중복(){
        String duplicateEmail = "email";
        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setEmail(duplicateEmail);
        doReturn(null).when(userRepository).findByUserId(dto.getUserId());
        doReturn(user()).when(userRepository).findByEmail(duplicateEmail);

        UserServiceException response = assertThrows(UserServiceException.class,
                () -> userService.signUp(dto));

        assertThat(response.getErrorCode()).isEqualTo(ErrorCode.USER_EMAIL_DUPLICATION_ERROR);
    }

    @Test
    public void 유저생성성공(){
        UserEntity user = user();
        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        doReturn(null).when(userRepository).findByUserId(user.getUserId());
        doReturn(null).when(userRepository).findByEmail(user.getEmail());
        doReturn(user).when(userRepository).save(any(UserEntity.class));

        userService.signUp(dto);

        verify(userRepository,times(1)).save(any(UserEntity.class));
    }

    @Test
    public void 유저삭제성공(){
        UserEntity user = user();
        doReturn(user).when(userRepository).findByUserId(user.getUserId());
        doNothing().when(userRepository).delete(any(UserEntity.class));

        // 로그인 구현 후 요청한 유저가 삭제하려는 유저와 동일한지 확인 로직
        userService.deleteUser(user.getUserId());

        verify(userRepository,times(1)).delete(any(UserEntity.class));
    }

    @Test
    public void 유저정보수정성공(){
        UserEntity user = user();
        UpdateUserRequestDto dto = new UpdateUserRequestDto();
        doReturn(user).when(userRepository).findByUserId(user.getUserId());
        doReturn(user).when(userRepository).save(user);

        // 로그인 구현 후 요청한 유저가 수정하려는 유저와 동일한지 확인 로직
        userService.updateUser(user.getUserId(),dto);

        verify(userRepository,times(1)).save(any(UserEntity.class));
    }

    private UserEntity user(){
        return new UserEntity(0,"userId","password","name","email",1,20,"","backend");
    }
}