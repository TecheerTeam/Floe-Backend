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


    // Comment
    COMMENT_PARENT_NOT_FOUND_ERROR(404, "C002", "부모 댓글을 찾을 수 없음"),
    COMMENT_CANNOT_REPLY_TO_DELETED(409, "C003", "삭제된 댓글에는 답글을 작성할 수 없음"),
    COMMENT_NOT_FOUND_ERROR(404, "C004", "댓글을 찾을 수 없음"),
    COMMENT_DELETED_ERROR(404, "C005", "삭제된 댓글입니다"),

    //Comment Like
    COMMENT_ALREADY_LIKED_ERROR(400, "CL01", "이미 좋아요 한 댓글"),
    COMMENT_LIKE_NOT_FOUND_ERROR(404, "CL02", "해당 댓글에 좋아요를 하지 않음"),

    // Media
    S3_UPLOAD_FAIL_ERROR(500, "M001", "파일을 S3에 업로드할 수 없음"),
    FILE_NAME_NOT_FOUND_ERROR(400, "M002", "파일 이름을 찾을 수 없음"),
    CANNOT_CONVERT_TO_BYTE(500, "M003", "파일을 바이트 배열로 변환할 수 없음"),
    MEDIA_NOT_FOUND_ERROR(400, "M004", "미디어 파일을 찾을 수 없음"),
    UPDATE_FILE_SEQUENCE_MISMATCH(400, "M005", "업데이트된 파일 순서와, 넘겨준 파일의 위치가 맞지 않음"),
    // Record
    RECORD_NOT_FOUND_ERROR(404, "R001", "기록을 찾을 수 없음"),
    RECORD_DELETED_OR_NOT_EXIST(404, "R002", "이미 삭제되거나 존재하지 않는 기록"),

    // Record Search
    RECORD_SEARCH_EMPTY_ERROR(400, "RS003", "검색어가 비어있음"),

    //User
    USER_NOT_FOUND_ERROR(404, "U001", "유저를 찾을 수 없음"),
    USER_ID_DUPLICATION_ERROR(400, "U002", "중복된 아이디"),
    USER_EMAIL_DUPLICATION_ERROR(400, "U003", "중복된 이메일"),
    USER_NICKNAME_DUPLICATION_ERROR(400, "U004", "중복된 닉네임"),
    EMAIL_NOT_FOUND_ERROR(400, "U005", "해당 이메일이 존재하지 않음"),

    //User Follow
    USER_FOLLOW_NOT_FOUND_ERROR(404, "UF001", "팔로우를 찾을 수 없음"),
    USER_FOLLOW_ALREADY_EXIST_ERROR(400, "UF002", "이미 팔로우한 유저"),
    USER_FOLLOW_SELF_ERROR(400, "UF004", "자신을 팔로우 할 수 없음"),


    //Auth
    TOKEN_ACCESS_NOT_EXIST(401, "A001", "토큰을 찾을 수 없음"),

    // Record Save
    RECORD_ALREADY_SAVED_ERROR(400, "RS01", "이미 저장한 기록"),
    RECORD_SAVED_NOT_FOUNT_ERROR(404, "RS02", "해당 기록을 저장 하지 않음"),

    // Record Like
    RECORD_ALREADY_LIKED_ERROR(400, "RL01", "이미 좋아요 한 기록"),
    RECORD_LIKE_NOT_FOUNT_ERROR(404, "RL02", "해당 기록에 좋아요를 하지 않음"),

    // notification
    NOTIFICATION_NOT_OWNED_BY_USER(403, "N005", "해당 알림은 유저의 알림이 아님"),
    NOTIFICATION_ALREADY_READ_ERROR(400, "N004", "해당 알림은 이미 읽음"),
    NOTIFICATION_NOT_FOUND_ERROR(404, "N003", "해당 알림을 찾을 수 없음"),
    SSE_EMITTER_SEND_FAILED(500, "N002", "sse 메세지 전송 실패"),
    REDIS_MESSAGE_DESERIALIZATION_FAILED(500, "N001", "redis 메세지 역직렬화 실패"),
    ;


    private final int status;
    private final String code;
    private final String message;
}