package org.jmulti;

public class Locale {
    public interface Translator {
        String getMessage(String key, Object ... parameters);
    }

    private static Translator translator = String::format;

    public static void setTranslator(Translator translator) {
        Locale.translator = translator;
    }

    public static String getMessage(String key, Object ... parameters) {
        return translator.getMessage(key, parameters);
    }
}
