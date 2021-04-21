package com.lgdisplay.bigdata.api.glue.scheduler.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Map Utility.
 */
public class MapUtils {

    /**
     * 지정한 Key Value를 가진 Map을 생성한다.
     *
     * @param key   Map 생성시 초기 key
     * @param value Map 생성시 초기 Key에 대한 Value
     * @return 새로 생성한 Map
     */
    public static Map<String, String> map(String key, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    /**
     * 지정한 Key Value를 가진 Map을 생성한다.
     *
     * @param key      Map 생성시 초기 key
     * @param paramMap Map 생성시 초기 Key에 대한 Map
     * @return 새로 생성한 Map
     */
    public static Map map(String key, Map paramMap) {
        Map map = new HashMap<>();
        map.put(key, paramMap);
        return map;
    }

    /**
     * 지정한 Key Value를 가진 Map을 생성한다.
     *
     * @param key   Map 생성시 초기 key
     * @param value Map 생성시 초기 Key에 대한 Value
     * @return 새로 생성한 Map
     */
    public static Map map(String key, Boolean value) {
        Map map = new HashMap<>();
        map.put(key, value);
        return map;
    }

}
