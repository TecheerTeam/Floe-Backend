package project.floe.domain.user.service;


import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.floe.domain.record.service.MediaService;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final MediaService mediaService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void updateProfileImage(HttpServletRequest request, MultipartFile profileImage) {
        String userEmail = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST)
        );
        User user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new UserServiceException(ErrorCode.EMAIL_NOT_FOUND_ERROR)
        );

        if (profileImage == null){ // 프로필 이미지 비우길 원한다면 비워줌
            user.updateProfileImage(null);
        }
        else {
            String updatedUrl = mediaService.uploadToS3(profileImage);
            user.updateProfileImage(updatedUrl);
        }
    }

    public GetUserResponseDto getUser(HttpServletRequest request) {
        String userEmail = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST)
        );
        User findUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));
        return GetUserResponseDto.from(findUser);
    }

    @Transactional
    public void oAuthSignUp(HttpServletRequest request, UserOAuthSignUpRequest dto) {
        String userEmail = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST)
        );
        User user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new UserServiceException(ErrorCode.EMAIL_NOT_FOUND_ERROR)
        );

        user.oAuthSignUp(dto);
    }

    @Transactional
    public void signUp(UserSignUpRequest dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserServiceException(ErrorCode.USER_EMAIL_DUPLICATION_ERROR);
        }

        if (userRepository.findByNickname(dto.getNickname()).isPresent()) {
            throw new UserServiceException(ErrorCode.USER_NICKNAME_DUPLICATION_ERROR);
        }
        User user = User.from(dto);
        user.passwordEncode(passwordEncoder);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(HttpServletRequest request) {

        String userEmail = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST)
        );

        User findUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));

        log.info("delete User: {}", userEmail);
        userRepository.delete(findUser);
    }

    @Transactional
    public UpdateUserResponseDto updateUser(HttpServletRequest request, UserUpdateRequest dto) {
        String userEmail = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST)
        );

        User findUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR));

        findUser.update(dto);
        userRepository.save(findUser);
        return UpdateUserResponseDto.from(findUser);
    }
}
