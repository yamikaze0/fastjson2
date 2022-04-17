package com.alibaba.fastjson2.v1issues.issue_1500;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Issue1583 {
    @Test
    public void test_issue() throws Exception {
        Map<String, List<String>> totalMap = new HashMap<String, List<String>>();
        for (int i = 0; i < 10; i++) {
            List<String> list = new ArrayList<String>();
            for (int j = 0; j < 2; j++) {
                list.add("list" + j);
            }
            totalMap.put("map" + i, list);
        }
        List<Map.Entry<String, List<String>>> mapList = new ArrayList<Map.Entry<String, List<String>>>(totalMap.entrySet());
        String jsonString = JSON.toJSONString(mapList);

        System.out.println(jsonString);
        List<Map.Entry<String, List<String>>> parse = JSON.parseObject(jsonString, new TypeReference<List<Map.Entry<String, List<String>>>>() {}.getType());
        System.out.println(parse);
    }
}
