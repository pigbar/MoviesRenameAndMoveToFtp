package com.pigbar.moviesfiles.utils;

import java.util.HashMap;
import java.util.Map;

public class FileNameUtil {
    public static final Map<Character, Character> CHARACTER_MAP = new HashMap<>();

    static {
        CHARACTER_MAP.put(' ', '_');
        CHARACTER_MAP.put('"', '_');
        CHARACTER_MAP.put('#', '_');
        CHARACTER_MAP.put('$', '_');
        CHARACTER_MAP.put('!', '_');
        CHARACTER_MAP.put('&', '_');
        CHARACTER_MAP.put('%', '_');
        CHARACTER_MAP.put('\'', '_');
        CHARACTER_MAP.put('*', '_');
        CHARACTER_MAP.put('+', '_');
        CHARACTER_MAP.put(',', '.');
        CHARACTER_MAP.put('/', '_');
        CHARACTER_MAP.put(':', '.');
        CHARACTER_MAP.put(';', '.');
        CHARACTER_MAP.put('<', '_');
        CHARACTER_MAP.put('=', '_');
        CHARACTER_MAP.put('>', '_');
        CHARACTER_MAP.put('?', '_');
        CHARACTER_MAP.put('@', '_');
        CHARACTER_MAP.put('\\', '_');
        CHARACTER_MAP.put('^', '_');
        CHARACTER_MAP.put('`', '_');
        CHARACTER_MAP.put('|', '_');
        CHARACTER_MAP.put('}', '_');
        CHARACTER_MAP.put('~', '_');
        CHARACTER_MAP.put('ü', 'u');
        CHARACTER_MAP.put('é', 'e');
        CHARACTER_MAP.put('â', 'a');
        CHARACTER_MAP.put('ä', 'a');
        CHARACTER_MAP.put('à', 'a');
        CHARACTER_MAP.put('å', 'a');
        CHARACTER_MAP.put('ç', 'c');
        CHARACTER_MAP.put('ê', 'e');
        CHARACTER_MAP.put('ë', 'e');
        CHARACTER_MAP.put('è', 'e');
        CHARACTER_MAP.put('ï', 'i');
        CHARACTER_MAP.put('î', 'i');
        CHARACTER_MAP.put('ì', 'i');
        CHARACTER_MAP.put('Ä', 'A');
        CHARACTER_MAP.put('Å', 'A');
        CHARACTER_MAP.put('É', 'E');
        CHARACTER_MAP.put('æ', 'e');
        CHARACTER_MAP.put('Æ', 'E');
        CHARACTER_MAP.put('ô', 'o');
        CHARACTER_MAP.put('ö', 'o');
        CHARACTER_MAP.put('ò', 'o');
        CHARACTER_MAP.put('û', 'u');
        CHARACTER_MAP.put('ù', 'u');
        CHARACTER_MAP.put('ÿ', 'y');
        CHARACTER_MAP.put('Ö', 'O');
        CHARACTER_MAP.put('Ü', 'U');
        CHARACTER_MAP.put('ø', '0');
        CHARACTER_MAP.put('£', 'L');
        CHARACTER_MAP.put('Ø', '0');
        CHARACTER_MAP.put('×', 'x');
        CHARACTER_MAP.put('ƒ', 'f');
        CHARACTER_MAP.put('á', 'a');
        CHARACTER_MAP.put('í', 'i');
        CHARACTER_MAP.put('ó', 'o');
        CHARACTER_MAP.put('ú', 'u');
        CHARACTER_MAP.put('ñ', 'n');
        CHARACTER_MAP.put('Ñ', 'N');
        CHARACTER_MAP.put('ª', 'a');
        CHARACTER_MAP.put('º', 'o');
        CHARACTER_MAP.put('¿', '_');
        CHARACTER_MAP.put('®', 'c');
        CHARACTER_MAP.put('¡', '_');
        CHARACTER_MAP.put('«', '_');
        CHARACTER_MAP.put('»', '_');
        CHARACTER_MAP.put('Á', 'A');
        CHARACTER_MAP.put('Â', 'A');
        CHARACTER_MAP.put('À', 'A');
        CHARACTER_MAP.put('©', 'C');
        CHARACTER_MAP.put('¢', 'c');
        CHARACTER_MAP.put('¥', 'Y');
        CHARACTER_MAP.put('ã', 'a');
        CHARACTER_MAP.put('Ã', 'A');
        CHARACTER_MAP.put('ð', 'o');
        CHARACTER_MAP.put('Ð', 'D');
        CHARACTER_MAP.put('Ê', 'E');
        CHARACTER_MAP.put('Ë', 'E');
        CHARACTER_MAP.put('È', 'E');
        CHARACTER_MAP.put('Í', 'I');
        CHARACTER_MAP.put('Î', 'I');
        CHARACTER_MAP.put('Ï', 'I');
        CHARACTER_MAP.put('¦', 'I');
        CHARACTER_MAP.put('Ì', 'I');
        CHARACTER_MAP.put('Ó', 'O');
        CHARACTER_MAP.put('ß', 'B');
        CHARACTER_MAP.put('Ô', 'O');
        CHARACTER_MAP.put('Ò', 'O');
        CHARACTER_MAP.put('õ', 'o');
        CHARACTER_MAP.put('Õ', 'O');
        CHARACTER_MAP.put('µ', 'u');
        CHARACTER_MAP.put('þ', 'b');
        CHARACTER_MAP.put('Þ', 'b');
        CHARACTER_MAP.put('Ú', 'U');
        CHARACTER_MAP.put('Û', 'U');
        CHARACTER_MAP.put('Ù', 'U');
        CHARACTER_MAP.put('ý', 'y');
        CHARACTER_MAP.put('Ý', 'Y');
        CHARACTER_MAP.put('¯', '_');
        CHARACTER_MAP.put('´', '\'');
        CHARACTER_MAP.put('≡', '_');
        CHARACTER_MAP.put('±', '_');
        CHARACTER_MAP.put('‗', '_');
        CHARACTER_MAP.put('÷', '_');
        CHARACTER_MAP.put('¸', '_');
        CHARACTER_MAP.put('°', '_');
        CHARACTER_MAP.put('¨', '_');
        CHARACTER_MAP.put('·', '.');
        CHARACTER_MAP.put('¹', '1');
        CHARACTER_MAP.put('³', '3');
        CHARACTER_MAP.put('²', '2');
    }

    public static String formatFileName(String fileName) {
        String formatted = fileName;
        if (fileName != null && !fileName.isEmpty()) {
            for (int idx = 0; idx < fileName.length(); idx++) {
                char ch = formatted.charAt(idx);
                Character tmpChar = CHARACTER_MAP.get(ch);
                if (tmpChar != null) {
                    formatted = formatted.replace(ch, tmpChar);
                }
            }
        }
        return formatted;
    }

    public static String getExtFromFileName(String fileName) {
        String ext = "";
        if (fileName != null && !fileName.isEmpty()) {
            int idx = fileName.lastIndexOf('.');
            if (idx > 0) {
                ext = fileName.substring(idx);
            }
        }
        return ext;
    }

    public static String getFileNameWithOutExt(String fileName) {
        String resp = fileName;
        if (fileName != null && !fileName.isEmpty()) {
            int idx = fileName.lastIndexOf('.');
            if (idx > 0) {
                resp = fileName.substring(0, idx);
            }
        }
        return resp;
    }
}
