package com.byw.web.platform.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.byw.web.platform.log.PlatformLogger;


/**
 * 
 * 
 * IP工具类.
 * 
 * @title IPUtils
 * @package com.topsec.tss.platform.core.utils
 * @author ctc
 * @version
 * @date 2014-12-16
 * 
 */
public class IPUtils {

    /**
     * 日志.
     */
    final private static PlatformLogger theLogger = PlatformLogger.getLogger(IPUtils.class);

    public String ips;

    public IPUtils() {

    }

    public IPUtils(String ip) {

        ips = ip;
    }

    public Long ipScopeLength() {

        if (Assert.isEmptyString(ips) == true) {
            return 0L;
        }
        String[] ipMinMax = parseIpSegmentRange(ips);
        if (Assert.isNull(ipMinMax) == true || ipMinMax.length <= 0) {
            return 0L;
        }
        try {
            Long ipStartLong = getIPV4Value(ipMinMax[0]);
            Long ipEndLong = getIPV4Value(ipMinMax[1]);

            if (ipStartLong == 0 || ipEndLong == 0 || ipEndLong < ipStartLong) {
                return 0L;
            }

            Long ipLengt = (ipEndLong - ipStartLong) + 1;
            return ipLengt;
        } catch (Exception e) {
            theLogger.exception(e);
            return 0L;
        }
    }

    public String getStartIp() {

        if (Assert.isEmptyString(ips) == true) {
            return "";
        }
        String[] ipMinMax = parseIpSegmentRange(ips);
        if (Assert.isNull(ipMinMax) == true || ipMinMax.length <= 0) {
            return "";
        }
        return ipMinMax[0];
    }

    public static List<String> bulidIpScopeList(String ip) {

        List<String> ipsList = new ArrayList<String>();

        List<Object[]> resultList = bulidIPScopeList(ip);
        for (int i = 0; i < resultList.size(); i++) {
            Object[] object = resultList.get(i);
            ipsList.add(object[0].toString());
        }
        return ipsList;
    }

    /**
     * 取得IP段的范围.
     * 
     * @param ipSegment
     * @return
     */
    public static String[] parseIpSegmentRange(String ipSegment) {

        if (Assert.isEmptyString(ipSegment) == true) {
            return null;
        }
        if (ipSegment.indexOf("-") > -1) {
            String ipStart = ipSegment.split("-")[0];
            String ipEnd = ipSegment.split("-")[1];
            // 验证参数是否为空
            if (Assert.isEmptyString(ipStart) == true || Assert.isEmptyString(ipEnd) == true) {
                return null;
            }
            // 验证参数是否确定为IP
            if (isIpV4(ipStart) == true && isIpV4(ipEnd) == true) {
                return new String[] { ipStart, ipEnd };
            }
        } else if (ipSegment.indexOf("/") > -1) {
            String ipStart = ipSegment.split("/")[0];
            int mask = Integer.parseInt(ipSegment.split("/")[1]);
            // 验证参数是否为空
            if (Assert.isEmptyString(ipStart) == true) {
                return null;
            }
            // 验证是否为IP
            if (isIpV4(ipStart) == true) {
                String[] result = getScopeMinIpMaxIpWithMask(ipStart, mask);
                if (Assert.isNull(result) == true) {
                    return null;
                }
                return new String[] { result[0], result[1] };
            }
        } else {
            if (isIpV4(ipSegment) == true) {
                return new String[] { ipSegment, ipSegment };
            }
        }
        return null;
    }

    /**
     * 
     * 获取IP范围值.
     * 
     * <pre>
     * @param ip
     * @return Object[0](String) Object[1] Long
     * 
     * 示例：
     * bulidIPScopeList("61.235.91.9-61.235.91.11")
     * 
     * 结果:
     * 61.235.91.9,1038834441
     * 61.235.91.10,1038834442
     * 61.235.91.11,1038834443
     * </pre>
     */
    public static List<Object[]> bulidIPScopeList(String ip) {

        // 返回结果集
        List<Object[]> ipResultList = new ArrayList<Object[]>();

        try {
            if (Assert.isEmptyString(ip) == true) {
                return ipResultList;
            }
            String[] ipMinMax = parseIpSegmentRange(ip);
            if (Assert.isNull(ipMinMax) == true || ipMinMax.length <= 0) {
                return ipResultList;
            }
            String ipStart = ipMinMax[0];
            String ipEnd = ipMinMax[1];
            return bulidIPScopeList(ipStart, ipEnd);
            /*if (ip.indexOf("-") > -1) {
            	String ipStart = ip.split("-")[0];
            	String ipEnd = ip.split("-")[1];
            	// 验证参数是否为空
            	if (Assert.isEmptyString(ipStart) == true || Assert.isEmptyString(ipEnd) == true) {
            		return ipResultList;
            	}
            	// 验证参数是否确定为IP
            	if (isIpV4(ipStart) == true && isIpV4(ipEnd) == true) {
            		return bulidIPScopeList(ipStart, ipEnd);
            	}
            } else if (ip.indexOf("/") > -1) {
            	String ipStart = ip.split("/")[0];
            	int mask = Integer.parseInt(ip.split("/")[1]);
            	// 验证参数是否为空
            	if (Assert.isEmptyString(ipStart) == true) {
            		return ipResultList;
            	}
            	// 验证是否为IP
            	if (isIpV4(ipStart) == true) {
            		return bulidIPScopeList(ipStart, mask);
            	}
            } else {
            	if (isIpV4(ip) == true) {
            		ipResultList.add(new Object[] { ip, getIPV4Value(ip) });
            	}
            }*/
        } catch (Exception e) {
            theLogger.exception(e);
        }

        return ipResultList;
    }

    /**
     * 
     * Your Methods description is in here.
     * 
     * @param ip
     * @param mask
     * @return
     */
    /*public static List<Object[]> bulidIPScopeList(String ip, int mask) {

    	String[] result = getScopeMinIpMaxIpWithMask(ip, mask);
    	if (Assert.isNull(result) == true) {
    		return null;
    	}
    	theLogger.debug("构建IP范围" + ip + "/" + mask + " ipScopeMin:" + result[0] + "===" + "ipScopeMax:" + result[1]);
    	return bulidIPScopeList(result[0], result[1]);
    }*/

    /**
     * 取得IP段的开始与结束地址.
     * 
     * @param ipSegment
     * @return
     */
    public static Long[] getIpSegemntRange(String ipSegment) {

        if (Assert.isEmptyString(ipSegment) == true) {
            return null;
        }
        String[] ipRange = parseIpSegmentRange(ipSegment);
        if (Assert.isNull(ipRange) == true || ipRange.length <= 0) {
            return null;
        }
        return new Long[] { getIPV4Value(ipRange[0]), getIPV4Value(ipRange[1]) };

    }

    /**
     * 
     * Your Methods description is in here.
     * 
     * @param ipStart
     * @param ipEnd
     * @return
     */
    public static List<Object[]> bulidIPScopeList(String ipStart, String ipEnd) {

        List<Object[]> ipResultList = new ArrayList<Object[]>();

        try {
            Long ipStartLong = getIPV4Value(ipStart);
            Long ipEndLong = getIPV4Value(ipEnd);

            if (ipStartLong == 0 || ipEndLong == 0 || ipEndLong < ipStartLong) {
                return ipResultList;
            }

            Long ipCount = ipEndLong - ipStartLong;
            /*if (ipCount > 65535) {
            	return ipResultList;
            }*/
            for (int i = 0; i <= ipCount; i++) {
                String ip = getIPV4Value(ipStartLong + i);
                ipResultList.add(new Object[] { ip, ipStartLong + i });
            }
        } catch (Exception e) {
            theLogger.exception(e);
        }

        return ipResultList;
    }

    /**
     * 将IP转成LONG.
     * 
     * @param ipStr
     * @return
     */
    public static long getIPV4Value(String ipStr) {

        if (Assert.isEmptyString(ipStr) == true) {
            return 0;
        }
        String[] ipArray = ipStr.split("\\.");
        if (ipArray == null || ipArray.length != 4) {
            return 0;
        }
        try {
            long[] ipValue = new long[4];
            //
            ipValue[0] = Long.parseLong(ipArray[0]);
            ipValue[1] = Long.parseLong(ipArray[1]);
            ipValue[2] = Long.parseLong(ipArray[2]);
            ipValue[3] = Long.parseLong(ipArray[3]);
            return (ipValue[0] << 24) + (ipValue[1] << 16) + (ipValue[2] << 8) + ipValue[3];
        } catch (Exception e) {
            theLogger.exception(e);
        }
        return 0;

    }

    /**
     * 
     * 将long类型转为IP.
     * 
     * @param ipAddress
     * @return
     */
    public static String getIPV4Value(long ipAddress) {

        if (Assert.isNull(ipAddress) == true) {
            return "";
        }
        try {
            StringBuffer result = new StringBuffer();
            result.append(String.valueOf((ipAddress >>> 24)));
            result.append(".");
            result.append(String.valueOf((ipAddress & 0x00FFFFFF) >>> 16));
            result.append(".");
            result.append(String.valueOf((ipAddress & 0x0000FFFF) >>> 8));
            result.append(".");
            result.append(String.valueOf((ipAddress & 0x000000FF)));
            return result.toString();
        } catch (Exception e) {
            theLogger.exception(e);
        }
        return "";
    }

    /**
     * 
     * 获取范围ip段.
     * 
     * @param ip
     * @param mask
     * @return
     */
    private static String[] getScopeMinIpMaxIpWithMask(String ip, int mask) {

        // 验证ip,mask参数是否合格
        if (Assert.isEmptyString(ip) == true || mask < 1 || mask > 32) {
            return null;
        }
        String maskResult = getMask(mask);
        return new String[] { bulidScopeMinIP(ip, maskResult), bulidScopeMaxIP(ip, maskResult) };

    }

    /**
     * 
     * 获取范围较小IP.
     * 
     * @param ip
     * @param mask
     * @return
     */
    private static String bulidScopeMinIP(String ip, String mask) {

        try {
            String[] ips = ip.trim().split("\\.");
            String[] masks = mask.trim().split("\\.");
            int[] result = new int[4];
            for (int i = 0; i < 4; i++) {
                result[i] = Integer.parseInt(ips[i]) & Integer.parseInt(masks[i]);
            }
            return result[0] + "." + result[1] + "." + result[2] + "." + (Integer.valueOf(result[3]) + 1);
        } catch (Exception e) {
            theLogger.exception(e);
        }

        return null;
    }

    /**
     * 
     * 获取范围较大IP.
     * 
     * @param ip
     * @param mask
     * @return
     */
    private static String bulidScopeMaxIP(String ip, String mask) {

        try {
            String[] ips = ip.trim().split("\\.");
            String[] masks = mask.trim().split("\\.");
            int[] result = new int[4];

            for (int i = 0; i < 4; i++) {
                result[i] = 255 - ((Integer.parseInt(ips[i]) & Integer.parseInt(masks[i])) ^ Integer.parseInt(masks[i]));
            }
            return result[0] + "." + result[1] + "." + result[2] + "." + (result[3] - 1);
        } catch (NumberFormatException e) {
            theLogger.exception(e);
        }

        return null;
    }

    /**
     * 
     * 验证是否是IP.
     * 
     * @param value
     * @return
     */
    private static boolean isIpV4(String value) {

        String pattern = "(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])";
        return Pattern.matches(pattern, value);
    }

    /**
     * 
     * 根据子网掩码位数获取子网掩码.
     * 
     * @param masks
     * @return
     */
    private static String getMask(int masks) {

        switch (masks) {
            case 1:
                return "128.0.0.0";
            case 2:
                return "192.0.0.0";
            case 3:
                return "224.0.0.0";
            case 4:
                return "240.0.0.0";
            case 5:
                return "248.0.0.0";
            case 6:
                return "252.0.0.0";
            case 7:
                return "254.0.0.0";
            case 8:
                return "255.0.0.0";
            case 9:
                return "255.128.0.0";
            case 10:
                return "255.192.0.0";
            case 11:
                return "255.224.0.0";
            case 12:
                return "255.240.0.0";
            case 13:
                return "255.248.0.0";
            case 14:
                return "255.252.0.0";
            case 15:
                return "255.254.0.0";
            case 16:
                return "255.255.0.0";
            case 17:
                return "255.255.128.0";
            case 18:
                return "255.255.192.0";
            case 19:
                return "255.255.224.0";
            case 20:
                return "255.255.240.0";
            case 21:
                return "255.255.248.0";
            case 22:
                return "255.255.252.0";
            case 23:
                return "255.255.254.0";
            case 24:
                return "255.255.255.0";
            case 25:
                return "255.255.255.128";
            case 26:
                return "255.255.255.192";
            case 27:
                return "255.255.255.224";
            case 28:
                return "255.255.255.240";
            case 29:
                return "255.255.255.248";
            case 30:
                return "255.255.255.252";
            case 31:
                return "255.255.255.254";
            case 32:
                return "255.255.255.255";
            default:
                return null;
        }

    }
}
