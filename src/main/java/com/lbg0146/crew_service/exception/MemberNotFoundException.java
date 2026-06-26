package com.lbg0146.crew_service.exception;

import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends BusinessException{
    public MemberNotFoundException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다.");
    }
}
