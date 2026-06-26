package com.lbg0146.crew_service.exception;

import org.springframework.http.HttpStatus;

public class RecruitmentClosedException extends BusinessException{
    public RecruitmentClosedException() {
        super(HttpStatus.BAD_REQUEST, "모집이 마감된 크루입니다.");
    }
}
