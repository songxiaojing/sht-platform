package com.byw.web.platform.core.utils;

import java.util.Collection;
import java.util.Map;


/**
 * @author baiyanwei Jul 13, 2013 our own assertion class that is useful for failing fast and for eliminating
 */
public final class Assert {

    /**
     * A private constructor to block instantiation.
     */
    private Assert() {

    }


    /**
     * check one string is empty or not empty:null,empty String,no letter.
     * 
     * @param s
     */
    final public static boolean isEmptyString(String s) {

        if (null == s || s.length() == 0 || s.trim().length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * check one collection is empty or not
     * 
     * @param c
     * @return
     */
    final public static <T extends Object> boolean isEmptyCollection(Collection<?> c) {

        if (null == c || c.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * check one Map is empty or not
     * 
     * @param c
     * @return
     */
    final public static <T extends Object> boolean isEmptyMap(Map<?, ?> m) {

        if (null == m || m.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Tests whether or not the url has a http or https prefix, if it does returns true else returns false.
     *
     * @param url
     * @return
     */
    final public static boolean isHttpPrefix(String url) {

        if (Assert.isEmptyString(url) == true) {
            return false;
        }

        return url.toLowerCase().startsWith("http://") == true || url.toLowerCase().startsWith("https://") == true;
    }
}
