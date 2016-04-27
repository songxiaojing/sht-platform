package com.byw.stock.house.platform.log;

import java.util.List;

import com.byw.stock.house.platform.core.utils.Assert;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;


/**
 * 平台日志配置工具.
 * <p>
 * 平台日志配置工具.
 *
 * @author baiyanwei
 * @title PlatformLoggerConfiguration
 * @package com.topsec.tss.platform.log
 * @date 2014-12-11
 */
final public class PlatformLoggerConfiguration {

    private static PlatformLoggerConfiguration _platformLoggerConfiguration = null;

    private PlatformLoggerConfiguration() {

    }

    public static synchronized PlatformLoggerConfiguration getInstance() {
        if (_platformLoggerConfiguration == null) {
            _platformLoggerConfiguration = new PlatformLoggerConfiguration();
        }
        return _platformLoggerConfiguration;

    }

    /**
     * 初始化平台日志配置.
     *
     * @param logConfigruation
     * @param frameworkPackageList
     * @throws Exception
     */
    public synchronized void initConfigurationForLogging(String logConfigruation, List<Class<?>> frameworkPackageList) throws Exception {

        if (logConfigruation == null || logConfigruation.trim().equals("") == true) {
            throw new Exception("invaild logging configuration path.");
        }
        try {
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
            configurator.doConfigure(PlatformLoggerConfiguration.class.getClassLoader().getResourceAsStream(logConfigruation.trim()));
            System.out.println("Platform Logger is set with " + logConfigruation);
        } catch (Exception e) {
            throw e;
        }
    }
}
