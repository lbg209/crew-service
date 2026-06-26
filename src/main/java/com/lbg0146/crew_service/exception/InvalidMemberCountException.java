package com.lbg0146.crew_service.exception;

import org.springframework.http.HttpStatus;

public class InvalidMemberCountException extends BusinessException{

    public InvalidMemberCountException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
