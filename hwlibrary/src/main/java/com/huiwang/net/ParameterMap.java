package com.huiwang.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ParameterMap {



    public static Map<String, String> getMap(Map<String, String> request) {
        Map<String, String> map = new HashMap<>(request);
        map.put("secondtimestamp", System.currentTimeMillis() + "");
        map.put("nonce_str", new Random().nextInt(10000) + "");
        map.put("orgno", API.orgno);
        return map;


    }
}
