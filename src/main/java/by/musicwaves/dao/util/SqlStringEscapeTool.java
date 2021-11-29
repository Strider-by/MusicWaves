package by.musicwaves.dao.util;

public class SqlStringEscapeTool {

    public static String escape(String s) {
        String result =
                s.replace("!", "!!")
                        .replace("%", "!%")
                        .replace("_", "!_")
                        .replace("[", "![");

        return result;
    }
}
