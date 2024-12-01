package kr.merutilm.base.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import kr.merutilm.base.exception.IllegalRenderStateException;
import kr.merutilm.base.io.BitMap;
import kr.merutilm.base.io.BitMapImage;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.base.util.TaskManager;


public class ShaderDispatcher {
    private final RenderState renderState;
    private final int renderID;
    private final BitMap bitMap;
    private BitMap original;
    protected final double initTime;
    private boolean rendered = false;
    private final AtomicInteger renderedAmount = new AtomicInteger();

    private final List<ShaderRenderer> renderers = new ArrayList<>();

    /**
     * Init Shader Dispatcher
     *
     * @param renderState 렌더링에 필요한 고유 값을 정의합니다.
     * @param renderID    렌더링 고유 번호입니다. renderState와 고유 값이 일치하지 않으면 {@link IllegalRenderStateException 예외}를 발생시킵니다. 초기값은 0입니다.
     * @param bitMap      이미지의 픽셀 데이터입니다.
     * @see RenderState
     * @see BitMapImage#BitMapImage(String)
     */
    public ShaderDispatcher(RenderState renderState, int renderID, BitMap bitMap) throws IllegalRenderStateException{
        this.renderState = renderState;
        this.renderID = renderID;
        this.bitMap = bitMap;
        this.initTime = System.currentTimeMillis() / 1000.0;
        tryBreak();
    }

    public final void tryBreak() throws IllegalRenderStateException {
        renderState.tryBreak(renderID);
    }


    public synchronized void process(ProcessVisualizer visualizer, long intervalMS) {
        AtomicBoolean processing = new AtomicBoolean(true);
        Thread t = TaskManager.runTask(() -> {
            try {

                while (processing.get()) {
                    Thread.sleep(intervalMS);
                    tryBreak();
                    visualizer.run((double) renderedAmount.get() / bitMap.getLength() / renderers.size());
                }

            } catch (IllegalRenderStateException | InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        try {
            dispatch();
            processing.set(false);
            t.interrupt();
            t.join();
            tryBreak();
            visualizer.run(1);
        } catch (IllegalRenderStateException | InterruptedException e) {
            processing.set(false);
            Thread.currentThread().interrupt();
        }

    }

    /**
     * 셰이더를 객체 생성에 전달한 원본 canvas 배열에 반영합니다.
     *
     * @return 결과 배열
     * @throws InterruptedException        스레드가 대기 상태일 때 강제 종료될 경우 호출됩니다
     * @throws IllegalStateException       렌더러가 없을 때 호출됩니다
     */
    public synchronized void dispatch() throws InterruptedException {

        if (renderers.isEmpty()) {
            return;
        }

        if (rendered) {
            throw new IllegalStateException("Dispatcher can execute only once");
        }
        rendered = true;

        final double time = System.currentTimeMillis() / 1000.0 - this.initTime;
        final BitMap tex2DOriginal = this.original; //The elements of tex2D are unmodifiable.

        final int[] canvas = bitMap.getCanvas();


        for (ShaderRenderer renderer : renderers) {

            if (!renderer.isValid()) {
                continue;
            }

            boolean[] renderedPixels = new boolean[bitMap.getLength()];
            original = bitMap.cloneCanvas(); // update tex2D to the canvas with applied previous shaders
            final int rpy = bitMap.getHeight() / Runtime.getRuntime().availableProcessors() + 1;
            final int xRes = bitMap.getWidth();
            final int yRes = bitMap.getHeight();
            List<Thread> renderThreads = new ArrayList<>();


            for (int sy = 0; sy < yRes; sy += rpy) {

                int finalSy = sy;

                Thread t = new Thread(() -> {
                    try {
                        for (int y = 0; y < rpy; y++) {
                            for (int x = 0; x < xRes; x++) {
                                renderState.tryBreak(renderID);
                                int py = finalSy + y;

                                if (py >= yRes) {
                                    continue;
                                }
                                int i = bitMap.convertLocation(x, py);

                                if (!renderedPixels[i]) {
                                    renderedPixels[i] = true;
                                    HexColor c = renderer.execute(x, py, xRes, yRes, (double) x / xRes, (double) py / yRes, i, HexColor.fromInteger(original.pipette(i)), time);
                                    canvas[i] = c == null ? 0 : c.toRGB().toInteger();
                                    renderedAmount.getAndIncrement();
                                }
                            }
                        }


                        for (int i = canvas.length - 1; i >= 0; i--) {
                            Point2D p = bitMap.convertLocation(i);
                            renderState.tryBreak(renderID);


                            if (!renderedPixels[i]) {
                                renderedPixels[i] = true;
                                HexColor c = renderer.execute((int) p.x(), (int) p.y(), xRes, yRes, p.x() / xRes, p.y() / yRes, i, HexColor.fromInteger(original.pipette(i)), time);
                                canvas[i] = c == null ? 0 : c.toRGB().toInteger();
                                renderedAmount.getAndIncrement();
                            }
                        }

                    } catch (IllegalRenderStateException ignored) {
                        //noop
                    }
                });

                renderThreads.add(t);


            }
            renderThreads.forEach(Thread::start);

            for (Thread renderThread : renderThreads) {
                renderThread.join();
            }


        }

        if (original != tex2DOriginal) {
            original = tex2DOriginal; // revert to original canvas for reuse
        }

    }


    /**
     * 해당 셰이더가 적용되기 직전 좌표에 따른 색상을 구합니다. (왼쪽 위 : 0)
     * 적용될 셰이더가 여러 개일 경우, 이전에 사용한 모든 셰이더가 반영된 이미지를 대상으로 합니다.
     */
    public HexColor texture2D(int x, int y) {
        return HexColor.fromInteger(original.pipette(AdvancedMath.restrict(0, bitMap.getWidth() - 1, x), AdvancedMath.restrict(0, bitMap.getHeight() - 1, y)));
    }


    /**
     * 해당 셰이더가 적용되기 직전 좌표에 따른 색상을 구합니다. (왼쪽 위 : 0)
     * 적용될 셰이더가 여러 개일 경우, 이전에 사용한 모든 셰이더가 반영된 이미지를 대상으로 합니다.
     */
    public HexColor texture2D(Point2D p) {
        return texture2D((int) p.x(), (int) p.y());
    }


    public void createRenderer(ShaderRenderer renderer) throws IllegalRenderStateException{
        this.renderers.add(renderer);
        tryBreak();
    }

    public BitMap getOriginalBitMap() {
        return original;
    }

    public BitMap getBitMap() {
        return bitMap;
    }


}
