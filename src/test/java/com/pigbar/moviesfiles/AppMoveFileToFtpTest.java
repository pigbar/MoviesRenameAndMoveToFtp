package com.pigbar.moviesfiles;

import com.pigbar.moviesfiles.utils.ContentCleaner;
import com.pigbar.moviesfiles.utils.FileNameUtil;
import com.pigbar.moviesfiles.utils.FtpUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;

/**
 * Unit tests for the name/path utilities used by the movie rename+move flow.
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
        assertEquals(".srt", FileNameUtil.getExtFromFileName("a.fre.srt"));
        assertEquals("", FileNameUtil.getExtFromFileName("noextension"));
    }

    public void testGetFileNameWithOutExt() {
        assertEquals("movie.1998", FileNameUtil.getFileNameWithOutExt("movie.1998.mp4"));
        assertEquals("noextension", FileNameUtil.getFileNameWithOutExt("noextension"));
    }

    // --- FileNameUtil: path member / parent, including the trailing-separator fix ---

    public void testGetLastMemberFromPath() {
        assertEquals("movie", FileNameUtil.getLastMemberFromPath(SEP + "home" + SEP + "pigbar" + SEP + "movie"));
    }

    public void testGetLastMemberFromPathTrailingSeparator() {
        assertEquals("movie", FileNameUtil.getLastMemberFromPath(SEP + "home" + SEP + "pigbar" + SEP + "movie" + SEP));
    }

    public void testGetParentPath() {
        assertEquals(SEP + "home" + SEP + "pigbar",
                FileNameUtil.getParentPath(SEP + "home" + SEP + "pigbar" + SEP + "movie"));
    }

    public void testGetParentPathTrailingSeparator() {
        assertEquals(SEP + "home" + SEP + "pigbar",
                FileNameUtil.getParentPath(SEP + "home" + SEP + "pigbar" + SEP + "movie" + SEP));
    }

    // --- FileNameUtil.formatFileName: extension-aware sanitising ---

    /** The subtitle name that crashed the FTP upload is now ASCII-safe. */
    public void testFormatFileNameAccentedSubtitle() {
        assertEquals("Francais.(Canada).(forced).fre.srt",
                FileNameUtil.formatFileName("Français (Canada) (forced).fre.srt"));
    }

    /** An already-clean YTS movie name is left untouched. */
    public void testFormatFileNameCleanMovieUnchanged() {
        String name = "Nosferatu.2024.1080p.WEBRip.x265.10bit.AAC5.1-[YTS.MX].mp4";
        assertEquals(name, FileNameUtil.formatFileName(name));
    }

    /** Spaces inside the name become dots; the extension is preserved. */
    public void testFormatFileNameSpacesToDots() {
        assertEquals("Obsession.2025.1080p.WEBRip.x265.10bit.AAC5.1-[YTS.GG.-.YTS.BZ].mp4",
                FileNameUtil.formatFileName("Obsession.2025.1080p.WEBRip.x265.10bit.AAC5.1-[YTS.GG - YTS.BZ].mp4"));
    }

    /** Known limitation: a wholly non-Latin base cannot be transliterated, so it is kept as-is. */
    public void testFormatFileNameNonLatinFallback() {
        assertEquals("日本語.mp4", FileNameUtil.formatFileName("日本語.mp4"));
    }

    // --- ContentCleaner: ASCII-safe sanitising ---

    public void testStripsDiacritics() {
        assertEquals("Zoe.cafe.resume", ContentCleaner.cleanContent("Zoë café résumé"));
    }

    public void testTransliteratesNonDecomposableLetters() {
        assertEquals("movie.ocean.aeon.sseta", ContentCleaner.cleanContent("møvie øcean æon ßeta"));
    }

    public void testTypographicPunctuation() {
        assertEquals("naive-dash.ellipsis", ContentCleaner.cleanContent("naïve—dash…ellipsis"));
    }

    public void testSeparatorsCollapseAndTrim() {
        assertEquals("Dont.Look.Up", ContentCleaner.cleanContent("Don't  Look   Up!"));
        assertEquals("", ContentCleaner.cleanContent("   ---___...   "));
    }

    public void testColonBecomesSeparator() {
        assertEquals("Mission.Impossible", ContentCleaner.cleanContent("Mission: Impossible"));
    }

    public void testKeepsStructuralPunctuation() {
        assertEquals("English.(GB).[Forced]", ContentCleaner.cleanContent("English (GB) [Forced]"));
    }

    public void testDropsNonLatinLettersKeepingAscii() {
        assertEquals("2024", ContentCleaner.cleanContent("Москва 2024"));
        assertEquals("", ContentCleaner.cleanContent("日本語"));
    }

    public void testDropsEmoji() {
        assertEquals("movie", ContentCleaner.cleanContent("🎬 movie 🍿"));
    }

    /** Output is always ASCII-only for a representative mix of scripts and symbols. */
    public void testOutputIsAlwaysAscii() {
        String[] inputs = {
                "Français (Canada).srt", "Zoë—café", "Москва", "日本語 映画",
                "møvie ßeta", "🎬 x 🍿", "a!!!b:::c"
        };
        for (String in : inputs) {
            String out = ContentCleaner.cleanContent(in);
            assertTrue("non-ASCII in output for [" + in + "] -> [" + out + "]",
                    out.chars().allMatch(c -> c < 128));
        }
    }

    public void testEmptyAndNull() {
        assertEquals("", ContentCleaner.cleanContent(""));
        assertNull(ContentCleaner.cleanContent(null));
    }

    /** An unreadable local file is skipped (returns false) instead of aborting the batch. */
    public void testUploadFileMissingLocalReturnsFalse() {
        // The missing-file path returns before the FTPClient is used, so an unconnected
        // client is fine here.
        FTPClient ftp = new FTPClient();
        assertFalse(FtpUtil.uploadFile(ftp, "/remote/dir", new File("/no/such/file.mp4")));
    }
}
