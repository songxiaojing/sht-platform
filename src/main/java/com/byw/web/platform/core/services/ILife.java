package com.byw.web.platform.core.services;

/**
 * 平台中有生命周期的服务，动作必须实现此接口
 */
public interface ILife {

    /**
     * start service
     * 
     * @throws Exception
     */
    public void start() throws Exception;

    /**
     * stop service
     * 
     * @throws Exception
     */
    public void stop() throws Exception;
}
