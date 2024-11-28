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
    RECORD_CREATE_SUCCESS("R001", "기록 생성 성공"),
    RECORD_DELETE_SUCCESS("R002", "기록 제거 성공"),
    DETAIL_RECORD_GET_SUCCESS("R003","개별 기록 조회 성공" ),
    Record_PAGING_GET_SUCCESS("R004","페이징된 전체 기록 조회 성공" ),
    RECORD_MODIFY_SUCCESS("R005", "기록 수정 성공"),

    // User
    USER_CREATE_SUCCESS("U001","유저 생성 성공"),
    USER_GET_SUCCESS("U002","유저 조회 성공"),
    USER_DELETE_SUCCESS("U003","유저 삭제 성공"),
    USER_UPDATE_SUCCESS("U004","유저정보 수정 성공"),

    // Record Like
    RECORD_LIKE_COUNT_GET_SUCCESS("RL01","기록 좋아요 개수 조회 성공"),
    RECORD_LIKE_POST_SUCCESS("RL02","기록 좋아요 추가 성공"),
    RECORD_LIKE_DELETE_SUCCESS("RL03","기록 좋아요 삭제 성공"),
    RECORD_LIKE_LIST_GET_SUCCESS("RL04","기록 좋아요 유저 목록 조회 성공")
    ;

    private final String code;
    private final String message;
}