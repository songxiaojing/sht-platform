package com.byw.web.platform.log;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Martin Bai.
 * 
 *         logger OSGI interface for platform ,shit long. May 30,2012
 */
final public class PlatformLogger {

    // The static define attributes for high performance
    final public static String RES_FILE_STR = "Res.properties";
    final private static String REPLACE_RULE_STR = "\\{\\s*\\d+\\s*\\}";
    final private static String REPLACE_TARGET_STR = "{}";
    final private static String CLASS_NAME_TO_PATH_RULE_STR = "\\.";
    final private static String CLASS_NAME_TO_PATH_TARGET_STR = "/";

    // the logger implementation.
    private Logger logger = null;
    // the cache in RES
    private Properties pp4Format = new Properties();
    private Properties pp4Logging = new Properties();
    private HashMap<String, MessageFormat> _propertyFormatters = new HashMap<String, MessageFormat>();

    private PlatformLogger(Class<?> clazz) {

        //create logger
        logger = LoggerFactory.getLogger(clazz);
        //read the logger format.
        initResProperties(clazz);
    }

    /**
     * @param clazz
     * @return get a logging instance by class.
     */
    public static PlatformLogger getLogger(Class<?> clazz) {

        return new PlatformLogger(clazz);
    }

    /**
     * @param clazz
     *            read the RES file by class loader.
     */
    private void initResProperties(Class<?> clazz) {

        InputStream fileInput = null;
        try {
            // parser the class name to path.
            String resPath = getResourcePath(clazz, RES_FILE_STR);
            fileInput = clazz.getClassLoader().getResourceAsStream(resPath);
            if (fileInput == null) {
                // Doesn't define the RES.properties file
                return;
            }
            // loading into cache.
            pp4Format.load(fileInput);
            for (Iterator<Object> keyIter = pp4Format.keySet().iterator(); keyIter.hasNext();) {
                Object key = keyIter.next();
                Object value = pp4Format.get(key);
                if (value == null) {
                    continue;
                }
                // the Logging message formation need do like "{}{}" not
                // "{0}{1}"
                pp4Logging.put(key, value.toString().replaceAll(REPLACE_RULE_STR, REPLACE_TARGET_STR));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInput != null) {
                try {
                    fileInput.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Get the class's resource path.
     * 
     * @param clazz
     * @param suffix
     * @return
     */
    public String getResourcePath(Class<?> clazz, String suffix) {

        if (clazz == null) {
            return null;
        }
        if (suffix == null) {
            return clazz.getName().replaceAll(CLASS_NAME_TO_PATH_RULE_STR, CLASS_NAME_TO_PATH_TARGET_STR);
        }
        return clazz.getName().replaceAll(CLASS_NAME_TO_PATH_RULE_STR, CLASS_NAME_TO_PATH_TARGET_STR) + suffix;
    }

    // @Override
    public String getName() {

        return this.logger.getName();
    }

    // @Override
    // public boolean isTraceEnabled() {
    // return this.logger.isTraceEnabled();
    // }

    // @Override
    public void trace(String msg) {

        this.logger.trace(this.pp4Logging.getProperty(msg, msg));
    }

    // @Override
    // public void trace(String format, Object arg) {
    // this.logger.trace(format, arg);
    // }

    // @Override
    // public void trace(String format, Object arg1, Object arg2) {
    // this.logger.trace(format, arg1, arg2);
    // }

    // @Override
    // public void trace(String format, Object[] argArray) {
    // this.logger.trace(format, argArray);
    //
    // }

    // @Override
    public void trace(String msg, Throwable t) {

        this.logger.trace(msg, t);

    }

    // @Override
    // public boolean isTraceEnabled(Marker marker) {
    // return this.logger.isTraceEnabled(marker);
    // }

    // @Override
    // public void trace(Marker marker, String msg) {
    // this.logger.trace(marker, msg);
    //
    // }

    // @Override
    // public void trace(Marker marker, String format, Object arg) {
    // this.logger.trace(marker, format, arg);
    //
    // }

    // @Override
    // public void trace(Marker marker, String format, Object arg1, Object arg2)
    // {
    // this.logger.trace(marker, format, arg1, arg2);
    //
    // }

    // @Override
    // public void trace(Marker marker, String format, Object[] argArray) {
    // this.logger.trace(marker, format, argArray);
    //
    // }

    // @Override
    // public void trace(Marker marker, String msg, Throwable t) {
    // this.logger.trace(marker, msg, t);
    //
    // }

    // @Override
    // public boolean isDebugEnabled() {
    // return this.logger.isDebugEnabled();
    // }

    // @Override
    public void debug(String msg) {

        this.logger.debug(this.pp4Logging.getProperty(msg, msg));

    }

    // @Override
    // public void debug(String format, Object arg) {
    // this.logger.debug(format, arg);
    //
    // }

    // @Override
    // public void debug(String format, Object arg1, Object arg2) {
    // this.logger.debug(format, arg1, arg2);
    //
    // }

    // @Override
    // public void debug(String format, Object[] argArray) {
    // this.logger.debug(format, argArray);
    //
    // }

    // @Override
    public void debug(String msg, Throwable t) {

        this.logger.debug(msg, t);

    }

    // @Override
    // public boolean isDebugEnabled(Marker marker) {
    // return this.logger.isDebugEnabled(marker);
    // }

    // @Override
    // public void debug(Marker marker, String msg) {
    // this.logger.debug(marker, msg);
    //
    // }

    // @Override
    // public void debug(Marker marker, String format, Object arg) {
    // this.logger.debug(marker, format, arg);
    //
    // }

    // @Override
    // public void debug(Marker marker, String format, Object arg1, Object arg2)
    // {
    // this.logger.debug(marker, format, arg1, arg2);
    //
    // }

    // @Override
    // public void debug(Marker marker, String format, Object[] argArray) {
    // this.logger.debug(marker, format, argArray);
    //
    // }

    // @Override
    // public void debug(Marker marker, String msg, Throwable t) {
    // this.logger.debug(marker, msg, t);
    //
    // }

    // @Override
    // public boolean isInfoEnabled() {
    // return this.logger.isInfoEnabled();
    // }

    // @Override
    public void info(String msg) {

        this.logger.info(this.pp4Logging.getProperty(msg, msg));

    }

    // @Override
    // public void info(String format, Object arg) {
    // this.logger.info(format, arg);
    //
    // }

    // // @Override
    // public void info(String format, Object arg1, Object arg2) {
    // this.logger.info(format, arg1, arg2);
    //
    // }

    // // @Override
    // public void info(String format, Object[] argArray) {
    // this.logger.info(format, argArray);
    //
    // }

    // @Override
    public void info(String msg, Throwable t) {

        this.logger.info(msg, t);

    }

    // @Override
    // public boolean isInfoEnabled(Marker marker) {
    // return this.logger.isInfoEnabled(marker);
    // }

    // @Override
    // public void info(Marker marker, String msg) {
    // this.logger.info(marker, msg);
    //
    // }

    // @Override
    // public void info(Marker marker, String format, Object arg) {
    // this.logger.info(marker, format, arg);
    //
    // }

    // @Override
    // public void info(Marker marker, String format, Object arg1, Object arg2)
    // {
    // this.logger.info(marker, format, arg1, arg2);
    //
    // }

    // @Override
    // public void info(Marker marker, String format, Object[] argArray) {
    // this.logger.info(marker, format, argArray);
    //
    // }

    // @Override
    // public void info(Marker marker, String msg, Throwable t) {
    // this.logger.info(marker, msg, t);
    //
    // }

    // @Override
    // public boolean isWarnEnabled() {
    // return this.logger.isWarnEnabled();
    // }

    // @Override
    public void warn(String msg) {

        this.logger.warn(this.pp4Logging.getProperty(msg, msg));
    }

    // @Override
    // public void warn(String format, Object arg) {
    // this.logger.warn(format, arg);
    //
    // }

    // @Override
    // public void warn(String format, Object[] argArray) {
    // this.logger.warn(format, argArray);
    //
    // }

    // @Override
    // public void warn(String format, Object arg1, Object arg2) {
    // this.logger.warn(format, arg1, arg2);
    //
    // }

    // @Override
    public void warn(String msg, Throwable t) {

        this.logger.warn(msg, t);

    }

    // @Override
    // public boolean isWarnEnabled(Marker marker) {
    // return this.logger.isWarnEnabled(marker);
    // }

    // @Override
    // public void warn(Marker marker, String msg) {
    // this.logger.warn(marker, msg);
    //
    // }

    // @Override
    // public void warn(Marker marker, String format, Object arg) {
    // this.logger.warn(marker, format, arg);
    //
    // }

    // @Override
    // public void warn(Marker marker, String format, Object arg1, Object arg2)
    // {
    // this.logger.warn(marker, format, arg1, arg2);
    //
    // }

    // @Override
    // public void warn(Marker marker, String format, Object[] argArray) {
    // this.logger.warn(marker, format, argArray);
    //
    // }

    // @Override
    // public void warn(Marker marker, String msg, Throwable t) {
    // this.logger.warn(marker, msg, t);
    //
    // }

    // @Override
    // public boolean isErrorEnabled() {
    // return this.logger.isErrorEnabled();
    // }

    // @Override
    public void error(String msg) {

        this.logger.error(this.pp4Logging.getProperty(msg, msg));

    }

    // @Override
    // public void error(String format, Object arg) {
    // this.logger.error(format, arg);
    //
    // }

    // @Override
    // public void error(String format, Object arg1, Object arg2) {
    // this.logger.error(format, arg1, arg2);
    //
    // }

    // @Override
    // public void error(String format, Object[] argArray) {
    // this.logger.error(format, argArray);
    //
    // }

    // @Override
    public void error(String msg, Throwable t) {

        this.logger.error(msg, t);

    }

    // @Override
    // public boolean isErrorEnabled(Marker marker) {
    // return this.logger.isErrorEnabled(marker);
    // }

    // @Override
    // public void error(Marker marker, String msg) {
    // this.logger.error(marker, msg);
    //
    // }

    // @Override
    // public void error(Marker marker, String format, Object arg) {
    // this.logger.error(marker, format, arg);
    //
    // }

    // @Override
    // public void error(Marker marker, String format, Object arg1, Object arg2)
    // {
    // this.logger.error(marker, format, arg1, arg2);
    //
    // }

    // @Override
    // public void error(Marker marker, String format, Object[] argArray) {
    // this.logger.error(marker, format, argArray);
    //
    // }

    // @Override
    // public void error(Marker marker, String msg, Throwable t) {
    // this.logger.error(marker, msg, t);
    // }

    public void exception(Throwable t) {

        this.logger.error(t.getMessage(), t);
    }

    public void exception(String msg, Throwable t) {

        this.logger.error(msg, t);
    }

    /**
     * @param format
     * @param objects
     *            logging information with formation in ("abc{}{}","1","2");
     */
    public void info(String format, Object... objects) {

        this.logger.info(this.pp4Logging.getProperty(format, format), objects);

    }

    /**
     * @param format
     * @param objects
     *            logging error with formation in ("abc{}{}","1","2");
     */
    public void error(String format, Object... objects) {

        this.logger.error(this.pp4Logging.getProperty(format, format), objects);

    }

    /**
     * @param format
     * @param objects
     *            logging debugs with formation in ("abc{}{}","1","2");
     */
    public void debug(String format, Object... objects) {

        this.logger.debug(this.pp4Logging.getProperty(format, format), objects);

    }

    /**
     * @param format
     * @param objects
     *            logging warning with formation in ("abc{}{}","1","2");
     */
    public void warn(String format, Object... objects) {

        this.logger.warn(this.pp4Logging.getProperty(format, format), objects);

    }

    /**
     * @param format
     * @param objects
     *            logging trace with formation in ("abc{}{}","1","2");
     */
    public void trace(String format, Object... objects) {

        this.logger.trace(this.pp4Logging.getProperty(format, format), objects);

    }

    /**
     * @param format
     * @param objects
     * @return format message.
     */
    public String messageFormat(String format, Object... objects) {

        MessageFormat messageFormatter = _propertyFormatters.get(format);

        // If the formatter doesn't exist then we want to create
        // a new one. First see if we loaded the bundle
        // and then create the formatter.
        if (messageFormatter == null) {
            messageFormatter = new MessageFormat(this.pp4Format.getProperty(format, format));
            _propertyFormatters.put(format, messageFormatter);
        }

        return messageFormatter.format(objects);

    }

    /**
     * get the message format string.
     * 
     * @param format
     * @return
     */
    public String getMessageFormat(String format) {

        return this.pp4Format.getProperty(format, format);
    }
}
