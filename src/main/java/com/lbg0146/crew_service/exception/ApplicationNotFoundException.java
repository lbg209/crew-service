package com.lbg0146.crew_service.exception;

import org.springframework.http.HttpStatus;

public class ApplicationNotFoundException extends BusinessException{
    public ApplicationNotFoundException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 신청입니다.");
    }
}
