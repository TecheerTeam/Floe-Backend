package project.floe.global.error.exception;

import lombok.Getter;
import project.floe.global.error.ErrorCode;

@Getter
public class EmptyKeywordException extends BusinessException {

    private final ErrorCode errorCode;

    public EmptyKeywordException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

}

