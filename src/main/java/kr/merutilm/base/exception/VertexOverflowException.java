package kr.merutilm.base.exception;

import java.io.Serial;

public final class VertexOverflowException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -5422843161673430391L;

    public VertexOverflowException(String s){
        super(s);
    }
}
