package com.lgdisplay.bigdata.api.glue.scheduler.controller;

import com.lgdisplay.bigdata.api.glue.scheduler.util.MapUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class DefaultController {

    public String getValue(Map<String, String> params, String key, String defaultValue) {
        String value = params.get(key);
        if (StringUtils.hasLength(value)) {
            return value;
        }
        return defaultValue;
    }

    public Map _true(Object object) {
        Map result = new HashMap();
        if (object != null) result.put("data", object);
        result.put("success", true);
        return result;
    }

    public Map _true(Map data) {
        Map result = MapUtils.map("data", data);
        result.put("success", true);
        return result;
    }

    public Map _true() {
        return MapUtils.map("success", true);
    }

    public Map _false() {
        return MapUtils.map("success", false);
    }
}
