package kr.merutilm.base.exception;

import java.io.Serial;

public final class IllegalFormulaException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 8159625986841487740L;

    public IllegalFormulaException(String s){
        super(s);
    }
}
