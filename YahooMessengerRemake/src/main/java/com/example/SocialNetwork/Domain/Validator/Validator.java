package com.example.SocialNetwork.Domain.Validator;


public interface Validator<T> {
    void validate(T object) throws ValidationException;
}
