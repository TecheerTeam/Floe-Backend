package project.floe.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.floe.global.error.exception.BusinessException;
import project.floe.global.error.exception.EmptyResultException;
import project.floe.global.error.exception.S3Exception;
import project.floe.global.error.exception.UserServiceException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error(e.getMessage(), e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler // @RestController에서 발생한 에러 핸들러, @Controller 관련 어노테이션이 있어야지만 사용 가능
    protected ResponseEntity<ErrorResponse> handleRuntimeException(BusinessException e) {
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse response = makeErrorResponse(errorCode);
        log.warn(e.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(S3Exception.class)
    protected ResponseEntity<ErrorResponse> handleS3Exception(S3Exception e) {
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse response = makeErrorResponse(errorCode);
        log.error(e.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(EmptyResultException.class)
    protected ResponseEntity<ErrorResponse> handleS3Exception(EmptyResultException e) {
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse response = makeErrorResponse(errorCode);
        log.error(e.getMessage());

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ErrorResponse> handleUserServiceException(UserServiceException e){
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.of(errorCode);
        log.warn(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INPUT_INVALID_VALUE, e.getBindingResult());
        log.warn(e.getMessage());
        return ResponseEntity.status(ErrorCode.INPUT_INVALID_VALUE.getStatus()).body(response);
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .errorMessage(errorCode.getMessage())
                .businessCode(errorCode.getCode())
                .build();
    }
}

