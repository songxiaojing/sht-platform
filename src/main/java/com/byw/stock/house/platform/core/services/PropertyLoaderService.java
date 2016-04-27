package com.byw.stock.house.platform.core.services;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.byw.stock.house.platform.core.utils.Assert;
import com.byw.stock.house.platform.core.utils.IOUtils;
import com.byw.stock.house.platform.log.PlatformLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * This server will fill the target service fields from configuration XML file.
 * <p>
 * This server will fill the target service fields from configuration XML file.
 *
 * @author baiyanwei
 * @version 1.0
 * @title PropertyLoaderService
 * @package com.topsec.tss.core.platform.core.services
 * @date 2014-5-15
 */
@PlatformServiceInfo(description = "Provides the ability to merge extension properties with configured properties.")
public class PropertyLoaderService implements IPlatformService {

    final private static PlatformLogger theLogger = PlatformLogger.getLogger(PropertyLoaderService.class);
    /**
     * service.xml配置文件的路径
     */
    final public static String SERVICE_CONFIGURATION_PATH = "app.cfg.service.path";
    /**
     * 配置文件的名称
     */
    final public static String DEFAULT_SERVICE_CONFIGURATION_PATH = "service.xml";
    final static public String EXTEND_PROPERTIES_NAME = "extendProperties";
    final static public String EXTEND_PROPERTIES_FILE_TYPE = ".properties";
    final static public String EXTEND_PROPERTIES_ELEMENT_NAME = "properties";

    private Document _userDocument = null;

    private XPath _xpath = XPathFactory.newInstance().newXPath();

    private String confidsAttributePath = "/@confids";

    private Properties _extProperty = new Properties();


    public PropertyLoaderService() {

    }

    @Override
    public void start() throws Exception {
        String cfgXmlFileName = getServiceConfigurationPath();
        if (cfgXmlFileName == null) {
            throw new Exception(SERVICE_CONFIGURATION_PATH + " is invalid.");
        }
        readDocument(cfgXmlFileName);
        loadExtendPropertyFile();
        theLogger.info("PropertyLoaderService is started~");
    }

    @Override
    public void stop() throws Exception {

    }


    /**
     * Gets the file path.
     *
     * @return
     */
    public String getServiceConfigurationPath() {

        String path = System.getProperty(SERVICE_CONFIGURATION_PATH);
        if (path == null || path.trim().length() == 0) {
            path = DEFAULT_SERVICE_CONFIGURATION_PATH;
        }
        return path;
    }

    /**
     * This method loads up the properties document. It will load two based on the file name and default file name properties.
     *
     * @param fileName
     * @param defaultName
     */
    private boolean readDocument(String fileName) {

        _userDocument = parseStream(this.getClass().getClassLoader().getResourceAsStream(fileName));
        return _userDocument != null;
    }

    /**
     * This method loads up the properties document. It will take the supplied input stream and use it as the method to load the xml document.
     *
     * @param inputStream
     */
    private boolean create(InputStream inputStream) {

        _userDocument = parseStream(inputStream);
        return _userDocument != null;
    }

    /*
     * PUBLIC DATA GETS THAT WILL USE THE DOCUMENT
     */
    private NodeList getList(String path) {

        try {
            return (NodeList) _xpath.evaluate(path, _userDocument, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            theLogger.exception(e);
        }
        return null;
    }

    /*
     * DATA GETS WITH REFERENCE ITEM.
     */
    private Object getValue(String path, Object defaultValue, QName type, Class<?> clazz, boolean firstTimeInject) {

        try {
            Node node = (Node) _xpath.evaluate(path, _userDocument, XPathConstants.NODE);
            Object object = _xpath.evaluate(path, _userDocument, type);
            object = replaceByExtendProperties(object);
            Object replaceDefaultValue = replaceByExtendProperties(defaultValue);
            if (object == null || node == null) {
                if (!firstTimeInject)
                    return null;
                if (clazz == null || clazz.equals(String.class)) {
                    return (String) replaceDefaultValue;
                } else if (clazz.equals(Long.class) == true) {
                    return new Long((String) replaceDefaultValue);
                } else if (clazz.equals(Integer.class) == true) {
                    return new Integer((String) replaceDefaultValue);
                } else if (clazz.equals(Float.class) == true) {
                    return new Float((String) replaceDefaultValue);
                } else if (clazz.equals(Double.class) == true) {
                    return new Double((String) replaceDefaultValue);
                } else if (clazz.equals(Boolean.class) == true) {
                    return new Boolean((String) replaceDefaultValue);
                } else if (clazz.equals(List.class)) {
                    String values = (String) replaceDefaultValue;
                    String[] value = values.split(",");
                    List<String> valueList = new ArrayList<String>(value.length);
                    for (int i = 0; i < value.length; i++) {
                        valueList.add(value[i]);
                    }
                    return valueList;
                } else {
                    return replaceDefaultValue;
                }
            } else if (clazz.equals(Long.class) == true) {
                return new Long(((Number) object).longValue());
            } else if (clazz.equals(Integer.class) == true) {
                return new Integer(((Number) object).intValue());
            } else if (clazz.equals(Float.class) == true) {
                return new Float(((Number) object).floatValue());
            } else if (clazz.equals(Double.class) == true) {
                return new Double(((Number) object).doubleValue());
            } else if (clazz.equals(Boolean.class) == true) {
                return new Boolean((String) object);
            } else if (clazz.equals(List.class)) {
                String values = (String) object;
                String[] value = values.split(",");
                List<String> valueList = new ArrayList<String>(value.length);
                for (int i = 0; i < value.length; i++) {
                    valueList.add(value[i]);
                }
                return valueList;
            } else {
                return (String) object;
            }
        } catch (XPathExpressionException e) {
            theLogger.exception(e);
        }
        return defaultValue;
    }

    /**
     * This method injects the properties from configuration file
     *
     * @param service
     */
    public void injectServiceProperties(IPlatformService service) {

        Class<?> clazz = service.getClass();
        PlatformServiceInfo serviceInfo = clazz.getAnnotation(PlatformServiceInfo.class);
        if (serviceInfo == null || serviceInfo.configurationPath().length() == 0) {
            return;
        }
        String configurationPath = serviceInfo.configurationPath();
        // This method will loop through the
        // variables of the object and set their values.
        // inject(service, configurationPath, true);
        injectServiceProperties(service, configurationPath);
    }

    /**
     * This method works the same ways as the one that doesn't supply the configuration path we use this method in the storage system to allow multiple service objects with different properties to be created.
     */
    public void injectServiceProperties(IPlatformService service, String configurationPath) {

        inject(service, configurationPath, true);
        injectUsingConfIds(service, configurationPath);
        // This method will loop through the
        // variables of the object and set their values.
    }

    private void injectUsingConfIds(IPlatformService service, String configurationPath) {

        MessageFormat f = new MessageFormat("//config[@id=''{0}'']");
        // List<String> ids = getAllConfIds(service);
        List<String> ids = getAllConfIds(configurationPath);
        for (String id : ids) {
            inject(service, f.format(new String[]{id}), false);
        }
    }

    private List<String> getAllConfIds(String path) {

        List<String> ids = new ArrayList<String>();
        String attributeV = null;
        try {
            attributeV = (String) _xpath.evaluate(path + confidsAttributePath, _userDocument, XPathConstants.STRING);
        } catch (XPathExpressionException e) {

        }
        if (attributeV != null && !attributeV.isEmpty()) {
            ids = Arrays.asList(attributeV.split(","));
        }
        return ids;
    }

    /**
     * Re-implement to support the multi-layer configuration
     *
     * @param object
     * @param xpathContext
     * @param firstTimeInject This is used to predict that the already set value will be over writeen by default value.
     */
    private void inject(Object object, String xpathContext, boolean firstTimeInject) {

        Class<?> clazz = object.getClass();
        // iterate to the top parent class
        while (!clazz.equals(Object.class)) {

            // go through all the declared fields
            Field[] serviceFields = clazz.getDeclaredFields();

            for (Field field : serviceFields) {

                try {
                    // get accessibility
                    boolean accessible = field.isAccessible();
                    if (!accessible) {
                        // make the private field accessible
                        field.setAccessible(true);
                    }
                    XmlElementWrapper xmlElementWrapper = field.getAnnotation(XmlElementWrapper.class);
                    if (xmlElementWrapper != null) {
                        // List object
                        XmlElement xmlElement = field.getAnnotation(XmlElement.class);
                        String actualPath = setupXPathContext(setupXPathContext(xpathContext, xmlElementWrapper.name()), xmlElement.name());
                        XPathExpression expr = _xpath.compile(actualPath);
                        NodeList nodes = (NodeList) expr.evaluate(_userDocument, XPathConstants.NODESET);
                        int length = nodes.getLength();
                        Method addMethod = field.getType().getMethod("add", Object.class);
                        for (int i = 0; i < length; i++) {
                            Object child = xmlElement.type().newInstance();
                            //check the child node type 
                            QName type = getXPathType(xmlElement.type());
                            if (customized(type)) {
                                //if is user define type.
                                inject(child, actualPath + "[" + (i + 1) + "]", firstTimeInject);
                            } else {
                                //if xmlElement's type is String,number,the basic type.
                                child = getValue(actualPath + "[" + (i + 1) + "]", xmlElement.defaultValue(), type, xmlElement.type(), firstTimeInject);
                            }
                            addMethod.invoke(field.get(object), child);
                        }
                    } else {
                        XmlElement xmlElement = field.getAnnotation(XmlElement.class);
                        if (xmlElement != null) {
                            QName type = getXPathType(xmlElement.type());
                            if (customized(type)) {
                                inject(field.get(object), setupXPathContext(xpathContext, xmlElement.name()), firstTimeInject);
                            } else {
                                Object o = getValue(setupXPathContext(xpathContext, xmlElement.name()), xmlElement.defaultValue(), type, xmlElement.type(), firstTimeInject);

                                if (o != null) {
                                    field.set(object, o);
                                }

                            }
                        } else {
                            XmlAttribute xmlAttribute = field.getAnnotation(XmlAttribute.class);
                            if (xmlAttribute != null) {
                                Object o = getValue(xpathContext + "/@" + xmlAttribute.name(), "", XPathConstants.STRING, String.class, firstTimeInject);

                                if (o != null) {
                                    field.set(object, o);
                                }
                            }
                        }

                    }

                    // restore, if we changed it.
                    if (!accessible) {
                        field.setAccessible(accessible);
                    }

                } catch (Exception e) {
                    theLogger.exception(e);
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    private QName getXPathType(Class<?> clazz) {

        if (clazz == null || clazz.equals(XmlElement.DEFAULT.class) || clazz.equals(String.class) || clazz.equals(Boolean.class) || clazz.equals(List.class)) {
            return XPathConstants.STRING;
        } else if (clazz.equals(Integer.class) || clazz.equals(Long.class) || clazz.equals(Float.class) || clazz.equals(Double.class)) {
            return XPathConstants.NUMBER;
        } else {
            return XPathConstants.NODE;
        }
    }

    private boolean customized(QName type) {

        return type.equals(XPathConstants.NODE) || type.equals(XPathConstants.NODESET);
    }

    private String setupXPathContext(String xpathContext, String xmlElement) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(xpathContext);
        if (!xpathContext.endsWith("/")) {
            stringBuffer.append("/");
        }
        stringBuffer.append(xmlElement);
        return stringBuffer.toString();
    }

    /* baiyanwei 2014-6-20 目前此方法没有外部使用
    public void loadCustomProperties(HashMap<String, String> propertiesMap, String configurationPath) {

        try {
            String value = "";
            String name = "";
            if (configurationPath != null && configurationPath.length() != 0) {
                NodeList xpathValue = (NodeList) _xpath.evaluate(configurationPath + "/properties/property", _userDocument, XPathConstants.NODESET);
                for (int index = 0; index < xpathValue.getLength(); index++) {
                    Element element = (Element) xpathValue.item(index);
                    if (element != null) {
                        name = element.getAttribute("name");
                        value = element.getAttribute("value");
                        NodeList values = element.getElementsByTagName("value");
                        if (values.getLength() == 1) {
                            //value = ((Element) values.item(0)).get;
                            value = ((Element) values.item(0)).getTextContent();
                        }

                        // Only add the properties that weren't already added.
                        if (propertiesMap.containsKey("name") == false) {
                            propertiesMap.put(name, value);
                        }
                    }
                }
            }
        } catch (XPathExpressionException e) {
            theLogger.exception(e);
        }
    }
    */
    /*
     * PRIVATE METHODS
     */
    private Document parseStream(InputStream inputStream) {

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse the stream into a document
            return documentBuilder.parse(inputStream);
        } catch (Exception e) {
            theLogger.exception(e);
        }
        return null;
    }

    /**
     * replace the ${} value from properties.
     *
     * @param valueObj
     * @return
     */
    private Object replaceByExtendProperties(Object valueObj) {

        if (valueObj == null) {
            return valueObj;
        }
        if (valueObj.getClass() != java.lang.String.class) {
            return valueObj;
        }
        String content = (String) valueObj;

        Pattern p = Pattern.compile("[$][{](.*?)[}]");
        Matcher m = p.matcher(content.toString());
        StringBuffer sb = new StringBuffer();
        boolean isHas = false;
        while (m.find()) {
            //String keyword = m.group(0);
            String target = m.group(1);
            String replaceKeyValue = this._extProperty.getProperty(target);
            if (replaceKeyValue != null) {
                m.appendReplacement(sb, replaceKeyValue.replace("\\", "/"));
            }
            isHas = true;
        }
        if (!isHas) {
            return content;
        }

        m.appendTail(sb);
        return sb.toString();
    }
    //

    /**
     * 加载扩展的属性文件内容.
     */
    private void loadExtendPropertyFile() throws Exception {

        NodeList extPropertyNodeList = _userDocument.getElementsByTagName(EXTEND_PROPERTIES_NAME);
        if (extPropertyNodeList == null || extPropertyNodeList.getLength() == 0) {
            return;
        }
        ArrayList<String> extPropertyResFilePathList = new ArrayList<String>();
        for (int i = 0; i < extPropertyNodeList.getLength(); i++) {
            Element node = (Element) extPropertyNodeList.item(i);
            NodeList sonList = node.getElementsByTagName(EXTEND_PROPERTIES_ELEMENT_NAME);
            if (sonList == null || sonList.getLength() == 0) {
                continue;
            }
            for (int j = 0; j < sonList.getLength(); j++) {
                Element sonNode = (Element) sonList.item(j);
                String extendPropertiesFileName = sonNode.getFirstChild().getNodeValue();
                if (Assert.isEmptyString(extendPropertiesFileName) == true) {
                    continue;
                }
                if (extendPropertiesFileName.endsWith(EXTEND_PROPERTIES_FILE_TYPE) == false) {
                    theLogger.error("extResFileTypeError", extendPropertiesFileName);
                    continue;
                }
                if (extPropertyResFilePathList.contains(sonNode.getFirstChild().getNodeValue()) == true) {
                    theLogger.warn("extResＤuplicate", extendPropertiesFileName);
                    continue;
                }
                extPropertyResFilePathList.add(sonNode.getFirstChild().getNodeValue());
            }

        }
        //加载系统变量到扩展中
        this._extProperty.putAll(System.getenv());
        //加载扩展配置数据
        for (int i = 0; i < extPropertyResFilePathList.size(); i++) {
            try {
                Properties one = IOUtils.createProperties(extPropertyResFilePathList.get(i));
                if (one == null) {
                    theLogger.error("loadExtResourceError", extPropertyResFilePathList.get(i));
                    continue;
                }
                _extProperty.putAll(one);
            } catch (IOException e) {
                theLogger.error("loadExtResourceError", extPropertyResFilePathList.get(i));
                theLogger.exception(e);
                continue;
            }

        }
//        theLogger.debug(this._extProperty.toString());
    }

}
