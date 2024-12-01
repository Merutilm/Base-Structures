package kr.merutilm.base.exception;

import java.io.Serial;

public final class EmptyListException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -3469170810383672760L;

    public EmptyListException(){
        super();
    }
    public EmptyListException(String s){
        super(s);
    }
}
