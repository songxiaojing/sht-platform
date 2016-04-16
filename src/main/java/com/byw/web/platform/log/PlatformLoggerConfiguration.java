package com.byw.web.platform.log;

import java.util.List;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

import com.byw.web.platform.core.utils.Assert;


/**
 * 
 * 平台日志配置工具.
 * 
 * 平台日志配置工具.
 * 
 * @title PlatformLoggerConfiguration
 * @package com.topsec.tss.platform.log
 * @author baiyanwei
 * @version
 * @date 2014-12-11
 * 
 */
public class PlatformLoggerConfiguration {

    /**
     * logger.
     */
    final private static PlatformLogger theLogger = PlatformLogger.getLogger(PlatformLoggerConfiguration.class);

    /**
     * 初始化平台日志配置.
     * 
     * @param logConfigruation
     * @param frameworkPackageList
     * @throws Exception
     */
    public static void initConfigurationForLogging(String logConfigruation, List<Class<?>> frameworkPackageList) throws Exception {

        if (logConfigruation == null || logConfigruation.trim().equals("")) {
            throw new Exception("invaild logging configuration path.");
        }
        try {
            theLogger.info("Starting configurate for " + logConfigruation);
            //
            LoggerContext logContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            //add platform logger into  getFrameworkPackages, for caller in logging recorder.
            //指定系统中PlatformLogger,ApplicationLoggingService为Logback的Framework类，
            //这样可以记录日志caller时，正确找到上层调用类
            logContext.getFrameworkPackages().add(PlatformLogger.class.getName());
            if (Assert.isEmptyCollection(frameworkPackageList) == false) {
                for (int i = 0; i < frameworkPackageList.size(); i++) {
                    logContext.getFrameworkPackages().add(frameworkPackageList.get(i).getName());
                }
            }
            //
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(logContext);
            logContext.reset();
            //
            configurator.doConfigure(PlatformLoggerConfiguration.class.getClassLoader().getResourceAsStream(logConfigruation));
            theLogger.info("complete logger configuration.");
        } catch (Exception e) {
            theLogger.exception(e);
            throw e;
        }
    }
}
