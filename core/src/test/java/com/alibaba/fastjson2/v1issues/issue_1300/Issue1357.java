package com.alibaba.fastjson2.v1issues.issue_1300;

import com.alibaba.fastjson2.JSON;
import junit.framework.TestCase;

import java.time.LocalDateTime;

/**
 * Created by wenshao on 31/07/2017.
 */
public class Issue1357 extends TestCase {
    public void test_for_issue() throws Exception {

        String str = "{\"d2\":null}";
        Test2Bean b = JSON.parseObject(str,Test2Bean.class);
        System.out.println(b);
    }

    public static class Test2Bean{
        private LocalDateTime d2;

    }
}
