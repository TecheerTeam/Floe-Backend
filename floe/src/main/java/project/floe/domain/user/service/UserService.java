package project.floe.domain.user.service;


import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.floe.domain.user.dto.request.UserSignUpRequest;
import project.floe.domain.user.dto.request.UserUpdateRequest;
import project.floe.domain.user.dto.response.GetUserResponseDto;
import project.floe.domain.user.dto.response.UpdateUserResponseDto;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.UserServiceException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public GetUserResponseDto getUser(String userId) {

        User findUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));

        return new GetUserResponseDto(findUser);
    }

    public void signUp(UserSignUpRequest dto) {

        if (userRepository.findByUserId(dto.getUserId()).isPresent()) {
            throw new UserServiceException(ErrorCode.USER_ID_DUPLICATION_ERROR);
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserServiceException(ErrorCode.USER_EMAIL_DUPLICATION_ERROR);
        }

        if (userRepository.findByNickName(dto.getNickName()).isPresent()){
            throw new UserServiceException(ErrorCode.USER_NICKNAME_DUPLICATION_ERROR);
        }
        User user = User.from(dto);
        user.passwordEncode(passwordEncoder);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String userId) {

        User findUser = userRepository.findByUserId(userId)
                .orElseThrow(()->new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));

        // 로그인 구현 후 요청한 유저가 삭제하려는 유저와 동일한지 확인 로직

        userRepository.delete(findUser);
    }

    @Transactional
    public UpdateUserResponseDto updateUser(String userId, UserUpdateRequest dto) {

        User findUserById = userRepository.findByUserId(userId)
                .orElseThrow(()-> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));

        if (dto.getEmail() != null) {
            Optional<User> findUserByEmail = userRepository.findByEmail(dto.getEmail());

            if (findUserByEmail.isPresent() && !userId.equals(findUserByEmail.get().getUserId())) {
                throw new UserServiceException(ErrorCode.USER_EMAIL_DUPLICATION_ERROR);
            }
        }

        // 로그인 구현 후 요청한 유저가 수정하려는 유저와 동일한지 확인 로직

        findUserById.update(dto);
        userRepository.save(findUserById);
        return new UpdateUserResponseDto(findUserById);
    }
}
