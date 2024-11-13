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
    CANNOT_SET_METADATA(500, "M004", "파일의 메타데이터를 설정할 수 없음"),

    // Record
    RECORD_NOT_FOUND_ERROR(400, "R001", "기록을 찾을 수 없음");

    private final int status;
    private final String code;
    private final String message;
}
