package kr.merutilm.base.parallel;

import java.util.concurrent.atomic.AtomicInteger;

import kr.merutilm.base.exception.IllegalRenderStateException;


public final class RenderState {

    private AtomicInteger id = new AtomicInteger();
    /**
     * 고유 값과 ID가 일치하지 않으면 예외를 발생시킵니다.
     * @see RenderState#createBreakpoint()
     */
    public void tryBreak(int id) throws IllegalRenderStateException {
        if (id != this.id.get()) {
            throw new IllegalRenderStateException("Render ID Changed during rendering");
        }
    }
    /**
     * 고유 값
     */
    public int getId() {
        return id.get();
    }
    /**
     * id를 증가시킵니다.
     * 해당 메서드가 호출 된 후, 기존에 생성된 셰이더는 tryBreak() 구문에서 예외가 발생합니다.
     */
    public void createBreakpoint(){
        id.getAndIncrement();
    }
}
