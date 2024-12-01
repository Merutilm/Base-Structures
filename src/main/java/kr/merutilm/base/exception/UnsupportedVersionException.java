package kr.merutilm.base.exception;

import java.io.Serial;

public final class UnsupportedVersionException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 5690740637804847458L;

    public UnsupportedVersionException(String s){
        super(s);
    }
}
