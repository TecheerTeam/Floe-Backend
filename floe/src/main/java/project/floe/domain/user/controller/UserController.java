package project.floe.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import project.floe.domain.user.dto.request.UserSignUpRequest;
import project.floe.domain.user.dto.request.UserUpdateRequest;
import project.floe.domain.user.dto.response.GetUserResponseDto;
import project.floe.domain.user.dto.response.UpdateUserResponseDto;
import project.floe.domain.user.service.UserService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@Tag(name = "UserController", description = "유저 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "회원 정보 조회",
            description = "회원 정보 조회"
    )
    @GetMapping
    ResponseEntity<ResultResponse> getUser(
            HttpServletRequest request
    ) {
        GetUserResponseDto data = userService.getUser(request);
        ResultResponse response = ResultResponse.of(ResultCode.USER_GET_SUCCESS, data);
        return ResponseEntity.ok().body(response);
    }

    @Operation(
            summary = "회원 삭제",
            description = "회원 삭제"
    )
    @DeleteMapping
    ResponseEntity<ResultResponse> deleteUser(HttpServletRequest request) {
        userService.deleteUser(request);
        ResultResponse response = ResultResponse.of(ResultCode.USER_DELETE_SUCCESS);
        return ResponseEntity.ok().body(response);
    }

    @Operation(
            summary = "회원 정보 수정",
            description = "회원 정보 수정"
    )
    @PatchMapping("/update")
    ResponseEntity<ResultResponse> updateUser(
            HttpServletRequest request,
            @RequestBody UserUpdateRequest dto
    ) {
        UpdateUserResponseDto responseDto = userService.updateUser(request, dto);
        ResultResponse response = ResultResponse.of(ResultCode.USER_UPDATE_SUCCESS, responseDto);
        return ResponseEntity.ok().body(response);
    }

    @Operation(
            summary = "회원 프로필 사진 수정",
            description = "회원 프로필 사진 수정"
    )
    @PutMapping("/profile")
    public ResponseEntity<ResultResponse> updateProfile(
            HttpServletRequest request,
            @RequestPart("profileImage") MultipartFile profileImage) {
        userService.updateProfileImage(request, profileImage);
        return ResponseEntity.ok().body(ResultResponse.of(ResultCode.USER_PROFILE_IMAGE_UPDATE_SUCCESS));
    }
}
