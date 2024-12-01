package kr.merutilm.base.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import kr.merutilm.base.exception.IllegalRenderStateException;
import kr.merutilm.base.struct.DoubleMatrix;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.base.util.TaskManager;


public class DoubleArrayDispatcher {
    private final RenderState renderState;
    private final int renderID;
    private final DoubleMatrix bitMap;
    private DoubleMatrix original;
    protected final double initTime;
    private boolean rendered = false;
    private final AtomicInteger renderedAmount = new AtomicInteger();

    private final List<DoubleArrayRenderer> renderers = new ArrayList<>();

    public DoubleArrayDispatcher(RenderState renderState, int renderID, DoubleMatrix bitMap) throws IllegalRenderStateException{
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

  
    public synchronized void dispatch() throws InterruptedException {

        if (renderers.isEmpty()) {
            return;
        }

        if (rendered) {
            throw new IllegalStateException("Dispatcher can execute only once");
        }
        rendered = true;

        final double time = System.currentTimeMillis() / 1000.0 - this.initTime;
        final DoubleMatrix tex2DOriginal = this.original; //The elements of tex2D are unmodifiable.

        final double[] canvas = bitMap.getCanvas();


        for (DoubleArrayRenderer renderer : renderers) {

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
                                    double c = renderer.execute(x, py, xRes, yRes, (double) x / xRes, (double) py / yRes, i, original.pipette(i), time);
                                    canvas[i] = c;
                                    renderedAmount.getAndIncrement();
                                }
                            }
                        }


                        for (int i = canvas.length - 1; i >= 0; i--) {
                            Point2D p = bitMap.convertLocation(i);
                            renderState.tryBreak(renderID);


                            if (!renderedPixels[i]) {
                                renderedPixels[i] = true;
                                double c = renderer.execute((int) p.x(), (int) p.y(), xRes, yRes, p.x() / xRes, p.y() / yRes, i, original.pipette(i), time);
                                canvas[i] = c;
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


    public double texture2D(int x, int y) {
        return original.pipette(AdvancedMath.restrict(0, bitMap.getWidth() - 1, x), AdvancedMath.restrict(0, bitMap.getHeight() - 1, y));
    }


    public double texture2D(Point2D p) {
        return texture2D((int) p.x(), (int) p.y());
    }


    public void createRenderer(DoubleArrayRenderer renderer) throws IllegalRenderStateException{
        this.renderers.add(renderer);
        tryBreak();
    }

    public DoubleMatrix getOriginalBitMap() {
        return original;
    }

    public DoubleMatrix getBitMap() {
        return bitMap;
    }


}
