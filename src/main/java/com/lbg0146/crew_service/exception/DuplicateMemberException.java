package com.lbg0146.crew_service.exception;

import org.springframework.http.HttpStatus;

public class DuplicateMemberException extends BusinessException {
    public DuplicateMemberException() {
        super(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다.");
    }
}
