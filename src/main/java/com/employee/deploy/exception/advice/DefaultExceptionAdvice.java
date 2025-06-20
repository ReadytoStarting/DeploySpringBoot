package com.employee.deploy.exception.advice;

import com.employee.deploy.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class DefaultExceptionAdvice {

//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ErrorObject> handleResourceNotFoundException(ResourceNotFoundException ex) {
//        ErrorObject errorObject = new ErrorObject();
//        errorObject.setStatusCode(ex.getHttpStatus().value());
//        errorObject.setMessage(ex.getMessage());
//
//        log.error(ex.getMessage(), ex);
//
//        return new ResponseEntity<ErrorObject>(errorObject, HttpStatusCode.valueOf(ex.getHttpStatus().value()));
//    }

    /*
        Spring6 버전에 추가된 ProblemDetail 객체에 에러정보를 담아서 리턴하는 방법
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    protected ProblemDetail handleException(ResourceNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getHttpStatus());
        problemDetail.setTitle("Not Found");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setProperty("errorCategory", "Generic");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    //숫자타입의 값에 문자열타입의 값을 입력으로 받았을때 발생하는 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleException(HttpMessageNotReadableException e) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("message", e.getMessage());
        result.put("httpStatus", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorObject> handleException(RuntimeException e) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorObject.setMessage(e.getMessage());

        log.error(e.getMessage(), e);

        return new ResponseEntity<ErrorObject>(errorObject, HttpStatusCode.valueOf(500));
    }
}