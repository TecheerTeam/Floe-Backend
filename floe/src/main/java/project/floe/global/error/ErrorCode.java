package project.floe.global.error;


import lombok.AllArgsConstructor;
import lombok.Getter;

/** {주체}_{이유} message 는 동사 명사형으로 마무리 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 도메인별로 구분
    // 예시
    // Record 도메인
    RECORD_NOT_FOUND_ERROR(400,"R001","기록을 찾을 수 없음");

    private final int status;
    private final String code;
    private final String message;
}
