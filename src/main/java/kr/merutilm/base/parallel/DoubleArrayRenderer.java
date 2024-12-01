package kr.merutilm.base.parallel;

import kr.merutilm.base.exception.IllegalRenderStateException;

public interface DoubleArrayRenderer {
    double execute(int x, int y, int xRes, int yRes, double rx, double ry, int i, double c, double t) throws IllegalRenderStateException;
    
    default boolean isValid(){
        return true;
    }
}
