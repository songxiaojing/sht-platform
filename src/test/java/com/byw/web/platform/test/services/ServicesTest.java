package com.byw.web.platform.test.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.byw.web.platform.core.services.PropertyLoaderService;
import com.byw.web.platform.core.services.ServiceHelper;


public class ServicesTest {

    @Before
    public void setUp() throws Exception {

        ServiceHelper.registerService(new PropertyLoaderService(), true, false);
    }

    @Test
    public void test() throws Exception {

        SimapleServices service = new SimapleServices();
        ServiceHelper.registerService(service);

        SimapleServices findService = ServiceHelper.findService(SimapleServices.class);

        Assert.assertNotNull(findService);

        Assert.assertEquals(service.hashCode(), findService.hashCode());

        Assert.assertNotNull(findService._jmxObjectName);

        Assert.assertNotSame(findService._jmxObjectName.trim(), "");

        System.out.println(findService._jmxObjectName);
    }
}
