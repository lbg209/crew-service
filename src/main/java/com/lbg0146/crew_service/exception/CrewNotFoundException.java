package com.lbg0146.crew_service.exception;

import org.springframework.http.HttpStatus;

public class CrewNotFoundException extends BusinessException{

    public CrewNotFoundException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 크루입니다.");
    }
}
