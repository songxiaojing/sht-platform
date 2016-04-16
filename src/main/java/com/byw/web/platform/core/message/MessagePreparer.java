package com.byw.web.platform.core.message;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import com.byw.web.platform.core.utils.Assert;
import com.byw.web.platform.log.PlatformLogger;


/**
 * 
 * 帮助系统中的模块方便的格式化字符串与日志输出的内容格式化.
 * 
 * 帮助系统中的模块方便的格式化字符串与日志输出的内容格式化.
 * 
 * @title MessagePreparer
 * @package com.topsec.tss.core.platform.core.message
 * @author baiyanwei
 * @version 1.0
 * @date 2014-5-16
 * 
 */
public class MessagePreparer {

    //
    //Logging Object
    //
    final private static PlatformLogger theLogger = PlatformLogger.getLogger(MessagePreparer.class);

    //
    // PRIVATE FINAL STATIC INSANCE VARIABLE
    //

    final private static String SUFFIX = "Res.properties";
    // The static define attributes for high performance
    final private static String REPLACE_RULE_STR = "\\{\\s*\\d+\\s*\\}";
    final private static String REPLACE_TARGET_STR = "{}";
    final private static String CLASS_NAME_TO_PATH_RULE_STR = "\\.";
    final private static String CLASS_NAME_TO_PATH_TARGET_STR = "/";

    /**
     * 字符串格式化.
     */
    private HashMap<String, MessageFormat> _propertyFormatters = new HashMap<String, MessageFormat>();

    /**
     * 日志输出内容格式化匹配符.
     */
    private Properties _logbackLoggingFormatters = new Properties();

    private MessagePreparer() {

    }

    /**
     * 取得实例，非单实例.
     * 
     * @param clazz
     * @return
     */
    public static MessagePreparer getMessagePreparer(Class<?> clazz) {

        MessagePreparer messagePreparer = new MessagePreparer();
        messagePreparer.LoadResProperties(clazz);
        return messagePreparer;
    }

    //
    //PUBLIC METHODS
    //
    /**
     * 将字符串进行格式化.
     * 
     * @param formatName
     * @param arguments
     * @return
     */
    public String format(String key, Object... arguments) {

        if (Assert.isEmptyString(key) == true) {
            return "";
        }

        if (this._propertyFormatters.containsKey(key) == true) {
            return _propertyFormatters.get(key).format(arguments);
        }

        //如果KEY不是之前Res.properties中定义好的，按格式化的表达重新构建一个Formatter进行格式化
        return MessageFormat.format(key, arguments);

    }

    /**
     * 日志输出的内容格式化表达式.
     * 
     * @param formatName
     * @return
     */
    public String getLogFormat(String formatName) {

        if (Assert.isEmptyString(formatName) == true) {
            return "";
        }
        return _logbackLoggingFormatters.getProperty(formatName, formatName);

    }

    /**
     * 加载Res.properties文件定义
     * 
     * @param clazz
     */
    private void LoadResProperties(Class<?> clazz) {

        InputStream fileInput = null;
        try {
            // parser the class name to path.
            String resPath = clazz.getName().replaceAll(CLASS_NAME_TO_PATH_RULE_STR, CLASS_NAME_TO_PATH_TARGET_STR) + SUFFIX;
            fileInput = clazz.getClassLoader().getResourceAsStream(resPath);
            if (fileInput == null) {
                // Doesn't define the RES.properties file
                return;
            }
            // loading into cache.
            _logbackLoggingFormatters.load(fileInput);
            for (Iterator<Object> keyIter = _logbackLoggingFormatters.keySet().iterator(); keyIter.hasNext();) {
                Object key = keyIter.next();
                Object value = _logbackLoggingFormatters.get(key);
                if (value == null) {
                    continue;
                }
                _propertyFormatters.put(key.toString(), new MessageFormat(value.toString()));
                // the Logging message formation need do like "{}{}" not "{0}{1}"
                _logbackLoggingFormatters.put(key, value.toString().replaceAll(REPLACE_RULE_STR, REPLACE_TARGET_STR));
            }
        } catch (IOException e) {
            theLogger.error(clazz.getName(), e);
        } finally {
            if (fileInput != null) {
                try {
                    fileInput.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
