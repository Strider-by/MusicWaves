package by.musicwaves.util;

import java.util.HashMap;
import java.util.Map;

public class HtmlStringEscapeTool {

    private final static Map<Character, String> CHARACTERS_TO_HTML_STRING;

    static {
        CHARACTERS_TO_HTML_STRING = new HashMap<>();
        CHARACTERS_TO_HTML_STRING.put(' ', "&nbsp;");
        CHARACTERS_TO_HTML_STRING.put('<', "&lt;");
        CHARACTERS_TO_HTML_STRING.put('>', "&gt;");
        CHARACTERS_TO_HTML_STRING.put('&', "&amp;");
        CHARACTERS_TO_HTML_STRING.put('"', "&quot;");
        CHARACTERS_TO_HTML_STRING.put('\'', "&apos;");
    }

    public String escape(String text) {

        StringBuilder sb = new StringBuilder();
        String replacer;

        for (char symbol : text.toCharArray()) {
            if ((replacer = CHARACTERS_TO_HTML_STRING.get(symbol)) != null) {
                sb.append(replacer);
            }  else {
                sb.append(symbol);
            }
        }

        return sb.toString();
    }
}
