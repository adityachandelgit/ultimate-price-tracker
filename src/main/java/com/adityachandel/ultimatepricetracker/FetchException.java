package com.adityachandel.ultimatepricetracker;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FetchException extends RuntimeException {
    public FetchException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
