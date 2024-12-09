package edu.miu.horelo.advice;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class UserAuthExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleFailedAuth(BadCredentialsException badCredentialsException) {
        var errorMsgMap = new HashMap<String, String>();
        errorMsgMap.put("errorMsg", badCredentialsException.getMessage());
        return errorMsgMap;
    }

}
