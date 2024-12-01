package kr.merutilm.base.parallel;

import kr.merutilm.base.exception.IllegalRenderStateException;

public interface ProcessVisualizer{
    void run(double progress) throws IllegalRenderStateException, InterruptedException;
}
