package kr.merutilm.base.util;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ConsoleUtils {

    private ConsoleUtils(){

    }
    public static void printProgress(String title, long total, long current) {
        current++;
        total++;
        double percent = (current * 100.0 / total);
        String string = '\r' + title +
                String.format(" | %.3f%% [", percent) +
                String.join("", Collections.nCopies((int) percent, "=")) +
                '>' +
                String.join("", Collections.nCopies(100 - (int) percent, " ")) +
                ']' +
                String.join("", Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current == 0 ? 1 : current)), " ")) +
                String.format(" %d/%d", current, total);

        Logger.getGlobal().log(Level.INFO, string);
    }

    public static void logError(Throwable e){
        Logger.getGlobal().log(Level.WARNING, e, () -> "");
    }

    public static void profile(Runnable r){
        long pms = System.currentTimeMillis();
        r.run();
        long cms = System.currentTimeMillis();
        Logger.getGlobal().log(Level.INFO, "Time taken : {0}ms", cms - pms);
    }

}
