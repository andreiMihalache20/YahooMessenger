package com.example.SocialNetwork.Domain.Validator;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

}
