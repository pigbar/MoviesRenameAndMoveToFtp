package com.pigbar.moviesfiles;

import com.pigbar.moviesfiles.utils.ContentCleaner;
import com.pigbar.moviesfiles.utils.FileNameUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;

/**
 * Unit tests for the pure name/path utilities used by the movie rename+move flow.
 */
public class AppMoveFileToFtpTest
        extends TestCase {

    private static final String SEP = File.separator;

    public AppMoveFileToFtpTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(AppMoveFileToFtpTest.class);
    }

    // --- FileNameUtil: extension / basename ---

    public void testGetExtFromFileName() {
        assertEquals(".mp4", FileNameUtil.getExtFromFileName("movie.1998.mp4"));
        assertEquals(".edition", FileNameUtil.getExtFromFileName("a.b.edition"));
        assertEquals("", FileNameUtil.getExtFromFileName("noextension"));
    }

    public void testGetFileNameWithOutExt() {
        assertEquals("movie.1998", FileNameUtil.getFileNameWithOutExt("movie.1998.mp4"));
        assertEquals("noextension", FileNameUtil.getFileNameWithOutExt("noextension"));
    }

    // --- FileNameUtil: path member / parent, including the trailing-separator fix ---

    public void testGetLastMemberFromPath() {
        String path = SEP + "home" + SEP + "pigbar" + SEP + "movie";
        assertEquals("movie", FileNameUtil.getLastMemberFromPath(path));
    }

    public void testGetLastMemberFromPathTrailingSeparator() {
        String path = SEP + "home" + SEP + "pigbar" + SEP + "movie" + SEP;
        assertEquals("movie", FileNameUtil.getLastMemberFromPath(path));
    }

    public void testGetParentPath() {
        String path = SEP + "home" + SEP + "pigbar" + SEP + "movie";
        assertEquals(SEP + "home" + SEP + "pigbar", FileNameUtil.getParentPath(path));
    }

    public void testGetParentPathTrailingSeparator() {
        String path = SEP + "home" + SEP + "pigbar" + SEP + "movie" + SEP;
        assertEquals(SEP + "home" + SEP + "pigbar", FileNameUtil.getParentPath(path));
    }

    // --- ContentCleaner ---

    public void testCleanContentStripsDiacritics() {
        assertEquals("Amelie", ContentCleaner.cleanContent("Amélie", true));
    }

    public void testCleanContentMapsPunctuationToUnderscore() {
        assertEquals("hi_", ContentCleaner.cleanContent("hi!", true));
    }

    public void testCleanContentKeepsExcludedChars() {
        assertEquals("(ytx)", ContentCleaner.cleanContent("(ytx)", true));
    }

    /** keepSrc=true drops an unmapped "other" character. */
    public void testCleanContentKeepSrcDropsUnknown() {
        assertEquals("ab", ContentCleaner.cleanContent("a{b", true));
    }

    /**
     * keepSrc=false replaces an unmapped "other" character with the default replace char.
     * Guards the fix where the 2-arg overload previously ignored keepSrc and always
     * behaved as keepSrc=true.
     */
    public void testCleanContentKeepSrcFalseReplacesUnknown() {
        assertEquals("a_b", ContentCleaner.cleanContent("a{b", false));
    }
}
