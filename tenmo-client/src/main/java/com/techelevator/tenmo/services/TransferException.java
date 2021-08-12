package com.techelevator.tenmo.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = HttpStatus.I_AM_A_TEAPOT, reason = "Insufficient Balance.")
public class TransferException extends Exception {
    private static final long serialVersionUID = 1L;

    public TransferException(String message) {
        super(message);
    }

}
