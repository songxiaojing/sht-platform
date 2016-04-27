package com.byw.stock.house.platform.core.services;

import com.byw.stock.house.platform.log.PlatformLogger;

import java.util.HashMap;


/**
 * 
 * 系统模块服务注册工具.
 * 
 * 支持系统模块的服务注册与配置文件读取.
 * 
 * @title ServiceHelper
 * @package com.topsec.tss.core.platform.core.services
 * @author baiyanwei
 * @version
 * @date 2014-5-15
 * 
 */
final public class PlatformServiceHelper<T extends IPlatformService> {

    final private static PlatformLogger theLogger = PlatformLogger.getLogger(PlatformServiceHelper.class);
    final private static HashMap<String, IPlatformService> _platformServiceMap = new HashMap<String, IPlatformService>();

    /**
     * 注册一个平台服务.
     * 
     * @param <T>
     * @param service
     * @return
     */
    public static <T extends IPlatformService> T registerService(T service) throws Exception {

        return registerService(service, true, true);
    }

    public static <T extends IPlatformService> T registerService(T service, boolean isStartup, boolean isPropertyes) throws Exception {

        try {
            if (isPropertyes) {
                PropertyLoaderService propertyLoaderService = findService(PropertyLoaderService.class);
                // if propertyLoaderService is null, we will do nothing. Because
                // PropertyLoaderService is the first service that to be
                // registered
                if (propertyLoaderService != null) {
                    // inject the variables from configure file
                    propertyLoaderService.injectServiceProperties(service);
                }
            }
            if (isStartup == true) {
                service.start();
            }

            _platformServiceMap.put(service.getClass().getName(), service);

            //
            PlatformServiceInfo serviceAnnotation = service.getClass().getAnnotation(PlatformServiceInfo.class);
            if (serviceAnnotation == null) {
                theLogger.info("registerService", service.getClass().getName(), "");
            } else {
                theLogger.info("registerService", service.getClass().getName(), serviceAnnotation.description());
            }
        } catch (Exception e) {
            throw e;
        }
        return service;
    }

    public static <T extends IPlatformService> T findService(Class<?> clazz) {

        return findService(clazz.getName());
    }

    @SuppressWarnings("unchecked")
    public static <T extends IPlatformService> T findService(String clazz) {

        if (clazz == null || clazz.length() == 0 || _platformServiceMap.containsKey(clazz) == false) {
            return null;
        }
        return (T) (_platformServiceMap.get(clazz));
    }

    public static <T extends IPlatformService> void unregisterService(Class<IPlatformService> clazz) throws Exception {

        unregisterService(clazz.getName());
    }

    public static <T extends IPlatformService> void unregisterService(String clazz) throws Exception {

        IPlatformService service = findService(clazz);
        if (service == null) {
            return;
        }
        synchronized (_platformServiceMap) {
            _platformServiceMap.remove(clazz);
        }
        //
        service.stop();
        //
        PlatformServiceInfo serviceInfo = service.getClass().getAnnotation(PlatformServiceInfo.class);
        if (serviceInfo == null) {
            theLogger.info("unregisterService", service.getClass().getName(), "");
        } else {
            theLogger.info("unregisterService", service.getClass().getName(), serviceInfo.description());
        }
    }
}
