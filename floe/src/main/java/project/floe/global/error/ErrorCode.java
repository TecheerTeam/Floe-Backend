package project.floe.global.error;


import lombok.AllArgsConstructor;
import lombok.Getter;

/** {주체}_{이유} message 는 동사 명사형으로 마무리 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 도메인별로 구분

    // Global
    INTERNAL_SERVER_ERROR(500, "G001", "서버 오류"),
    INPUT_INVALID_VALUE(409, "G002", "잘못된 입력"),
    ACCESS_INVALID_VALUE(400, "G003", "잘못된 접근"),

    // Record
    RECORD_NOT_FOUND_ERROR(400,"R001","기록을 찾을 수 없음"),

    //User
    USER_NOT_FOUND_ERROR(404,"U001","유저를 찾을 수 없음"),
    USER_ID_DUPLICATION_ERROR(400,"U002","중복된 아이디"),
    USER_EMAIL_DUPLICATION_ERROR(400,"U003","중복된 이메일")
    ;

    private final int status;
    private final String code;
    private final String message;
}