package com.byw.stock.house.platform.core.utils;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;



/**
 * 平台工具类.
 * 提供平台公共方法.
 *
 * @author baiyanwei
 * @title Utils
 * @package com.byw.platform.core.utils
 * @date 2014-8-8
 */
public class Utils {

    /**
     * Converts the input bytes to hex.
     */
    public static String convertToHex(byte[] data) {

        if (data == null || data.length == 0) {
            return "";
        }

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * get MD5 for content.
     *
     * @param content
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String md5String(String content) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        if (Assert.isEmptyString(content) == true) {
            return null;
        }
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5hash = new byte[32];
        md.update(content.getBytes(Constants.ISO_8859_1), 0, content.length());
        md5hash = md.digest();
        return convertToHex(md5hash);
    }


    /**
     * subtract the pat from the source
     *
     * @param source
     * @param pat
     * @return
     */
    public static String subtract(String source, String pat) {

        if (Assert.isEmptyString(source) == true) {
            return null;
        }

        if (Assert.isEmptyString(pat) == true) {
            return null;
        }

        int index = source.indexOf(pat);
        if (index == -1) {
            // not found
            return source;
        }

        StringBuffer sb = new StringBuffer();
        if (index != 0) {
            sb.append(source.substring(0, index));
        }

        if (index + pat.length() != source.length()) {
            sb.append(source.substring(index + pat.length()));
        }

        return sb.toString();
    }

    /**
     * @param clazz
     * @return
     */
    public static String getShortClassName(Class<?> clazz) {

        if (clazz == null) {
            return null;
        }

        return getShortClassName(clazz.getName());
    }

    /**
     * Get the Short class name.
     *
     * @param className
     * @return
     */
    public static String getShortClassName(String className) {

        if (Assert.isEmptyString(className) == true) {
            return null;
        }
        int lastDotIndex = className.lastIndexOf(".");
        int nameEndIndex = className.length();
        String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
        //
        return shortName;
    }

    /**
     * get 32 bit UUID String
     *
     * @return
     */
    public static String getUUID32() {

        String sn = UUID.randomUUID().toString();
        // UUID(32);
        return sn.substring(0, 8) + sn.substring(9, 13) + sn.substring(14, 18) + sn.substring(19, 23) + sn.substring(24);
    }

}
