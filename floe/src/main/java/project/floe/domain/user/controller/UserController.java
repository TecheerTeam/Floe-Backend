package project.floe.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.floe.domain.user.dto.request.UserSignUpRequest;
import project.floe.domain.user.dto.request.UpdateUserRequestDto;
import project.floe.domain.user.dto.response.GetUserResponseDto;
import project.floe.domain.user.dto.response.UpdateUserResponseDto;
import project.floe.domain.user.service.UserService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    ResponseEntity<ResultResponse> getUser(
            @PathVariable("userId") String userId
    ) {
        GetUserResponseDto data = userService.getUser(userId);
        ResultResponse response = ResultResponse.of(ResultCode.USER_GET_SUCCESS, data);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    ResponseEntity<ResultResponse> signUp(
            @RequestBody UserSignUpRequest dto
    ) {
        userService.signUp(dto);
        ResultResponse response = ResultResponse.of(ResultCode.USER_CREATE_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{userId}")
    ResponseEntity<ResultResponse> deleteUser(
        @PathVariable("userId") String userId
    ){
        userService.deleteUser(userId);
        ResultResponse response = ResultResponse.of(ResultCode.USER_DELETE_SUCCESS);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{userId}")
    ResponseEntity<ResultResponse> updateUser(
            @PathVariable("userId") String userId,
            @RequestBody UpdateUserRequestDto dto
            ){
        UpdateUserResponseDto responseDto = userService.updateUser(userId, dto);
        ResultResponse response = ResultResponse.of(ResultCode.USER_UPDATE_SUCCESS,responseDto);
        return ResponseEntity.ok().body(response);
    }
}
