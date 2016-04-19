package com.byw.web.platform.test.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.byw.web.platform.core.services.PropertyLoaderService;
import com.byw.web.platform.core.services.PlatformServiceHelper;


public class ServicesTest {

    @Before
    public void setUp() throws Exception {

        PlatformServiceHelper.registerService(new PropertyLoaderService(), true, false);
    }

    @Test
    public void test() throws Exception {

        SimapleServices service = new SimapleServices();
        PlatformServiceHelper.registerService(service);

        SimapleServices findService = PlatformServiceHelper.findService(SimapleServices.class);

        Assert.assertNotNull(findService);

        Assert.assertEquals(service.hashCode(), findService.hashCode());

        Assert.assertNotNull(findService._jmxObjectName);

        Assert.assertNotSame(findService._jmxObjectName.trim(), "");

        System.out.println(findService._jmxObjectName);
    }
}
