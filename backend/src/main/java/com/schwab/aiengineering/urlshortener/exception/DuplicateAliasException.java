package com.schwab.aiengineering.urlshortener.exception;

public class DuplicateAliasException extends RuntimeException {

    public DuplicateAliasException(String message) {
        super(message);
    }

}