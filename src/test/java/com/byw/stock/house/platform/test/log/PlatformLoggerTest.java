package com.byw.stock.house.platform.test.log;

import com.byw.stock.house.platform.log.PlatformLogger;
import org.junit.Before;
import org.junit.Test;

import com.byw.stock.house.platform.log.PlatformLoggerConfiguration;


public class PlatformLoggerTest {

    public static PlatformLogger logger = PlatformLogger.getLogger(PlatformLoggerTest.class);

    @Before
    public void setUp() throws Exception {

        try {
            PlatformLoggerConfiguration.getInstance().initConfigurationForLogging("tss-core-cfg-logging.xml", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loggerPrint() {

        logger.info("-------------start loggerPrint---------");
        logger.debug("message");
        logger.debug("messageTest", "A", "B");
        logger.info("messageTest", "A", "B");
        logger.error("messageTest", "A", "B");
        logger.error("errorException", new Exception("errorException"));
        logger.exception(new Exception("test Exception"));
        logger.info("-------------end loggerPrint---------");
    }
}
