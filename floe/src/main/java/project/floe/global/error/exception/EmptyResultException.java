package project.floe.global.error.exception;

import lombok.Getter;
import org.springframework.dao.EmptyResultDataAccessException;
import project.floe.global.error.ErrorCode;

@Getter
public class EmptyResultException extends EmptyResultDataAccessException {
    private final ErrorCode errorCode;

    public EmptyResultException(ErrorCode errorCode) {
        super(errorCode.getMessage(), 0);
        this.errorCode = errorCode;
    }}
