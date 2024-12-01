package kr.merutilm.base.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.DoubleConsumer;

import kr.merutilm.base.exception.ThreadLockException;
import kr.merutilm.base.functions.FunctionEase;

/**
 * 작업 관리자
 */
public final class TaskManager {
    private TaskManager() {
    }

    private static final List<WaitingThread> WAITING_THREADS = new ArrayList<>();

    /**
     * 작업을 실행합니다.
     *
     * @param task 작업
     */
    public static Thread runTask(Runnable task) {
        return runTask(task, 0);
    }

    public static Thread runTask(Runnable task, long millis) {
        return runTask(task, millis, 1, 0);
    }

    public static Thread runTask(Runnable task, int repetition, long interval) {
        return runTask(task, 0, repetition, interval);

    }


    public static Thread runTask(Runnable task, long firstMillis, int repetition, long interval) {
        Runnable r = () -> {
            try {
                Thread.sleep(firstMillis);
                for (int i = 0; i < repetition; i++) {
                    task.run();
                    Thread.sleep(interval);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        Thread t = new Thread(r);
        t.start();
        return t;
    }

    /**
     * 스레드를 최대 1분동안 정지합니다.
     * 1분 이상 정지 상태를 유지할 경우 예외를 발생시킵니다.
     * 정지 상태 해제를 시도하려면 {@link TaskManager#notifyAvailableThread() 해당 기능}을 사용하십시오.
     *
     * @throws ThreadLockException 일정 기간 이상 유지 상태일 때 던져집니다
     */
    public static void lockThread(AtomicBoolean locked) {
        try {
            if (locked.get()) {
                WAITING_THREADS.add(new WaitingThread(locked, Thread.currentThread()));
                Thread.sleep(60000);
                throw new ThreadLockException("it takes over 1 minute");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * {@link TaskManager#lockThread(AtomicBoolean) 스레드 잠금}에 전달한 인수가 false 바뀌었을 경우 잠금을 해제할 수 있습니다.
     */
    public static void notifyAvailableThread() {
        for (WaitingThread waitingThread : WAITING_THREADS) {
            if (!waitingThread.waiting.get()) {
                waitingThread.thread.interrupt();
            }
        }
    }


    public static void animate(long millis, DoubleConsumer function, FunctionEase ease) throws InterruptedException {
        long start = System.currentTimeMillis();
        long end = start + millis;


        while (System.currentTimeMillis() < end) {
            function.accept(ease.apply(AdvancedMath.getRatio(start, end, System.currentTimeMillis())));
            Thread.sleep(10);
        }

        function.accept(1);
    }
    private record WaitingThread(
            AtomicBoolean waiting,
            Thread thread
    ) {

    }
}
