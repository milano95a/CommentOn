package com.example.aj.commenton.exception;

public class AuthenticationClientNotFound extends Exception {

    String mMmessage;

    public AuthenticationClientNotFound(String message) {
        this.mMmessage = message;
    }

    @Override
    public String getMessage() {
        return mMmessage;
    }
}
