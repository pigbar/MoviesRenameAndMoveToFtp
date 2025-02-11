package com.pigbar.moviesfiles.utils;

import java.util.HashMap;
import java.util.Map;

public class FileNameUtil {
    
    public static String formatFileName(String fileName, Character replaceChar) {
        return ContentCleaner.cleanContent(fileName, replaceChar);
    }

    public static String formatFileName(String fileName) {
        return formatFileName(fileName, DEFAULT_REPLACE_CHAR);
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
