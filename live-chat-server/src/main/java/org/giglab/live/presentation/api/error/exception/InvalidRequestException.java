package org.giglab.live.presentation.api.error.exception;

/**
 * 잘못된 요청 예외
 * 
 * @author : JAKE
 * @date : 26. 1. 11.
 */
public class InvalidRequestException extends RuntimeException {
    
    public InvalidRequestException(String message) {
        super(message);
    }
    
    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
