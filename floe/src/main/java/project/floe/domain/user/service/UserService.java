package project.floe.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.floe.domain.user.dto.request.SignUpRequestDto;
import project.floe.domain.user.dto.request.UpdateUserRequestDto;
import project.floe.domain.user.dto.response.GetUserResponseDto;
import project.floe.domain.user.dto.response.UpdateUserResponseDto;
import project.floe.domain.user.entity.UserEntity;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.UserServiceException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public GetUserResponseDto getUser(String userId) {

        UserEntity findUser = userRepository.findByUserId(userId);

        if (findUser == null) {
            throw new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR);
        }
        return new GetUserResponseDto(findUser);
    }

    public void signUp(SignUpRequestDto dto) {

        UserEntity findUser = null;

        findUser = userRepository.findByUserId(dto.getUserId());
        if (findUser != null) {
            throw new UserServiceException(ErrorCode.USER_ID_DUPLICATION_ERROR);
        }

        findUser = userRepository.findByEmail(dto.getEmail());
        if (findUser != null) {
            throw new UserServiceException(ErrorCode.USER_EMAIL_DUPLICATION_ERROR);
        }

        UserEntity userEntity = new UserEntity(dto);
        userRepository.save(userEntity);
    }

    public void deleteUser(String userId) {

        UserEntity findUser = userRepository.findByUserId(userId);

        if (findUser == null) {
            throw new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        // 로그인 구현 후 요청한 유저가 삭제하려는 유저와 동일한지 확인 로직

        userRepository.delete(findUser);
    }

    public UpdateUserResponseDto updateUser(String userId, UpdateUserRequestDto dto) {

        UserEntity findUser = userRepository.findByUserId(userId);

        if (findUser == null) {
            throw new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        // 로그인 구현 후 요청한 유저가 수정하려는 유저와 동일한지 확인 로직

        findUser.update(dto);
        userRepository.save(findUser);
        return new UpdateUserResponseDto(findUser);
    }
}
