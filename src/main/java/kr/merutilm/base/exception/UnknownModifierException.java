package kr.merutilm.base.exception;

import java.io.Serial;

public final class UnknownModifierException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -2396605644928451539L;

    public UnknownModifierException(String s){
        super(s);
    }
}
