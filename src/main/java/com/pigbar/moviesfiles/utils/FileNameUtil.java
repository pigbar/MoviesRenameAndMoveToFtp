package com.pigbar.moviesfiles.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.pigbar.moviesfiles.utils.ContentCleaner.DEFAULT_REPLACE_CHAR;

public class FileNameUtil {
    
    public static String formatFileName(String fileName, Character replaceChar) {
        return ContentCleaner.cleanContent(fileName, replaceChar, true);
    }

    public static String formatFileName(String fileName) {
        return formatFileName(fileName, DEFAULT_REPLACE_CHAR);
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
                resp = resp.substring(resp.length()-1);
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
                resp = resp.substring(resp.length()-1);
            }
            int idx = resp.lastIndexOf(File.separator);
            if (idx > 0) {
                resp = resp.substring(0, idx);
            }
        }
        return resp;
    }
}
