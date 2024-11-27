package project.floe.global.result;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * {행위}_{목적어}_{성공여부} message 는 동사 명사형으로 마무리
 */

@Getter
@AllArgsConstructor
public enum ResultCode {

    // Record
    RECORD_CREATE_SUCCESS("R001", "기록 생성 성공"),
    RECORD_DELETE_SUCCESS("R002", "기록 제거 성공"),
    DETAIL_RECORD_GET_SUCCESS("R003", "개별 기록 조회 성공"),
    RECORD_PAGING_GET_SUCCESS("R004", "페이징된 전체 기록 조회 성공"),
    RECORD_MODIFY_SUCCESS("R005", "기록 수정 성공"),

    // User
    USER_CREATE_SUCCESS("U001", "유저 생성 성공"),
    USER_GET_SUCCESS("U002", "유저 조회 성공"),
    USER_DELETE_SUCCESS("U003", "유저 삭제 성공"),
    USER_UPDATE_SUCCESS("U004", "유저정보 수정 성공"),
    USER_OAUTH_SIGNUP_SUCCESS("U005", "소셜 로그인 유저 회원가입 성공"),
    USER_LOGIN_FAIL("U006", "로그인 실패"),
    USER_PROFILE_IMAGE_UPDATE_SUCCESS("U007", "프로필 사진 업데이트 성공");

    private final String code;
    private final String message;
}