package org.jmulti;

public class Logger {
    public interface LogWriter {
        void write(String msg);
    }

    private static LogWriter writer = System.out::println;

    public static void log(String msg) {
        writer.write(msg);
    }

    public static void setWriter(LogWriter writer) {
        Logger.writer = writer;
    }
}
