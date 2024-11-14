package project.floe.global.error.exception;

import project.floe.global.error.ErrorCode;

public class UserServiceException extends BusinessException{

    public UserServiceException(ErrorCode errorCode) {
        super(errorCode);
    }
}
