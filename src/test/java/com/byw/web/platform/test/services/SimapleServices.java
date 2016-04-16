package com.byw.web.platform.test.services;

import javax.management.DynamicMBean;
import javax.xml.bind.annotation.XmlElement;

import com.byw.web.platform.core.metrics.AbstractMetricMBean;
import com.byw.web.platform.core.services.IService;
import com.byw.web.platform.core.services.ServiceInfo;


@ServiceInfo(description = "SimapleServices", configurationPath = "application/services/SimapleServices/")
public class SimapleServices extends AbstractMetricMBean implements IService, DynamicMBean {

    @XmlElement(name = "jmxObjectName", defaultValue = "")
    public String _jmxObjectName = "";

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {

    }

}
