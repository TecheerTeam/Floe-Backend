package project.floe.global.error.exception;

import lombok.Getter;
import project.floe.global.error.ErrorCode;


@Getter
public class CommentException extends BusinessException {

    private final ErrorCode errorCode;

    public CommentException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

}