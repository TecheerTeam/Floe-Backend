package project.floe.global.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** {행위}_{목적어}_{성공여부} message 는 동사 명사형으로 마무리 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    // Comment
    COMMENT_CREATE_SUCCESS("C001", "댓글 생성 성공"),
    COMMENT_GET_SUCCESS("C002", "댓글 조회 성공"),
    COMMENT_DELETE_SUCCESS("C003", "댓글 삭제 성공"),
    COMMENT_UPDATE_SUCCESS("C004", "댓글 수정 성공"),

    // Record
    RECORD_CREATE_SUCCESS("R001", "기록 생성 성공");

    private final String code;
    private final String message;
}
