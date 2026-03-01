package org.simarro.rag_daw.exception;

import lombok.Getter;

@Getter
public class FiltroException  extends Exception{
    private final String errorCode;
    private final String message;
    private String detailedMessage;

    public FiltroException(String errorCode,String message,String detailedMessage) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.detailedMessage=detailedMessage;
    }
}
