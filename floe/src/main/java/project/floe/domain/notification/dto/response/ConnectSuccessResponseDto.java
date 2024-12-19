package project.floe.domain.notification.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project.floe.global.result.ResultCode;

@Getter
@NoArgsConstructor
public class ConnectSuccessResponseDto {
    private String code;
    private String message;

    public ConnectSuccessResponseDto(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
}
