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

    //Record
    RECORD_NOT_FOUND_ERROR(400, "R001", "기록을 찾을 수 없음");

    private final int status;
    private final String code;
    private final String message;
}