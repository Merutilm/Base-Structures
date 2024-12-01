package kr.merutilm.base.exception;

import java.io.Serial;

public final class ThreadLockException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -99611161360770158L;

    public ThreadLockException(){
        super();
    }

    public ThreadLockException(String s){
        super(s);
    }
}
