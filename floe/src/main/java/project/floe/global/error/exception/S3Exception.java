package project.floe.global.error.exception;

import lombok.Getter;
import project.floe.global.error.ErrorCode;

@Getter
public class S3Exception extends RuntimeException{
    private final ErrorCode errorCode;

    public S3Exception(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
