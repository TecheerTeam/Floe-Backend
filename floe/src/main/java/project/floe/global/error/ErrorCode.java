package project.floe.global.error;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * {주체}_{이유} message 는 동사 명사형으로 마무리
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 도메인별로 구분

    // Global
    INTERNAL_SERVER_ERROR(500, "G001", "서버 오류"),
    INPUT_INVALID_VALUE(409, "G002", "잘못된 입력"),
    ACCESS_INVALID_VALUE(400, "G003", "잘못된 접근"),

    // Media
    S3_UPLOAD_FAIL_ERROR(500, "M001", "파일을 S3에 업로드할 수 없음"),
    FILE_NAME_NOT_FOUND_ERROR(400, "M002", "파일 이름을 찾을 수 없음"),
    CANNOT_CONVERT_TO_BYTE(500, "M003", "파일을 바이트 배열로 변환할 수 없음"),
    MEDIA_NOT_FOUND_ERROR(400,"M004", "미디어 파일을 찾을 수 없음"),
    UPDATE_FILE_SEQUENCE_MISMATCH(400, "M005", "업데이트된 파일 순서와, 넘겨준 파일의 위치가 맞지 않음"),
    // Record
    RECORD_NOT_FOUND_ERROR(404, "R001", "기록을 찾을 수 없음"),
    RECORD_DELETED_OR_NOT_EXIST(404,"R002" ,"이미 삭제되거나 존재하지 않는 기록" ),

    // User
    USER_NOT_FOUND_ERROR(404,"U001","유저를 찾을 수 없음"),
    USER_ID_DUPLICATION_ERROR(400,"U002","중복된 아이디"),
    USER_EMAIL_DUPLICATION_ERROR(400,"U003","중복된 이메일"),

    // Record Like
    RECORD_ALREADY_LIKED_ERROR(400,"RL01","이미 좋아요 했음")
    ;

    private final int status;
    private final String code;
    private final String message;
}