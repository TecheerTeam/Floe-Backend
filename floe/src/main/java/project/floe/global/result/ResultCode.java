package project.floe.global.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** {행위}_{목적어}_{성공여부} message 는 동사 명사형으로 마무리 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // Record
    RECORD_CREATE_SUCCESS("R001", "기록 생성 성공");

    private final String code;
    private final String message;
}
