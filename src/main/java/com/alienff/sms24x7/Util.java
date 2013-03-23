package com.alienff.sms24x7;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * <p>
 *     Util class.
 * </p>
 *
 * Created 3/23/13 10:24 AM
 * @author mike
 */
public class Util {
    private static final Logger log = LoggerFactory.getLogger(Util.class);
    private static final String UTF_8 = "utf-8";

    /**
     * URL encode string using UTF-8 character mapping
     * @param s    String to be encoded
     * @return encoded string
     * @throws UnsupportedEncodingException
     */
    public static String encode(String s) {
        try {
            return URLEncoder.encode(s, UTF_8);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            return s;
        }
    }

    static String maskCookie(String cookie) {
        if (cookie == null || cookie.length() < 2) {
            return cookie;
        } else {
            return cookie.substring(0, cookie.length() / 2) + "...";
        }
    }
}
