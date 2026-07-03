package com.pigbar.moviesfiles.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class FileNameUtil {

    /**
     * Sanitise a file name to an ASCII-safe, Plex-friendly form. The extension is
     * split off first and cleaned separately so the extension dot is never merged
     * or trimmed by the separator collapsing in {@link ContentCleaner}.
     */
    public static String formatFileName(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return fileName;
        }
        String ext = getExtFromFileName(fileName);       // ".srt" or ""
        String base = getFileNameWithOutExt(fileName);   // name without the last extension
        String cleanBase = ContentCleaner.cleanContent(base);
        if (StringUtils.isEmpty(cleanBase)) {
            // Nothing survived (e.g. a wholly non-Latin name): keep the original base
            // rather than reduce the file to just its extension.
            cleanBase = base;
        }
        if (StringUtils.isEmpty(ext)) {
            return cleanBase;
        }
        String cleanExt = ContentCleaner.cleanContent(ext.substring(1));
        return StringUtils.isEmpty(cleanExt) ? cleanBase : cleanBase + "." + cleanExt;
    }

    public static String getExtFromFileName(String fileName) {
        String ext = "";
        if (StringUtils.isNoneEmpty(fileName)) {
            int idx = fileName.lastIndexOf('.');
            if (idx > 0) {
                ext = fileName.substring(idx);
            }
        }
        return ext;
    }

    public static String getFileNameWithOutExt(String fileName) {
        String resp = fileName;
        if (StringUtils.isNoneEmpty(fileName)) {
            int idx = fileName.lastIndexOf('.');
            if (idx > 0) {
                resp = fileName.substring(0, idx);
            }
        }
        return resp;
    }

    public static String getLastMemberFromPath(String path){
        String resp = path;
        if (StringUtils.isNoneEmpty(resp)) {
            while (resp.endsWith(File.separator)){
                resp = resp.substring(0, resp.length()-1);
            }
            int idx = resp.lastIndexOf(File.separator);
            if (idx > 0) {
                resp = resp.substring(idx + 1);
            }
        }
        return resp;
    }

    public static String getParentPath(String path){
        String resp = path;
        if (StringUtils.isNoneEmpty(resp)) {
            while (resp.endsWith(File.separator)){
                resp = resp.substring(0, resp.length()-1);
            }
            int idx = resp.lastIndexOf(File.separator);
            if (idx > 0) {
                resp = resp.substring(0, idx);
            }
        }
        return resp;
    }
}
