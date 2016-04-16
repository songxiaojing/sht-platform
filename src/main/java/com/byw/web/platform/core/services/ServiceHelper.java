package com.byw.web.platform.core.services;

import java.util.HashMap;

import com.byw.web.platform.log.PlatformLogger;


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
public class ServiceHelper<T extends IService> {

    final private static PlatformLogger theLogger = PlatformLogger.getLogger(ServiceHelper.class);
    private static HashMap<String, IService> _registationMap = new HashMap<String, IService>();

    /**
     * register a service into OSGI frame.
     * 
     * @param <T>
     * @param service
     * @return
     */
    public static <T extends IService> T registerService(T service) throws Exception {

        return registerService(service, true, true);
    }

    public static <T extends IService> T registerService(T service, boolean isStartup, boolean isPropertyes) throws Exception {

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

            _registationMap.put(service.getClass().getName(), service);

            //
            ServiceInfo serviceAnnotation = service.getClass().getAnnotation(ServiceInfo.class);
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

    public static <T extends IService> T findService(Class<?> clazz) {

        return findService(clazz.getName());
    }

    @SuppressWarnings("unchecked")
    public static <T extends IService> T findService(String clazz) {

        if (clazz == null || clazz.length() == 0 || _registationMap.containsKey(clazz) == false) {
            return null;
        }
        return (T) (_registationMap.get(clazz));
    }

    public static <T extends IService> void unregisterService(Class<IService> clazz) throws Exception {

        unregisterService(clazz.getName());
    }

    public static <T extends IService> void unregisterService(String clazz) throws Exception {

        IService service = findService(clazz);
        if (service == null) {
            return;
        }
        synchronized (_registationMap) {
            _registationMap.remove(clazz);
        }
        //
        service.stop();
        //
        ServiceInfo serviceInfo = service.getClass().getAnnotation(ServiceInfo.class);
        if (serviceInfo == null) {
            theLogger.info("unregisterService", service.getClass().getName(), "");
        } else {
            theLogger.info("unregisterService", service.getClass().getName(), serviceInfo.description());
        }
    }
}
