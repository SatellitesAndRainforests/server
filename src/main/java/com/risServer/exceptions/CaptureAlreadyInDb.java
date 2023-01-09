package com.risServer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class CaptureAlreadyInDb extends RuntimeException {
    public CaptureAlreadyInDb(String message) {
        super(message);
    }
}


