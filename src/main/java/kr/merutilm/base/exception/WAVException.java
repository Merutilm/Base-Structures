package kr.merutilm.base.exception;

import java.io.Serial;

public final class WAVException extends Exception{
    @Serial
    private static final long serialVersionUID = -5496728195193541501L;

    public WAVException(){
        super();
    }
    public WAVException(String s){
        super(s);
    }
}
