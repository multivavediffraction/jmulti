package org.jmulti;

import org.jetbrains.annotations.NotNull;

public class Logger {
    public interface LogWriter {
        void write(String msg);
        default void error(String msg) {write(msg);}
        default void error(@NotNull Exception ex) {error(ex.getMessage());}
        default void resetProgress() {}
        default void incrementProgress(long inc) {}
        default void incrementProgress() {
            incrementProgress(1);
        }
        default void setMaxProgress(long max) {}
        default void setProgressComplete() {}
    }

    private static LogWriter writer = System.out::println;

    public static void log(String msg) {
        writer.write(msg);
    }

    public static void error(String msg) {
        writer.error(msg);
    }

    public static void error(@NotNull Exception ex) {
        writer.error(ex);
    }
    public static void setWriter(LogWriter writer) {
        Logger.writer = writer;
    }

    public static void setMaxProgress(long max) {
        Logger.writer.setMaxProgress(max);
    }

    public static void resetProgress() {
        Logger.writer.resetProgress();
    }

    public static void incrementProgress() {
        Logger.writer.incrementProgress();
    }

    public static void incrementProgress(long inc) {
        Logger.writer.incrementProgress(inc);
    }

    public static void setProgressComplete() {
        Logger.writer.setProgressComplete();
    }
}
