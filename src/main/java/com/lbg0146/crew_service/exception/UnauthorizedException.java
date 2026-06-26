package com.lbg0146.crew_service.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException() {
        super(HttpStatus.FORBIDDEN, "권한이 없습니다.");
    }

    public UnauthorizedException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
