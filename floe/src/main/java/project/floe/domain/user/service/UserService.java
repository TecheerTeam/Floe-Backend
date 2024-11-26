package project.floe.domain.user.service;


import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.floe.domain.user.dto.request.UserOAuthSignUpRequest;
import project.floe.domain.user.dto.request.UserSignUpRequest;
import project.floe.domain.user.dto.request.UserUpdateRequest;
import project.floe.domain.user.dto.response.GetUserResponseDto;
import project.floe.domain.user.dto.response.UpdateUserResponseDto;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.auth.jwt.service.JwtService;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.UserServiceException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public GetUserResponseDto getUser(String email) {

        User findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));

        return new GetUserResponseDto(findUser);
    }

    public void oAuthSignUp(HttpServletRequest request, UserOAuthSignUpRequest dto) {
        String userEmail = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST)
        );
        User user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new UserServiceException(ErrorCode.EMAIL_NOT_FOUND_ERROR)
        );

        user.oAuthSignUp(dto);
    }

    public void signUp(UserSignUpRequest dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserServiceException(ErrorCode.USER_EMAIL_DUPLICATION_ERROR);
        }

        if (userRepository.findByNickname(dto.getNickname()).isPresent()){
            throw new UserServiceException(ErrorCode.USER_NICKNAME_DUPLICATION_ERROR);
        }
        User user = User.from(dto);
        user.passwordEncode(passwordEncoder);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String email) {

        User findUser = userRepository.findByEmail(email)
                .orElseThrow(()->new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));

        // 로그인 구현 후 요청한 유저가 삭제하려는 유저와 동일한지 확인 로직

        userRepository.delete(findUser);
    }

    @Transactional
    public UpdateUserResponseDto updateUser(String email, UserUpdateRequest dto) {

        User findUserById = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));
        // 로그인 구현 후 요청한 유저가 수정하려는 유저와 동일한지 확인 로직

        findUserById.update(dto);
        userRepository.save(findUserById);
        return new UpdateUserResponseDto(findUserById);
    }
}
