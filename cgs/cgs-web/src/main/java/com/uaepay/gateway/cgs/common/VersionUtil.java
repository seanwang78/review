package com.uaepay.gateway.cgs.common;

import org.apache.commons.lang3.StringUtils;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/4/5
 * @since 0.1
 */
public class VersionUtil {

    /**
     * v1 = 1.0.9
     * v2 = 1.0.8
     * @return 1 (v1 > v2)
     * v1 = 1.0.9
     * v2 = 1.0.9
     * @return 0 (v1 = v2)
     * v1 = 1.0.9
     * v2 = 1.0.10
     * @return -1 (v1 < v2)
     */
    public static int compare(String v1, String v2) {
        Integer vs1 = getVersion(v1);
        Integer vs2 = getVersion(v2);
        return vs1.compareTo(vs2);
    }

    public static Integer getVersion(String versionName) {
        try {
            StringBuilder sb = new StringBuilder();
            String[] split = versionName.split("\\.");
            for (int i = 0; i < 3; i++) {
                if (split.length > i) {
                    String digits = StringUtils.getDigits(split[i]);
                    sb.append(appendZeroBefore(digits, 3));
                } else {
                    sb.append("000");
                }
            }
            return Integer.parseInt(sb.toString());
        } catch (Exception e) {
        }
        return Integer.MAX_VALUE;
    }

    public static String appendZeroBefore(String origin, int length) {
        if (StringUtils.isNotBlank(origin)) {
            if (origin.length() == length) {
                return origin;
            }
            StringBuilder sb = new StringBuilder(origin);
            for (int i = origin.length(); i < length; i++) {
                sb.insert(0, "0");
            }
            return sb.toString();
        }
        return origin;
    }

    public static String getVersionDigits(String sdkVersion) {
        return StringUtils.getDigits(sdkVersion);
    }

    public static void main(String[] args) {
        System.out.println(getVersionDigits("1.0.9"));
    }
}
