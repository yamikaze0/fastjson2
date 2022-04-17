package com.alibaba.fastjson2.v1issues.issue_3300;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.HashMap;

public class Issue3326 extends TestCase {
    public void test_for_issue() throws Exception {
        HashMap<String, Number> map = JSON.parseObject("{\"id\":10.0}"
                , new TypeReference<HashMap<String, Number>>() {
                    }.getType()
                );
        assertEquals(BigDecimal.valueOf(10.0), map.get("id"));
    }
}
