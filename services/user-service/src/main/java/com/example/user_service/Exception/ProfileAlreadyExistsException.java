package com.example.user_service.Exception;

public class ProfileAlreadyExistsException extends RuntimeException{
    public ProfileAlreadyExistsException(String message){
        super(message);
    }
}
