package project.floe.global.result;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * {행위}_{목적어}_{성공여부} message 는 동사 명사형으로 마무리
 */

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
    DETAIL_RECORD_GET_SUCCESS("R003", "개별 기록 조회 성공"),
    RECORD_PAGING_GET_SUCCESS("R004", "페이징된 전체 기록 조회 성공"),
    RECORD_MODIFY_SUCCESS("R005", "기록 수정 성공"),
    GET_USER_RECORDS_SUCCESS("R009", "유저 전체 게시물 조회 성공"),

    // Record Search
    RECORD_SEARCH_SUCCESS("R006", "페이징된 기록 검색 성공"),

    // User
    USER_CREATE_SUCCESS("U001", "유저 생성 성공"),
    USER_GET_SUCCESS("U002", "유저 조회 성공"),
    USER_DELETE_SUCCESS("U003", "유저 삭제 성공"),
    USER_UPDATE_SUCCESS("U004", "유저정보 수정 성공"),
    USER_OAUTH_SIGNUP_SUCCESS("U005", "소셜 로그인 유저 회원가입 성공"),
    USER_LOGIN_FAIL("U006", "로그인 실패"),
    USER_PROFILE_IMAGE_UPDATE_SUCCESS("U007", "프로필 사진 업데이트 성공"),

    // User Follow
    USER_FOLLOW_CREATE_SUCCESS("UF01", "유저 팔로우 생성 성공"),
    USER_FOLLOW_DELETE_SUCCESS("UF02", "유저 팔로우 삭제 성공"),
    USER_FOLLOW_GET_SUCCESS("UF03", "유저 팔로우 조회 성공"),
    USER_FOLLOW_COUNT_GET_SUCCESS("UF04", "유저 팔로우 수 조회 성공"),
    USER_FOLLOW_STATUS_GET_SUCCESS("UF05", "유저 팔로우 상태 조회 성공"),


    // Record Save
    RECORD_SAVE_POST_SUCCESS("RS02", "기록 저장 성공"),
    RECORD_SAVE_DELETE_SUCCESS("RS03", "기록 저장 삭제 성공"),
    RECORD_SAVE_COUNT_GET_SUCCESS("RS01", "기록 저장 개수 조회 성공"),
    RECORD_SAVE_LIST_GET_SUCCESS("RS04", "저장한 기록 목록 조회 성공"),
    RECORD_SAVE_CHECK_SUCCESS("RS05", "기록 저장 여부 확인 성공"),

    // Record Like
    RECORD_LIKE_COUNT_GET_SUCCESS("RL01", "기록 좋아요 개수 조회 성공"),
    RECORD_LIKE_POST_SUCCESS("RL02", "기록 좋아요 추가 성공"),
    RECORD_LIKE_DELETE_SUCCESS("RL03", "기록 좋아요 삭제 성공"),
    RECORD_LIKE_LIST_GET_SUCCESS("RL04", "기록 좋아요 유저 목록 조회 성공"),
    RECORD_LIKED_LIST_GET_SUCCESS("RL05", "좋아요한 기록 목록 조회 성공"),

    // CommentLike
    Comment_LIKE_COUNT_GET_SUCCESS("CL01", "댓글 좋아요 개수 조회 성공"),
    Comment_LIKE_CREATE_SUCCESS("CL02", "댓글 좋아요 추가 성공"),
    Comment_LIKE_DELETE_SUCCESS("CL03", "댓글 좋아요 삭제 성공"),
    Comment_LIKE_USERS_GET_SUCCESS("CL04", "댓글 좋아요 유저 목록 조회 성공"),

    // Tag
    GET_TAG_STATISTICS_SUCCESS("T001", "전체 태그 통계 조회 성공"),
    GET_USER_TAG_STATISTICS_SUCCESS("T002", "유저 태그 통계 조회 성공"),

    // notification
    NOTIFICATION_CONNECT_SUCCESS("N001", "알림 연결 성공"),
    NOTIFICATION_LIST_GET_SUCCESS("N002", "전체 알림 조회 성공"),
    NOTIFICATION_READ_SUCCESS("N003", "해당 알림 읽음 처리 성공"),
    NOTIFICATION_ALL_READ_SUCCESS("N004", "읽지 않은 모든 알림 읽음 처리 성공"),
    NOTIFICATION_DELETE_SUCCESS("N005", "해당 알림 삭제 성공"),
    NOTIFICATION_ALL_DELETE_SUCCESS("N006", "읽은 모든 알림 삭제 성공"),
    NOTIFICATION_UNREAD_COUNT_GET_SUCCESS("N007", "읽지 않은 알림 개수 조회 성공"),
    ;


    private final String code;
    private final String message;
}