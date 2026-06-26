package com.lbg0146.crew_service.exception;

import org.springframework.http.HttpStatus;

public class DuplicateApplicationException extends BusinessException{

    public DuplicateApplicationException() {
        super(HttpStatus.CONFLICT, "이미 신청한 크루입니다.");
    }
}
