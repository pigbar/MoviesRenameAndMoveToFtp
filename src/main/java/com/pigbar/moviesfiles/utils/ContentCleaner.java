package com.pigbar.moviesfiles.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

/**
 * Sanitises arbitrary text into an ASCII-safe, Plex-friendly form so that names
 * are accepted by any FTP server even when UTF-8 path handling is not available.
 *
 * Pipeline:
 *   1. Unicode NFD normalisation strips Latin diacritics (é→e, ç→c, ñ→n, ü→u…).
 *   2. Non-decomposable letters are transliterated (ø→o, ß→ss, æ→ae, þ→th…),
 *      together with common typographic punctuation (– — → '-', … → '.').
 *   3. [A-Za-z0-9] and the structural punctuation ()[]-_. are kept verbatim.
 *   4. Whitespace and , ; : become the canonical separator '.'.
 *   5. Every other character (remaining ASCII symbols, emoji, and any non-Latin
 *      letter we cannot transliterate) is dropped.
 *   6. Runs of the same separator (. _ -) collapse; leading/trailing separators
 *      are trimmed.
 *
 * The result is guaranteed to contain only ASCII characters.
 */
public class ContentCleaner {

    /** Canonical separator: spaces and separator-like punctuation become this. */
    public static final char SEPARATOR = '.';

    /** ASCII punctuation kept verbatim (structural chars common in release names). */
    private static final String KEEP_PUNCT = "()[]-_.";

    /** Letters/punctuation that NFD cannot fold to ASCII; transliterated explicitly. */
    private static final Map<Character, String> TRANSLIT = new HashMap<>();

    static {
        // Non-decomposable Latin letters.
        TRANSLIT.put('ø', "o");  TRANSLIT.put('Ø', "O");
        TRANSLIT.put('æ', "ae"); TRANSLIT.put('Æ', "AE");
        TRANSLIT.put('œ', "oe"); TRANSLIT.put('Œ', "OE");
        TRANSLIT.put('ß', "ss");
        TRANSLIT.put('ð', "d");  TRANSLIT.put('Ð', "D");
        TRANSLIT.put('þ', "th"); TRANSLIT.put('Þ', "Th");
        TRANSLIT.put('đ', "d");  TRANSLIT.put('Đ', "D");
        TRANSLIT.put('ł', "l");  TRANSLIT.put('Ł', "L");
        TRANSLIT.put('ħ', "h");  TRANSLIT.put('Ħ', "H");
        TRANSLIT.put('ı', "i");  TRANSLIT.put('İ', "I");
        TRANSLIT.put('ŋ', "n");  TRANSLIT.put('Ŋ', "N");
        TRANSLIT.put('ĸ', "k");  TRANSLIT.put('ſ', "s");
        // Common typographic punctuation -> ASCII equivalents.
        TRANSLIT.put('–', "-");  TRANSLIT.put('—', "-"); TRANSLIT.put('―', "-");
        TRANSLIT.put('…', ".");  TRANSLIT.put('·', "."); TRANSLIT.put('•', ".");
    }

    private ContentCleaner() {
    }

    public static String cleanContent(String content) {
        if (StringUtils.isEmpty(content)) {
            return content;
        }

        String normalized = Normalizer.normalize(content, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        StringBuilder out = new StringBuilder(normalized.length());
        normalized.codePoints().forEach(cp -> {
            if ((cp >= '0' && cp <= '9') || (cp >= 'A' && cp <= 'Z') || (cp >= 'a' && cp <= 'z')) {
                out.append((char) cp);                         // ASCII alphanumeric: keep
            } else if (cp == ',' || cp == ';' || cp == ':'
                    || Character.isWhitespace(cp) || Character.isSpaceChar(cp)) {
                out.append(SEPARATOR);                          // separator-like: '.'
            } else if (cp < 128 && KEEP_PUNCT.indexOf(cp) >= 0) {
                out.append((char) cp);                          // structural punctuation: keep
            } else if (cp <= 0xFFFF && TRANSLIT.containsKey((char) cp)) {
                out.append(TRANSLIT.get((char) cp));            // transliterate to ASCII
            }
            // else: drop (other ASCII symbols, emoji, non-transliterable letters)
        });

        String s = out.toString()
                .replaceAll("\\.{2,}", ".")
                .replaceAll("_{2,}", "_")
                .replaceAll("-{2,}", "-");
        return s.replaceAll("^[._-]+", "").replaceAll("[._-]+$", "");
    }
}
