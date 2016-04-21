package com.byw.stock.house.platform.core.metrics;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.byw.stock.house.platform.core.utils.Assert;


/**
 * @author Martin Bai.
 *         <p>
 *         <p>
 *         Metric Utils supplies common methods, delegate all methods of the DynamicBean Jun 1, 2012
 */
public class MetricUtils {

    final public static String JMX_TITLE = "byw.stock.platform:type=";

    /**
     * 收集Metric注解字段.
     *
     * @param clazz
     * @return
     */
    public static HashMap<String, Field> collectMetricFields(Class<?> clazz) {

        Field[] classFields = clazz.getDeclaredFields();
        HashMap<String, Field> metricFields = new HashMap<String, Field>();

        for (Field field : classFields) {
            Metric metric = field.getAnnotation(Metric.class);
            if (metric != null) {
                metricFields.put(field.getName(), field);
            }
        }

        return metricFields;
    }

    /**
     * 收集Metric注解方法.
     *
     * @param clazz
     * @return
     */
    public static HashMap<String, Method> collectMetricMethods(Class<?> clazz) {

        if (clazz == null) {
            return null;
        }

        Method[] classMethods = clazz.getDeclaredMethods();
        HashMap<String, Method> metricMethods = new HashMap<String, Method>();

        for (Method method : classMethods) {
            Metric metric = method.getAnnotation(Metric.class);
            if (metric != null) {
                metricMethods.put(method.getName(), method);
            }
        }

        return metricMethods;

    }

    /**
     * 取得指定的属性值.
     *
     * @param attribute
     * @param metricFields
     * @param instance
     * @return
     */
    public static Object getAttribute(String attribute, HashMap<String, Field> metricFields, Object instance) {

        if (Assert.isEmptyString(attribute) == true) {
            return null;
        }

        if (metricFields == null || metricFields.isEmpty() == true) {
            return null;
        }

        Field field = metricFields.get(attribute);
        if (field == null)
            return null;

        try {
            Class<?> cls = field.getClass();
            if (cls == String.class) {
                return field.get(instance);
            }
            if (cls == Long.class || cls == long.class)
                return field.getLong(instance);
            //
            if (cls == Integer.class || cls == int.class) {
                return field.getInt(instance);
            }

            if (cls == Float.class || cls == float.class) {
                return field.getFloat(instance);
            }

            if (cls == Byte.class || cls == byte.class) {
                return field.getByte(instance);
            }

            if (cls == Boolean.class || cls == boolean.class) {
                return field.getBoolean(instance);
            }

            if (cls == Short.class || cls == short.class) {
                return field.getShort(instance);
            }

            if (cls == Character.class || cls == char.class) {
                return field.getChar(instance);
            }
            //          

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 取得对应一组属性值.
     *
     * @param attributes
     * @param metricFields
     * @param instance
     * @return
     */
    public static AttributeList getAttributes(String[] attributes, HashMap<String, Field> metricFields, Object instance) {

        if (attributes == null || attributes.length == 0 || metricFields == null || metricFields.isEmpty() == false) {
            return new AttributeList();
        }

        AttributeList resultList = new AttributeList();
        for (String object : attributes) {
            resultList.add(new Attribute(object, getAttribute(object, metricFields, instance)));
        }

        return resultList;
    }

    /**
     * 取得MBean信息.
     *
     * @param mBeanInfo
     * @param instance
     * @param constructName
     * @param mBeanName
     * @param metricFields
     * @param metricMethods
     * @return
     */
    public static MBeanInfo getMBeanInfo(MBeanInfo mBeanInfo, Object instance, String constructName, String mBeanName, HashMap<String, Field> metricFields,
                                         HashMap<String, Method> metricMethods) {

        if (mBeanInfo == null) {
            Class<?> cls = instance.getClass();

            MBeanConstructorInfo mBeanConstructorInfo = new MBeanConstructorInfo(constructName, cls.getConstructors()[0]);
            MBeanAttributeInfo[] mBeanAttributeInfos = new MBeanAttributeInfo[metricFields.keySet().size()];
            Iterator<String> fieldNames = metricFields.keySet().iterator();
            for (int i = 0; fieldNames.hasNext(); i++) {
                String name = fieldNames.next();
                Field field = metricFields.get(name);
                Metric metric = field.getAnnotation(Metric.class);
                mBeanAttributeInfos[i] = new MBeanAttributeInfo(field.getName(), field.getClass().getName(), metric.description(), true, false, false);
            }

            Iterator<String> methodNames = metricMethods.keySet().iterator();
            MBeanOperationInfo[] mBeanOperationInfos = new MBeanOperationInfo[metricMethods.keySet().size()];
            for (int i = 0; methodNames.hasNext(); i++) {
                String name = methodNames.next();
                Method method = metricMethods.get(name);
                Metric metric = method.getAnnotation(Metric.class);
                mBeanOperationInfos[i] = new MBeanOperationInfo(method.getName(), metric.description(), null, method.getReturnType().getName(), MBeanOperationInfo.ACTION);
            }

            mBeanInfo = new MBeanInfo(cls.getName(), mBeanName, mBeanAttributeInfos, new MBeanConstructorInfo[]{mBeanConstructorInfo}, mBeanOperationInfos, null);
        }
        return mBeanInfo;
    }

    public static Object invoke(String actionName, Object[] params, String[] signature, Object instance, HashMap<String, Method> metricMethods) {

        if (!metricMethods.keySet().contains(actionName))
            return null;

        try {
            return metricMethods.get(actionName).invoke(instance, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param clazz
     * @param metricMBean
     * @throws Exception register a AbstractMetricMBean into MBeanServer, So the JMX client can see the metric.
     */
    public static void registerMBean(AbstractMetricMBean metricMBean) throws Exception {

        if (metricMBean == null) {
            throw new Exception("invalid Metric MBean register prarameter.");
        }
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName mbeanName = new ObjectName(JMX_TITLE + metricMBean.getClass().getName() + ",name=" + metricMBean.hashCode());
            mbs.registerMBean(metricMBean, mbeanName);
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * unregister server from MBean Server
     *
     * @param jmxObjectName
     * @param service
     */
    public static void unRegisterMBean(AbstractMetricMBean metricMBean) throws Exception {

        if (metricMBean == null) {
            throw new Exception("invalid Metric MBean register prarameter.");
        }

        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = new ObjectName(JMX_TITLE + metricMBean.getClass().getName() + ",name=" + metricMBean.hashCode());
            mBeanServer.unregisterMBean(objectName);
        } catch (Exception e) {
            throw e;
        }
    }
}
