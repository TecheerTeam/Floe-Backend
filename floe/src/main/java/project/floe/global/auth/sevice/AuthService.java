package project.floe.global.auth.sevice;

import org.springframework.http.ResponseEntity;

public interface AuthService {

    UserRegisterResponse register(UserRegisterRequest userRegisterDto);

    JwtToken login(UserLoginRequest userLoginDto);

    ResponseEntity<ResultResponse> oauth2Login(String code, String state);
}
