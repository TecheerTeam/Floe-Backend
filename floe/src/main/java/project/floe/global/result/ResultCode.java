package project.floe.global.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** {행위}_{목적어}_{성공여부} message 는 동사 명사형으로 마무리 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // Record
    RECORD_CREATE_SUCCESS("R001", "기록 생성 성공"),

    // User
    USER_CREATE_SUCCESS("U001","유저 생성 성공"),
    USER_GET_SUCCESS("U002","유저 조회 성공"),
    USER_DELETE_SUCCESS("U003","유저 삭제 성공"),
    USER_UPDATE_SUCCESS("U004","유저정보 수정 성공"),
    ;

    private final String code;
    private final String message;
}
