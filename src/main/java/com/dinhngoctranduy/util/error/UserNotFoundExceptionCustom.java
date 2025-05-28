package com.dinhngoctranduy.util.error;

public class UserNotFoundExceptionCustom extends Exception { // Hoặc extends RuntimeException
    public UserNotFoundExceptionCustom(String message) {
        super(message);
    }
}