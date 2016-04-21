package com.byw.stock.house.platform.test.services;

import javax.management.DynamicMBean;
import javax.xml.bind.annotation.XmlElement;

import com.byw.stock.house.platform.core.services.IPlatformService;
import com.byw.stock.house.platform.core.services.PlatformServiceInfo;
import com.byw.stock.house.platform.core.metrics.AbstractMetricMBean;


@PlatformServiceInfo(description = "SimapleServices", configurationPath = "application/services/SimapleServices/")
public class SimapleServices extends AbstractMetricMBean implements IPlatformService, DynamicMBean {

    @XmlElement(name = "jmxObjectName", defaultValue = "")
    public String _jmxObjectName = "";

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {

    }

}
